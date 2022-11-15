package com.example.sikjipsa.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sikjipsa.R
import com.example.sikjipsa.databinding.FragmentGridBinding
import kotlinx.android.synthetic.main.fragment_grid.*

class GridFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_grid,container,false)
        return inflater.inflate(R.layout.fragment_grid, container, false)

    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        image_load_btn.setOnClickListener{
//            var intent = Intent(getActivity(), SearchActivity::class.java)
//            startActivity(intent)
//        }
//    }



}
