package com.example.criconnect.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.criconnect.Adapters.MyFragmentAdapter
import com.example.criconnect.databinding.FragmentTeamStatisticsBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class TeamStatisticsFragment : Fragment() {
    lateinit var binding : FragmentTeamStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTeamStatisticsBinding.inflate(layoutInflater,container,false)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Team Statistics"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("AI Recommendation"))

        val fragmentManager =
            requireActivity().supportFragmentManager // Use requireActivity() to get the activity's fragment manager

        val adapter = MyFragmentAdapter(fragmentManager, lifecycle)
        binding.viewpager2.setAdapter(adapter)

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewpager2.setCurrentItem(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return binding.root
    }

 }