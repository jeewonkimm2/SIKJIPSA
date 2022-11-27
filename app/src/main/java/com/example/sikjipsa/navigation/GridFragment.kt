package com.example.sikjipsa.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sikjipsa.databinding.FragmentGridBinding
import kotlinx.android.synthetic.main.activity_search.*

class GridFragment : Fragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {




        val binding = FragmentGridBinding.inflate(layoutInflater)


        return binding.root




    }




}

