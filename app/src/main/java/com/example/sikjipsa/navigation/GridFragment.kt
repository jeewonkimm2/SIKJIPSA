package com.example.sikjipsa.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sikjipsa.R
import kotlinx.android.synthetic.main.fragment_grid.image_load_btn

class GridFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        왜안될까?
        fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            image_load_btn.setOnClickListener{
                activity?.let{
                    Log.d("ITM","클릭")
                    val intent = Intent(getActivity(), SearchActivity::class.java)
                    Log.d("ITM","클릭1")
                    startActivity(intent)
                }
            }
        }

        return inflater.inflate(R.layout.fragment_grid, container, false)
    }




}

