package com.example.criconnect.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.Adapters.PlayerAdapter
import com.example.criconnect.Adapters.RegisteredTeamsAdapter
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityTournamentDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

class TournamentDetailActivity : AppCompatActivity(),Serializable {
    lateinit var binding: ActivityTournamentDetailBinding
    val dataViewModel: TeamViewModel by viewModel()
    var selectedTournament : TournamentData?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTournamentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedTournament = intent?.getSerializableExtra("selectedTournament") as TournamentData
        Log.d("TAGTournamentid", "onCreate: ${selectedTournament?.tournamentId}")

        if(selectedTournament!=null) {
            setTournamentAttributes()
            getRegisteredTeamsInTournament()
        }

        binding.registerTeamId.setOnClickListener {
            registerTeam()
        }

        binding.btnOrganizeMatches.setOnClickListener {
            startActivity(
                Intent(this@TournamentDetailActivity, OrganizeMatchesActivity::class.java)
                    .putExtra("selectedTournament", selectedTournament)
            )

        }
    }

    private fun setTournamentAttributes() {
        loadImage()
        binding.locationId.text = selectedTournament?.tournamentName
        binding.entryId.text = selectedTournament?.tournamentEntryFee
        binding.winningId.text = selectedTournament?.tournamentWinningPrize
    }

    fun loadImage(){
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.circlelogo)
            .error(R.drawable.circlelogo)

        Glide.with(this@TournamentDetailActivity)
            .load(selectedTournament?.tournamentLogo)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .apply(options)
            .into(binding.tournamentImageId)

    }

    private fun registerTeam() {
        binding.progressBar.visibility = View.VISIBLE
        dataViewModel.registerTeamInTournament(selectedTournament?.tournamentId) {
            dataViewModel.getRegisteredTeamsInTournament(selectedTournament?.tournamentId) {
                binding.progressBar.visibility = View.GONE
                binding.registerTeamId.visibility = View.VISIBLE
                if (it?.size != 0) {
                    binding.btnOrganizeMatches.visibility = View.VISIBLE
                    setAdapter(it)
                } else {
                    binding.btnOrganizeMatches.visibility = View.GONE
                    binding.teamsRV.visibility = View.GONE
                }
            }
        }
    }

    private fun getRegisteredTeamsInTournament() {
        val user = dataViewModel.getLoggedInUser()
        dataViewModel.getRegisteredTeamsInTournament(selectedTournament?.tournamentId) {listTeam->
             if (listTeam?.size != 0) {
                 listTeam?.forEach {
                    if(it.teamId.equals(user?.uid) || listTeam.size>12){
                        binding.registerTeamId.visibility=View.GONE
                     }
                }
                 if(listTeam?.size!! < 3 || listTeam?.size!! > 12){
                     binding.btnOrganizeMatches.visibility=View.GONE
                 }
                setAdapter(listTeam)

            } else {
                binding.btnOrganizeMatches.visibility=View.GONE
                binding.teamsRV.visibility=View.GONE
                binding.registerTeamId.visibility=View.VISIBLE
            }
        }
    }

    fun setAdapter(teamsList: List<TeamModel>?) {
        binding.teamsRV.layoutManager = LinearLayoutManager(this@TournamentDetailActivity)
        binding.teamsRV.adapter = RegisteredTeamsAdapter(this@TournamentDetailActivity, teamsList)
    }


}