package com.example.sikjipsa.navigation

import android.content.Intent
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
import com.example.sikjipsa.CommentActivity
import com.example.sikjipsa.R
import com.example.sikjipsa.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*


class SearchResultFragment : Fragment() {

    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null
    var mainView: View? = null
    //Realtime DB object
    private lateinit var mDBRef: DatabaseReference
    //    User's uid
    var uid : String? = null
    //    User's nickname
    var nickname : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        firestore = FirebaseFirestore.getInstance()
        mDBRef = FirebaseDatabase.getInstance().reference
        //        Obtaining current user
        user = FirebaseAuth.getInstance().currentUser
        //        Obtaining current user's uid
        uid = FirebaseAuth.getInstance().currentUser?.uid

//        Initializing current user's nickname
        mDBRef.child("user").child(uid.toString()).child("nickname").get().addOnSuccessListener {
            nickname = it.value.toString()
        }

//        Connecting RecyclerView and adapter
        mainView = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

        //Setting value for order of recyclerView
        (view.detailviewfragment_recyclerview.layoutManager as LinearLayoutManager).reverseLayout = true
        (view.detailviewfragment_recyclerview.layoutManager as LinearLayoutManager).stackFromEnd = true
        view.detailviewfragment_recyclerview?.smoothScrollToPosition(0)
        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        //        Contents of post values
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        //        Current user's uid values
        var contentUidList: ArrayList<String> = arrayListOf()

        //        Initializing and bring data from firestore
        init {
            var a: String? = arguments?.getString("keyword")
            firestore?.collection("images")?.whereEqualTo("explain","$a")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    Log.d("uidcheck","$querySnapshot")
                    contentDTOs.clear()
                    contentUidList.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
//                    Refresh
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
            //Casting holder value to CustomViewHolder
            var viewholder = (holder as CustomViewHolder).itemView
            //Setting current user's current Nickname
            viewholder.detailviewitem_profile_textview.text = contentDTOs!![position].nickname
            //Setting Image (Glide)
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_imageview_content)
            //Setting Explain(Contents of post)
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![position].explain
            //Setting Likes
            viewholder.detailviewitem_favoritecounter_textview.text = "Likes "+ contentDTOs!![position].favoriteCount
            //Setting ProfileImage
            val fs = FirebaseStorage.getInstance()
            fs.getReference().child("profilepic").child(contentDTOs!![position].uid.toString()).downloadUrl.addOnSuccessListener {
                var imageUrl = it
                Glide.with(holder.itemView.context).load(imageUrl).apply(RequestOptions().circleCrop()).into(viewholder.detailviewitem_profile_image)
            }
            //Event when like button is clicked
            viewholder.detailviewitem_favorite_imageview.setOnClickListener{
                favoriteEvent(position)
            }
            //Page loading
            if(contentDTOs!![position].favorites.containsKey(uid)){
                //When clicking likes
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_baseline_favorite_24)
            }else{
                //When not clicking likes
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
            //Comment for each post
            viewholder.detailviewitem_comment_imageview.setOnClickListener { view ->
                var intent = Intent(view.context, CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position])
                //intent.putExtra("destinationUid",contentDTOs[position].uid)
                startActivity(intent)
            }
        }

        //        Function regarding "Liking post" event
        fun favoriteEvent(position: Int){
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction->
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
                if(contentDTO!!.favorites.containsKey(uid)){
                    //Cancelling liking when the post is already liked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)
                }else {
                    //Putting Liking
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                }
                transaction.set(tsDoc,contentDTO)
            }

        }
    }//inner class is finished


}