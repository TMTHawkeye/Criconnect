package com.example.criconnect.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.criconnect.Adapters.TournamentHandlingAdapter
import com.example.criconnect.databinding.FragmentOrganizerTornamentHandlingBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class OrganizerTornamentHandlingFragment : Fragment() {
    lateinit var binding: FragmentOrganizerTornamentHandlingBinding
    private var adapter: TournamentHandlingAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentOrganizerTornamentHandlingBinding.inflate(layoutInflater, container, false)
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tournament Scheduling"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Team Reqests"))

        val fragmentManager =
            requireActivity().supportFragmentManager // Use requireActivity() to get the activity's fragment manager

        adapter = TournamentHandlingAdapter(fragmentManager, lifecycle)
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