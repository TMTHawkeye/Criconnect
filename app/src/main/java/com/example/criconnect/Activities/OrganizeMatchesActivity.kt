package com.example.criconnect.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.criconnect.Adapters.MatchesAdapter
import com.example.criconnect.HelperClasses.Constants.generateMatches
import com.example.criconnect.ModelClasses.MatchModel
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityOrganizeMatchesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable


class OrganizeMatchesActivity : AppCompatActivity(), Serializable {
    lateinit var binding: ActivityOrganizeMatchesBinding
    val dataViewModel: TeamViewModel by viewModel()
    var selectedTournamentId: String? = null
    var selectedTournamentName: String? = null
    var matches: List<MatchModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizeMatchesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun storeMatchesInDatabase(tournamentId: String?, matches: List<MatchModel>) {
        dataViewModel.saveMatches(tournamentId,matches){
            Log.d("matchIdii", "onCreate: ${matches?.get(0)?.teamAId}")
            setAdapter(matches)
        }

    }

    fun setAdapter(matchesList: List<MatchModel>) {
        var adapter =
            MatchesAdapter(this@OrganizeMatchesActivity, matchesList) { selectedWinner,selectedLooser, position ->

                dataViewModel.updateTeamStats(selectedWinner, selectedLooser) { success ->
                    if (success) {
                        Toast.makeText(this@OrganizeMatchesActivity, "Team stats updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@OrganizeMatchesActivity, "Failed to update team stats", Toast.LENGTH_SHORT).show()
                    }
                }
//                // Update wins count for the corresponding team
//                if (selectedWinner != null && selectedWinner.isNotEmpty()) {
//                    matches?.get(position)?.winner = selectedWinner!!
//                    var newTeam: TeamModel? = null
//
//                    for (team in tournamentData?.teamList ?: ArrayList()) {
//                        if (team.teamId == selectedWinner) {
//                            newTeam = team
//                            break
//                        }
//                    }
//
//                    if (newTeam != null) {
//                        newTeam?.wins = newTeam.wins + 1
//                        dataViewModel.saveTeam(newTeam, getDrawable(R.drawable.circlelogo)) {
//                            if (it) {
//                                Toast.makeText(
//                                    this@OrganizeMatchesActivity,
//                                    "Saved in team",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            } else {
//                                Toast.makeText(
//                                    this@OrganizeMatchesActivity,
//                                    "Not Saved in team",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//
//                            }
//                        }
//                    }
//
//                }
//
//                storeMatchesInDatabase(tournamentData?.tournamentId, match\ujes!!)
            }
        binding.matchesRV.apply {
            (getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations = false
            this.layoutManager = LinearLayoutManager(this@OrganizeMatchesActivity)
            this.adapter = adapter
            setHasFixedSize(true)
        }

    }


    // Function to distribute matches evenly for each team


    override fun onResume() {
        super.onResume()
        selectedTournamentId = intent?.getStringExtra("selectedTournament")
        selectedTournamentName= intent?.getStringExtra("tournamentName")
        binding.titleTournament.text=selectedTournamentName

        dataViewModel.getMatchesForTournament(selectedTournamentId) { existingMatches ->
            if (existingMatches?.size==0) {
                dataViewModel.getRegisteredTeamsInTournament(selectedTournamentId) { registeredTeams ->
                    matches = generateMatches(registeredTeams)
                    matches?.let {
                        storeMatchesInDatabase(selectedTournamentId, it)
                        setAdapter(it)
                    }
                }
            } else {
                matches = existingMatches
                matches?.let {
                    setAdapter(it)
                }
            }
        }
    }




}