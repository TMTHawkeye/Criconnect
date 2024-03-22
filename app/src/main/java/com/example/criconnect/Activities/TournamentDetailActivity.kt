package com.example.criconnect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityTournamentDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TournamentDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityTournamentDetailBinding
     val dataViewModel : TeamViewModel by viewModel()
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTournamentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

         val tournamentId = intent?.getStringExtra("tournamentId")
         Log.d("TAGTournamentid", "onCreate: $tournamentId")


        binding.registerTeamId.setOnClickListener {
            registerTeam(tournamentId)
        }


    }

    private fun registerTeam(tournamentId: String?) {
        dataViewModel.registerTeamInTournament(tournamentId){

        }
    }


}