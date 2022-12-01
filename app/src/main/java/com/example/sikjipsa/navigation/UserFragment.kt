package com.example.sikjipsa.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sikjipsa.*
import com.example.sikjipsa.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class UserFragment : Fragment(){
    //수오 유저 로그아웃
    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid : String? = null
    var auth: FirebaseAuth? = null
    var currentUserId: String? = null
    var mDBRef: DatabaseReference = FirebaseDatabase.getInstance().reference


    private var editBtn: Button? = null
    private var plantBtn: Button? = null
    private var watering: Button? = null
    private var mypostBtn: Button? = null
    private var name: String? = null
//    private var profile: ImageView? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //지훈 mypage view이름 수오가 fragmentView로 바꿈
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)


        //수오
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = auth?.currentUser?.uid

        mDBRef = FirebaseDatabase.getInstance().reference
        var nickname : String? = null
        //        닉네임 정보 받아옴 -> jigglypuff설정필요
        mDBRef.child("user").child(uid.toString()).child("nickname").get().addOnSuccessListener {
            nickname = it.value.toString()
            Log.d("nickname","$nickname")
            fragmentView?.name?.text = nickname
        }

        //if(uid == currentUserId){
            //내 계정일 때는 로그아웃
            fragmentView?.button_signout?.setOnClickListener{
                auth?.signOut()
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        //}
        
//        프로필편집 화면 전환
        name = fragmentView?.findViewById<TextView>(R.id.name).toString()
//        profile = fragmentView?.findViewById<ImageView>(R.id.profile)
        editBtn = fragmentView?.findViewById(R.id.editBtn)
        plantBtn = fragmentView?.findViewById(R.id.myplantBtn)
        watering = fragmentView?.findViewById(R.id.wateringBtn)
        mypostBtn = fragmentView?.findViewById(R.id.mypostBtn)

        //ProfileImage
/*        val fs = FirebaseStorage.getInstance()
        fs.getReference().child("profilepic").downloadUrl.addOnSuccessListener { it ->
            var imageUrl = it
            profile?.setImageURI(imageUrl)
        }*/



        editBtn?.setOnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        }

        plantBtn?.setOnClickListener{
            startActivity(Intent(context, myPlantActivity::class.java))
        }

        mypostBtn?.setOnClickListener {
            startActivity(Intent(context, MyPostActivity::class.java))
        }

        watering?.setOnClickListener{
            startActivity(Intent(context, WateringActivity::class.java))
        }

/*        var contentDTOs: ArrayList<ContentDTO> = arrayListOf() //게시글 담음
        var contentUidList: ArrayList<String> = arrayListOf()  //사용자 uid 담음

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
                }*/



        return fragmentView


    }

/*    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            var view = LayoutInflater.from(p0.context).inflate(R.layout.fragment_user, p0, false)
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
            //viewholder.detailviewitem_profile_textview.text = contentDTOs!![position].userId
            //viewholder.detailviewitem_profile_textview.text = contentDTOs!![position].nickname
            viewholder.textView2.text = contentDTOs!![position].nickname

            //ProfileImage
            val fs = FirebaseStorage.getInstance()
            fs.getReference().child("profilepic")
                .child(contentDTOs!![position].uid.toString()).downloadUrl.addOnSuccessListener {
                var imageUrl = it
                Glide.with(holder.itemView.context).load(imageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(viewholder.profile)
            }
        }
    }*/
}