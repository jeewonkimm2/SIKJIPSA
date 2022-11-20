package com.example.sikjipsa.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sikjipsa.R
import com.example.sikjipsa.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment() {

    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null
    var mainView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()
        //RecyclerView와 어댑터 연결
        mainView = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)

        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

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
            var viewholder = (holder as CustomViewHolder).itemView
            //id
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![position].userId

            //Image (Glide)
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_imageview_content)

            //Explain
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![position].explain

            //Likes
            //viewholder.detailviewitem_favoritecounter_textview.text = "Likes "+ contentDTOs!![position].favoriteCount

            //ProfileImage
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_profile_image)


            /*//나중거
            firestore?.collection("profileImages")
                ?.document(contentDTOs[position].uid!!)
                ?.get()
                ?.addOnCompleteListener { task ->
                    if(task.isSuccessful){

                        var url = task.result?.get("image")

                        if (url != null) {
                            Glide.with(holder.itemView.context).load(url)
                                .apply(RequestOptions().circleCrop())
                                .into(viewholder.detailviewitem_profile_image)
                        }

                    }
                }

            // 좋아요 버튼에 이벤트 추가
            viewholder.detailviewitem_favorite_imageView.setOnClickListener {
                favoriteEvent(position)
            }

            // 계정 이미지 눌렀을 때 (프로필로 이동)
            viewholder.detailviewitem_profile_image.setOnClickListener {
                profilemove(position)
            }

            // 계정 이름 눌렀을 때 (프로필로 이동)
            viewholder.detailviewitem_profile_textView.setOnClickListener {
                profilemove(position)
            }

            // 댓글 버튼 눌렀을 때
            viewholder.detailviewitem_comment_imageView.setOnClickListener { view ->
                var intent = Intent(view.context,CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position])
                intent.putExtra("destinationUid",contentDTOs[position].uid)
                startActivity(intent)
            }

            if(contentDTOs!![position].favorites.containsKey(uid)){
                // 좋아요 버튼이 눌려 있을 때
                viewholder.detailviewitem_favorite_imageView.setImageResource(R.drawable.ic_favorite)
            }
            else{
                // 좋아요 버튼이 눌려 있지 않을 때
                viewholder.detailviewitem_favorite_imageView.setImageResource(R.drawable.ic_favorite_border)
            }*/
        }

        //지원이가..? 지우는 건가...??
/*    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {


            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                //Sometimes, This code return null of querySnapshot when it signout
                if(querySnapshot == null) return@addSnapshotListener

                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            TODO("Not yet implemented")
        }

*/

    }
}
