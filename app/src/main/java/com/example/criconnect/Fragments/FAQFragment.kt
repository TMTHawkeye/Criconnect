package com.example.criconnect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.criconnect.R
import com.example.criconnect.databinding.FragmentFAQBinding

class FAQFragment : Fragment() {
    lateinit var binding : FragmentFAQBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFAQBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

 }