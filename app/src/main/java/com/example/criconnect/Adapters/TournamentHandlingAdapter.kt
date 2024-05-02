package com.example.criconnect.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.criconnect.Fragments.TeamJoinningRequestsFragment
import com.example.criconnect.Fragments.TournamentSchedulingFragment

class TournamentHandlingAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return if (position == 1) {
            TeamJoinningRequestsFragment()
        } else TournamentSchedulingFragment()
    }

    override fun getItemCount(): Int {
        return 2
    }
}
