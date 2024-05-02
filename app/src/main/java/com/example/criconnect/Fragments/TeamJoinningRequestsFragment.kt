package com.example.criconnect.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.criconnect.Adapters.PlayerAdapter
import com.example.criconnect.Adapters.RequestsAdapter
import com.example.criconnect.Interfaces.RequestAcceptListner
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ViewModels.TournamentViewModel
import com.example.criconnect.databinding.FragmentTeamJoinningRequestsBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TeamJoinningRequestsFragment : Fragment() , RequestAcceptListner{
    private var adapter: RequestsAdapter? = null
    val tournamentViewModel : TournamentViewModel by sharedViewModel()

    //    private var dataList: List<DataClass>? = null
    var dataList: List<PlayerData>? = null

    lateinit var binding: FragmentTeamJoinningRequestsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTeamJoinningRequestsBinding.inflate(layoutInflater)

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


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.noDataIdTV.visibility=View.VISIBLE

        tournamentViewModel.getRequestList {teamList->
            if(teamList.size!=0) {
                teamList.let {
                    setAdapter(it)
                }
                binding.noDataIdTV.visibility=View.GONE
            }
            else{
                binding.noDataIdTV.visibility=View.VISIBLE
            }
        }
    }


    private fun setAdapter(teamsList: ArrayList<TeamModel>) {
        val gridLayoutManager = GridLayoutManager(requireActivity(), 1)
        binding.recyclerView.setLayoutManager(gridLayoutManager)
        adapter = RequestsAdapter(requireActivity(), teamsList)
        adapter?.setListner(this)
        binding.recyclerView.setAdapter(adapter)
    }

    override fun requestAcceptedTeam(team: TeamModel?) {
        team?.let {
            tournamentViewModel.acceptRequest(it.teamId){
                if(it){
                    Snackbar.make(binding.root, "Request Accepted!", Snackbar.LENGTH_SHORT).show()
                }
                else{
                    Snackbar.make(binding.root, "Something went wrong!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }


//    private fun searchList(text: String) {
//        val dataSearchList: MutableList<DataClass> = ArrayList()
//        for (data in dataList) {
//            if (data.getDataTitle().toLowerCase().contains(text.lowercase(Locale.getDefault()))) {
//                dataSearchList.add(data)
//            }
//        }
//        if (dataSearchList.isEmpty()) {
//            Toast.makeText(requireContext(), "Not Found", Toast.LENGTH_SHORT).show()
//        } else {
//            adapter!!.setSearchList(dataSearchList)
//        }
//    }

}