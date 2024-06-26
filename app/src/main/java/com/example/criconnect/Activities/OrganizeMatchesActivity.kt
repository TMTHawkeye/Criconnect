package com.example.criconnect.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.example.criconnect.Adapters.MatchesAdapter
import com.example.criconnect.HelperClasses.Constants.generateMatches
import com.example.criconnect.ModelClasses.MatchModel
import com.example.criconnect.ModelClasses.tournamentDataClass
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityOrganizeMatchesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable


class OrganizeMatchesActivity : AppCompatActivity(), Serializable {
    lateinit var binding: ActivityOrganizeMatchesBinding
    val dataViewModel: TeamViewModel by viewModel()
    var selectedTournament : tournamentDataClass?=null
//    var selectedTournamentId: String? = null
//    var selectedTournamentOwnerName: String? = null
//    var selectedTournamentName: String? = null
    var matches: List<MatchModel>? = null
    var isOwner = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizeMatchesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun storeMatchesInDatabase(tournamentId: String?, matches: List<MatchModel>?) {
        matches.let { matchesList ->
            dataViewModel.saveMatches(tournamentId, matchesList) { isSaved, matchList ->
                Log.d("matchIdii", "onCreate: ${matchList?.get(0)?.matchId}")
                setAdapter(matchList)
            }
        }

    }

    fun setAdapter(matchesList: List<MatchModel>?) {
        for (i in matchesList!!) {
            Log.d("TAGlisttttt", "setAdapter: ${i.completeStatus}")

        }

        var adapter =
            MatchesAdapter(
                this@OrganizeMatchesActivity,
                matchesList,
                selectedTournament?.tournamentId,
                selectedTournament?.organizerName,
                isOwner
            )

        binding.matchesRV.apply {
//            (getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations = false
            this.layoutManager = LinearLayoutManager(this@OrganizeMatchesActivity)
            this.adapter = adapter
//            setHasFixedSize(true)
        }

        binding.backbtnId.setOnClickListener {
            finish()
        }

    }


    override fun onResume() {
        super.onResume()
//        selectedTournamentId = intent?.getStringExtra("selectedTournament")
//        selectedTournamentOwnerName = intent?.getStringExtra("tournamentOwnerID")
//        selectedTournamentName = intent?.getStringExtra("tournamentName")
        selectedTournament =
            intent?.getSerializableExtra("selectedTournament") as tournamentDataClass
        binding.titleTournament.text = selectedTournament?.name

        binding.progressBar.visibility = View.VISIBLE

        if (selectedTournament?.tournamentId?.equals(dataViewModel.getLoggedInUser()?.uid) == true) {
            isOwner = true
        } else {
            isOwner=false
        }
        selectedTournament?.let {
            dataViewModel.getMatchesForTournament(it.tournamentId) { existingMatches ->
                if (existingMatches?.size == 0) {
                    dataViewModel.getRegisteredTeamsInTournament(it.tournamentId) { registeredTeams ->
                        matches = generateMatches(registeredTeams)
                        matches?.let {match->
                            storeMatchesInDatabase(it.tournamentId, match)
                        }
                    }
                } else {
                    matches = existingMatches
                    matches?.let {
                        setAdapter(it)
                    }
                }
                binding.progressBar.visibility = View.GONE

            }
        }
    }


}