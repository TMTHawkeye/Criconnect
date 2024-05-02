package com.example.criconnect.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.Activities.OrganizeMatchesActivity
import com.example.criconnect.Adapters.RegisteredTeamsAdapter
import com.example.criconnect.HelperClasses.Constants
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.tournamentDataClass
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.ViewModels.TournamentViewModel
import com.example.criconnect.databinding.FragmentTournamentSchedulingBinding
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TournamentSchedulingFragment : Fragment() {
    val dataViewModel: TeamViewModel by sharedViewModel()
    val tournamentViewModel: TournamentViewModel by sharedViewModel()
    var selectedTournament: tournamentDataClass? = null

    lateinit var binding: FragmentTournamentSchedulingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTournamentSchedulingBinding.inflate(layoutInflater)
//        var activeUser = FirebaseAuth.getInstance().currentUser
         tournamentViewModel.getTournamentbyID( ) { tournament ->
            if (tournament != null) {
                selectedTournament=tournament
                setTournamentAttributes()
                getRegisteredTeamsInTournament()

            } else {
                Toast.makeText(requireContext(), "No Data Found!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnOrganizeMatches.setOnClickListener {
            selectedTournament?.let {
                startActivity(
                    Intent(requireContext(), OrganizeMatchesActivity::class.java)
                        .putExtra("selectedTournament", selectedTournament)
                )
            }
        }


        return binding.root
    }

    private fun setTournamentAttributes() {
        loadImage()
        binding.titleTournament.text = selectedTournament?.name
        binding.locationId.text = selectedTournament?.ground
        binding.entryId.text = selectedTournament?.ballType
        binding.winningId.text = selectedTournament?.organizerEmail
        binding.organizerNameId.text = selectedTournament?.organizerName
        binding.winningPrizeId.text = selectedTournament?.tournamentWinningPrize
        binding.eventStartId.text = selectedTournament?.eventStartDate
        binding.eventEndId.text = selectedTournament?.eventEndDate
        binding.pitchTypeId.text = selectedTournament?.pitchType
        binding.detailsId.text = selectedTournament?.details
    }

    fun loadImage() {
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.no_image_found)
            .error(R.drawable.no_image_found)

        Glide.with(requireContext())
            .load(selectedTournament?.tournamentLogo)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .apply(options)
            .into(binding.tournamentImageId)

    }

    private fun getRegisteredTeamsInTournament() {
        val user = dataViewModel.getLoggedInUser()
        dataViewModel.getRegisteredTeamsInTournament(selectedTournament?.tournamentId) { listTeam ->
            if (listTeam?.size != 0) {
                if (listTeam?.size!! < 3 || listTeam?.size!! > 12) {
                    binding.btnOrganizeMatches.visibility = View.GONE
                } else {
                    binding.btnOrganizeMatches.visibility = View.VISIBLE
                }
                setAdapter(listTeam)
            } else {
                binding.btnOrganizeMatches.visibility = View.GONE
                binding.teamsRV.visibility = View.GONE
             }
        }


    }

    fun setAdapter(teamsList: List<TeamModel>?) {
        binding.teamsRV.visibility = View.VISIBLE
        binding.teamsRV.layoutManager = LinearLayoutManager(requireContext())
        binding.teamsRV.adapter = RegisteredTeamsAdapter(
            requireContext(),
            teamsList,
            selectedTournament?.tournamentId
        )
    }




}