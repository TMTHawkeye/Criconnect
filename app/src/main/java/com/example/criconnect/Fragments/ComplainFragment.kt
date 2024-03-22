package com.example.criconnect.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.criconnect.Adapters.PlayerAdapter
import com.example.criconnect.ModelClasses.DataClass
import com.example.criconnect.R
import com.example.criconnect.databinding.FragmentComplainBinding
 import java.util.Locale

class ComplainFragment : Fragment() {
    lateinit var binding : FragmentComplainBinding

    private var dataList: ArrayList<DataClass> =  ArrayList()
    private var adapter: PlayerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentComplainBinding.inflate(layoutInflater,container,false)

        binding.search.clearFocus()
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                searchList(newText)
                return true
            }
        })

//        val gridLayoutManager = GridLayoutManager(requireActivity(), 1)
//        binding.recyclerView.setLayoutManager(gridLayoutManager)
//
//        var androidData = DataClass("Asim", R.string.camera, "Baller", R.drawable.personal1)
//        dataList.add(androidData)
//
//        androidData = DataClass("Uzair", R.string.recyclerview, "Batsman", R.drawable.personal2)
//        dataList.add(androidData)
//
//        androidData = DataClass("Ayesha", R.string.date, "Baller", R.drawable.personal3)
//        dataList.add(androidData)
//
//        androidData = DataClass("Shahab", R.string.edit, "Batsman", R.drawable.personal4)
//        dataList.add(androidData)
//
//
//        adapter = PlayerAdapter(requireActivity(), dataList)

        binding.recyclerView.setAdapter(adapter)
        return binding.root
    }

//    private fun searchList(text: String) {
//        val dataSearchList: MutableList<DataClass> = java.util.ArrayList()
//        for (data in dataList) {
//            if (data.dataTitle.toLowerCase().contains(text.lowercase(Locale.getDefault()))) {
//                dataSearchList.add(data)
//            }
//        }
//        if (dataSearchList.isEmpty()) {
//            Toast.makeText(requireContext(), "Not Found", Toast.LENGTH_SHORT).show()
//        } else {
//            adapter?.setSearchList(dataSearchList)
//        }
//    }

}