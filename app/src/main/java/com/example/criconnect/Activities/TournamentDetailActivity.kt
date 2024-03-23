package com.example.criconnect.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.criconnect.Adapters.RegisteredTeamsAdapter
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityTournamentDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TournamentDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityTournamentDetailBinding
    val dataViewModel: TeamViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTournamentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tournamentId = intent?.getStringExtra("tournamentId")
        Log.d("TAGTournamentid", "onCreate: $tournamentId")

        getRegisteredTeamsInTournament(tournamentId)

        binding.registerTeamId.setOnClickListener {
            registerTeam(tournamentId)
        }

        binding.btnOrganizeMatches.setOnClickListener {
            startActivity(
                Intent(this@TournamentDetailActivity, OrganizeMatchesActivity::class.java)
                    .putExtra("tournamentId", tournamentId)
            )

        }
    }

    private fun registerTeam(tournamentId: String?) {
        binding.progressBar.visibility = View.VISIBLE
        dataViewModel.registerTeamInTournament(tournamentId) {
            dataViewModel.getRegisteredTeamsInTournament(tournamentId) {
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

    private fun getRegisteredTeamsInTournament(tournamentId: String?) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnOrganizeMatches.visibility = View.GONE
        val user = dataViewModel.getLoggedInUser()
        dataViewModel.getRegisteredTeamsInTournament(tournamentId) {
            binding.progressBar.visibility = View.GONE
            if (it?.size != 0) {
                it?.forEach {
                    if(it.teamId.equals(user?.uid)){
                        binding.registerTeamId.visibility=View.GONE
                    }
                }
                setAdapter(it)
                if(it?.size!! >= 3) {
                    binding.btnOrganizeMatches.visibility = View.VISIBLE
                }
                if(it?.size!!>12){
                    binding.registerTeamId.visibility=View.GONE
                }
            } else {
                binding.btnOrganizeMatches.visibility = View.GONE
                binding.teamsRV.visibility = View.GONE
                binding.registerTeamId.visibility=View.VISIBLE
            }
        }
    }

    fun setAdapter(teamsList: List<TeamModel>?) {
        binding.teamsRV.layoutManager = LinearLayoutManager(this@TournamentDetailActivity)
        binding.teamsRV.adapter = RegisteredTeamsAdapter(this@TournamentDetailActivity, teamsList)
    }


}