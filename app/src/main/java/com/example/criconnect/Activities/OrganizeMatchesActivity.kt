package com.example.criconnect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.criconnect.Adapters.MatchesAdapter
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityOrganizeMatchesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable
import java.util.UUID

class OrganizeMatchesActivity : AppCompatActivity(), Serializable {
    lateinit var binding: ActivityOrganizeMatchesBinding
    val dataViewModel: TeamViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizeMatchesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tournamentData = intent?.getSerializableExtra("selectedTournament") as TournamentData
        Log.d("TAGTournamentid", "onCreate: ${tournamentData.tournamentId}")

        if (tournamentData != null) {
            binding.titleTournament.text = tournamentData.tournamentName
            dataViewModel.getRegisteredTeamsInTournament(tournamentData.tournamentId) { registeredTeams ->
                val matches = generateMatches(registeredTeams)
                storeMatchesInDatabase(tournamentData.tournamentId, matches)
            }
        }
    }

    fun storeMatchesInDatabase(tournamentId: String?, matches: List<Pair<TeamModel, TeamModel>>) {
        dataViewModel.storeMatchesinFirebase(tournamentId, matches) {
            setAdapter(matches)
        }
    }

    fun setAdapter(matches: List<Pair<TeamModel, TeamModel>>) {
        binding.matchesRV.layoutManager = LinearLayoutManager(this@OrganizeMatchesActivity)
        binding.matchesRV.adapter = MatchesAdapter(this@OrganizeMatchesActivity, matches)
    }

    private fun generateMatches(teamsList: List<TeamModel>?): List<Pair<TeamModel, TeamModel>> {
        val matches = mutableListOf<Pair<TeamModel, TeamModel>>()

        if (teamsList != null && teamsList.size >= 2) {

            when (teamsList.size) {
                3 -> {
                    matches.add(Pair(teamsList[0], teamsList[1]))
                    matches.add(Pair(teamsList[1], teamsList[2]))
                    matches.add(Pair(teamsList[2], teamsList[0]))

                    val finalistMatch =
                        Pair(TeamModel(teamName = "Finalist 1"), TeamModel(teamName = "Finalist 2"))
                    matches.add(finalistMatch)
                }

                4 -> { // For 4 teams
                    // Semi-finals
                    matches.add(Pair(teamsList[0], teamsList[3]))
                    matches.add(Pair(teamsList[1], teamsList[2]))

                    // Semi-finals
                    matches.add(
                        Pair(
                            TeamModel(teamName = "Winner 1"),
                            TeamModel(teamName = "Winner 2")
                        )
                    )
                    matches.add(
                        Pair(
                            TeamModel(teamName = "Winner 3"),
                            TeamModel(teamName = "Winner 4")
                        )
                    )

                    val finalistMatch =
                        Pair(TeamModel(teamName = "Finalist 1"), TeamModel(teamName = "Finalist 2"))
                    matches.add(finalistMatch)
                }

                8 -> { // For 8 teams
                    // Quarter-finals
                    val quarterFinalTeams = listOf(
                        Pair(teamsList[0], teamsList[7]),
                        Pair(teamsList[1], teamsList[6]),
                        Pair(teamsList[2], teamsList[5]),
                        Pair(teamsList[3], teamsList[4])
                    )
                    matches.addAll(quarterFinalTeams)

                    // Semi-finals
                    matches.add(
                        Pair(
                            TeamModel(teamName = "Winner 1"),
                            TeamModel(teamName = "Winner 2")
                        )
                    )
                    matches.add(
                        Pair(
                            TeamModel(teamName = "Winner 3"),
                            TeamModel(teamName = "Winner 4")
                        )
                    )

                    // Final
                    matches.add(
                        Pair(
                            TeamModel(teamName = "Finalist 1"),
                            TeamModel(teamName = "Finalist 2")
                        )
                    )
                }

                12 -> { // For 12 teams
                    // Preliminary Round
                    val preliminaryRoundMatches = mutableListOf<Pair<TeamModel, TeamModel>>()
                    for (i in 0 until teamsList.size - 1) {
                        for (j in i + 1 until teamsList.size) {
                            preliminaryRoundMatches.add(Pair(teamsList[i], teamsList[j]))
                        }
                    }

                    // Distribute matches evenly for each team
                    val distributedMatches =
                        distributeMatchesEvenly(preliminaryRoundMatches, teamsList.size)

                    matches.addAll(distributedMatches)

                    // Quarter-finals
                    val quarterFinalTeams = listOf(
                        Pair(teamsList[0], teamsList[11]),
                        Pair(teamsList[1], teamsList[10]),
                        Pair(teamsList[2], teamsList[9]),
                        Pair(teamsList[3], teamsList[8])
                    )
                    matches.addAll(quarterFinalTeams)

                    // Semi-finals
                    matches.add(
                        Pair(
                            TeamModel(teamName = "Winner 1"),
                            TeamModel(teamName = "Winner 2")
                        )
                    )
                    matches.add(
                        Pair(
                            TeamModel(teamName = "Winner 3"),
                            TeamModel(teamName = "Winner 4")
                        )
                    )

                    // Final
                    matches.add(
                        Pair(
                            TeamModel(teamName = "Finalist 1"),
                            TeamModel(teamName = "Finalist 2")
                        )
                    )
                }

                else -> {
                    println("Unsupported number of teams")
                }
            }
        } else {
            println("Invalid teams list")
        }

        return matches
    }

    // Function to distribute matches evenly for each team
    private fun distributeMatchesEvenly(
        matches: List<Pair<TeamModel, TeamModel>>,
        numTeams: Int
    ): List<Pair<TeamModel, TeamModel>> {
        val distributedMatches = mutableListOf<Pair<TeamModel, TeamModel>>()
        val matchesPerTeam = numTeams / 2

        for (i in 0 until matchesPerTeam) {
            for (j in 0 until numTeams step matchesPerTeam) {
                distributedMatches.add(matches[i + j])
            }
        }

        return distributedMatches
    }

    // Helper function to determine the winner team
    private fun getWinnerTeam(team1: TeamModel, team2: TeamModel): TeamModel {
        // For simplicity, we can just return the team with a higher alphabetical name
        return if (team1.teamName < team2.teamName) team1 else team2
    }


    fun createDummyTeams(): List<TeamModel> {
        val dummyTeams = mutableListOf<TeamModel>()

        for (i in 1..8) {
            val teamName = "Team $i"
            val captainName = "Captain ${UUID.randomUUID()}"
            val city = "City ${UUID.randomUUID()}"
            val homeGround = "Ground ${UUID.randomUUID()}"

            val dummyTeam = TeamModel(
                teamName = teamName,
                captainName = captainName,
                city = city,
                homeGround = homeGround
            )

            dummyTeams.add(dummyTeam)
        }

        return dummyTeams
    }


}