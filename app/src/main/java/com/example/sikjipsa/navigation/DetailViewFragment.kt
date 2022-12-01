package com.example.sikjipsa.navigation

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.sikjipsa.CommentActivity
import com.example.sikjipsa.R
import com.example.sikjipsa.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*


class DetailViewFragment : Fragment() {

    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null
    var mainView: View? = null
    //    Firebase 객체
//    lateinit var mAuth: FirebaseAuth
    var auth = FirebaseAuth.getInstance()
    //    DB객체
    private lateinit var mDBRef: DatabaseReference

    //수오가 추가(유저 아이디)
    var uid : String? = null
    var nickname : String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //RecyclerView와 어댑터 연결
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        mDBRef = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser
//        수오가 추가(유저 아이디 값)
        uid = FirebaseAuth.getInstance().currentUser?.uid
        firestore = FirebaseFirestore.getInstance()

//        nickname 지원추가
        mDBRef.child("user").child(uid.toString()).child("nickname").get().addOnSuccessListener {
            nickname = it.value.toString()
            Log.d("nickname","$nickname")
        }

        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

        //게시글 역순으로 출력
        (view.detailviewfragment_recyclerview.layoutManager as LinearLayoutManager).reverseLayout = true
        (view.detailviewfragment_recyclerview.layoutManager as LinearLayoutManager).stackFromEnd = true
        //최상단으로 출력
        view.detailviewfragment_recyclerview?.smoothScrollToPosition(0)
        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf() //게시글 담음
        var contentUidList: ArrayList<String> = arrayListOf()  //사용자 uid 담음

        init {
            firestore?.collection("images")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    // 새로고침
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail, p0, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            //holder 값을 CustomViewHolder에 캐스팅
            var viewholder = (holder as CustomViewHolder).itemView
            //id > 여기가 총체적 난국이여... 게시글을 등록할 때 해당하는 유저가 들어가려면 그냥 닉네임으로 넣는게 아닌 것 같음.. 모르겠음
            viewholder.detailviewitem_profile_textview.text = contentDTOs!![position].userId

            //viewholder.detailviewitem_profile_textview.text = contentDTOs!![position].nickname

            //Image (Glide)
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_imageview_content)

            //Explain
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![position].explain

            //Likes
            viewholder.detailviewitem_favoritecounter_textview.text = "Likes "+ contentDTOs!![position].favoriteCount

            //ProfileImage
            val fs = FirebaseStorage.getInstance()
            fs.getReference().child("profilepic").child(contentDTOs!![position].uid.toString()).downloadUrl.addOnSuccessListener {
                var imageUrl = it
                Glide.with(holder.itemView.context).load(imageUrl).apply(RequestOptions().circleCrop()).into(viewholder.detailviewitem_profile_image)
            }

            //code when the button is clicked
            viewholder.detailviewitem_favorite_imageview.setOnClickListener{
                favoriteEvent(position)
            }

            //code when page load
            if(contentDTOs!![position].favorites.containsKey(uid)){
                //좋아요 클릭된 경우
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_baseline_favorite_24)

           }else{
               //좋아요 클릭 안된 경우
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }


            // 댓글 버튼 눌렀을 때
            viewholder.detailviewitem_comment_imageview.setOnClickListener { view ->
                var intent = Intent(view.context, CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position])
                //intent.putExtra("destinationUid",contentDTOs[position].uid)
                startActivity(intent)
            }


        }
        //좋아요
        fun favoriteEvent(position: Int){
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])

            firestore?.runTransaction { transaction->
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid)){
                    //이미 좋아요가 되어있을 때 좋아요 취소
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)

                }else {
                    //좋아요 넣기
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                }
                transaction.set(tsDoc,contentDTO)
            }

        }
    }
}
