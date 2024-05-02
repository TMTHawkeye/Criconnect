package com.example.criconnect.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.criconnect.Adapters.PlayerAdapter
import com.example.criconnect.ModelClasses.DataClass
import com.example.criconnect.ModelClasses.MatchModel
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.FragmentComplainBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Locale

class ComplainFragment : Fragment() {
    lateinit var binding : FragmentComplainBinding
    val teamViewModel : TeamViewModel by sharedViewModel()

    private var dataList: ArrayList<PlayerData>? =  ArrayList()
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
                searchList(newText)
                return true
            }
        })

        val user=teamViewModel.getLoggedInUser()
        binding.progressBar.visibility=View.VISIBLE
        teamViewModel.getSelectedTeamDetails(user?.uid) { team ->
            if(team!=null) {
                teamViewModel.getAllMatchesFromAllTournaents {
                    Log.d("TAG_matchesList", "onCreateView: ${it?.size}")
                    val bestPlayersList = getMostPlayerOfTheMatch(user?.uid, it, team?.playerData)
                    dataList = team?.playerData
                    if (bestPlayersList.size != 0) {
                        binding.noDataTV.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        setAdapter(bestPlayersList)
                    } else {
                        binding.noDataTV.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }
            else{
                binding.progressBar.visibility=View.GONE
                binding.noDataTV.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
        return binding.root
    }

    private fun setAdapter(playerList: List<PlayerData>?) {
        val gridLayoutManager = GridLayoutManager(requireActivity(), 1)
        binding.recyclerView.setLayoutManager(gridLayoutManager)
        adapter = PlayerAdapter(requireActivity(), playerList,null)
        binding.recyclerView.setAdapter(adapter)
    }

    private fun searchList(text: String) {
        dataList?.let {
            val dataSearchList: MutableList<PlayerData> = java.util.ArrayList()
            for (data in it) {
                if (data.playerName.toLowerCase().contains(text.lowercase(Locale.getDefault()))) {
                    dataSearchList.add(data)
                }
            }
            if (dataSearchList.isEmpty()) {
//                Toast.makeText(requireContext(), "Not Found", Toast.LENGTH_SHORT).show()
            } else {
                adapter?.setSearchList(dataSearchList)
            }
        }
    }

   /* fun getMostPlayerOfTheMatch(teamId: String?, matches: List<MatchModel>?): String {
        val playerMatchCountMap = mutableMapOf<String, Int>()

        // Iterate through the matches
        matches?.forEach { match ->
            if (match.winnerId == teamId) {
                val playerOfTheMatch = match.playerOfMatch
                // Increment count for the player
                if (playerOfTheMatch.isNotBlank()) {
                    playerMatchCountMap[playerOfTheMatch] = (playerMatchCountMap[playerOfTheMatch] ?: 0) + 1
                }
            }
        }

        // Find the player with the highest count
        var mostPlayerOfTheMatch: String? = null
        var maxCount = 0
        playerMatchCountMap.forEach { (player, count) ->
            if (count > maxCount) {
                mostPlayerOfTheMatch = player
                maxCount = count
            }
        }

        return mostPlayerOfTheMatch ?: ""
    }*/

//    fun getMostPlayerOfTheMatch(teamId: String?, matches: List<MatchModel>?, allPlayers: ArrayList<PlayerData>?): List<PlayerData> {
//        val playerMatchCountMap = mutableMapOf<String, Int>()
//
//        // Iterate through the matches
//        matches?.forEach { match ->
//            if (match.winnerId == teamId) {
//                val playerOfTheMatch = match.playerOfMatch
//                // Increment count for the player
//                if (playerOfTheMatch.isNotBlank()) {
//                    playerMatchCountMap[playerOfTheMatch] = (playerMatchCountMap[playerOfTheMatch] ?: 0) + 1
//                }
//            }
//        }
//
//        // Sort players by count of "Player of the Match" awards
//        val sortedPlayers = playerMatchCountMap.entries.sortedByDescending { it.value }.mapNotNull { entry ->
//            val playerName = entry.key
//            val playerData = allPlayers?.find { it.playerName == playerName }
//            playerData
//        }
//
//        return sortedPlayers
//    }



    fun getMostPlayerOfTheMatch(teamId: String?, matches: List<MatchModel>?, allPlayers: ArrayList<PlayerData>?): List<PlayerData> {
        // Filter matches based on teamId and non-blank playerOfTheMatch
        val relevantMatches = matches?.filter { it.winnerId == teamId && it.playerOfMatch.isNotBlank() }

        // Count occurrences of playerOfTheMatch
        val playerMatchCountMap = relevantMatches?.groupBy { it.playerOfMatch }?.mapValues { it.value.size }

        // Sort players by count of "Player of the Match" awards
        val sortedPlayers = playerMatchCountMap?.entries?.sortedByDescending { it.value }?.mapNotNull { entry ->
            allPlayers?.find { it.playerName == entry.key }
        }

        return sortedPlayers ?: emptyList()
    }



}