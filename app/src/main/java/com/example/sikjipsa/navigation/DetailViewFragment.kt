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

    var auth = FirebaseAuth.getInstance()
    private lateinit var mDBRef: DatabaseReference

    var uid : String? = null
    var nickname : String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        mDBRef = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser

        uid = FirebaseAuth.getInstance().currentUser?.uid
        firestore = FirebaseFirestore.getInstance()


        mDBRef.child("user").child(uid.toString()).child("nickname").get().addOnSuccessListener {
            nickname = it.value.toString()
            Log.d("nickname","$nickname")
        }
        // Connecting RecyclerView to Adapter, LaoutManager
        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

        // Output posts in reverse order
        (view.detailviewfragment_recyclerview.layoutManager as LinearLayoutManager).reverseLayout = true
        (view.detailviewfragment_recyclerview.layoutManager as LinearLayoutManager).stackFromEnd = true
        // Output to the top
        view.detailviewfragment_recyclerview?.smoothScrollToPosition(0)
        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf() // Contents of post values
        var contentUidList: ArrayList<String> = arrayListOf()  // Current user's uid values
        // Initializing and bring data from firestore
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
                    // Refresh
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

            // Casting holder values into CustomViewHolder
            var viewholder = (holder as CustomViewHolder).itemView
            // id(nickname)
            viewholder.detailviewitem_profile_textview.text = contentDTOs!![position].nickname

            // Image (Glide)
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_imageview_content)

            // Explain
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![position].explain

            // Likes
            viewholder.detailviewitem_favoritecounter_textview.text = "Likes "+ contentDTOs!![position].favoriteCount

            // ProfileImage
            val fs = FirebaseStorage.getInstance()
            fs.getReference().child("profilepic").child(contentDTOs!![position].uid.toString()).downloadUrl.addOnSuccessListener {
                var imageUrl = it
                Glide.with(holder.itemView.context).load(imageUrl).apply(RequestOptions().circleCrop()).into(viewholder.detailviewitem_profile_image)
            }

            // code when the button is clicked
            viewholder.detailviewitem_favorite_imageview.setOnClickListener{
                favoriteEvent(position)
            }

            // code when page load
            if(contentDTOs!![position].favorites.containsKey(uid)){
                // When clicking likes
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_baseline_favorite_24)

           }else{
                // When no-clicking likes
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }


            // press the comment button, intent CommentActivity
            viewholder.detailviewitem_comment_imageview.setOnClickListener { view ->
                var intent = Intent(view.context, CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position])

                startActivity(intent)
            }


        }
        // Connect with Firestore for favorite event
        fun favoriteEvent(position: Int){
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])

            firestore?.runTransaction { transaction->
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid)){
                    // Cancel like when it's already on
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)

                }else {
                    // Put Like
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                }
                transaction.set(tsDoc,contentDTO)
            }

        }
    }
}
