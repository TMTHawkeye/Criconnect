package com.example.criconnect.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.criconnect.Activities.PlayerProfileActivity
import com.example.criconnect.Adapters.PlayerAdapter
import com.example.criconnect.Interfaces.PlayerListner
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.FragmentTeamManagementBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Locale

class TeamManagementFragment : Fragment() , PlayerListner{
    lateinit var binding : FragmentTeamManagementBinding
    val teamViewModel : TeamViewModel by sharedViewModel()

    var dataList: List<PlayerData>?=null
    private var adapter: PlayerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTeamManagementBinding.inflate(layoutInflater,container,false)


        binding.fab.setOnClickListener(View.OnClickListener { // Replace the current fragment with PlayerRegistrationFragment
            val intent = Intent(context, PlayerProfileActivity::class.java)
            startActivity(intent)
        })



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
        return binding.root
    }

    private fun getTeamsListFromRepository() {
        binding.progressBar.visibility=View.VISIBLE
        teamViewModel.getPlayersList {
            if(it!=null) {
                dataList = it
                setAdapter(it)
                binding.noDataTV.visibility=View.GONE
            }
            else{
                binding.noDataTV.visibility=View.VISIBLE
            }
            binding.progressBar.visibility=View.GONE

        }
    }

    private fun setAdapter(playerList: List<PlayerData>?) {
        val gridLayoutManager = GridLayoutManager(requireActivity(), 1)
        binding.recyclerView.setLayoutManager(gridLayoutManager)
        adapter = PlayerAdapter(requireActivity(), playerList,this)
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
                Toast.makeText(requireContext(), "Not Found", Toast.LENGTH_SHORT).show()
            } else {
                adapter!!.setSearchList(dataSearchList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getTeamsListFromRepository()
    }

    override fun onDeletePlayer(player: PlayerData) {
        teamViewModel.deletePlayerFromTeam(player?.playerId) { success ->
            if (success) {
                val updatedList = dataList?.filter { it.playerId != player.playerId }
                if(updatedList?.size!=0) {
                    adapter?.updateList(updatedList)
                    binding.noDataTV.visibility=View.GONE
                }
                else{
                    binding.noDataTV.visibility=View.VISIBLE
                }
                Toast.makeText(requireContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Unable to delete, tryagain!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}