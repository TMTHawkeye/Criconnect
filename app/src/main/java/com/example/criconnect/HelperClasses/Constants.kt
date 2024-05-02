package com.example.criconnect.HelperClasses

import android.content.Context
import android.content.SharedPreferences
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.Adapters.MyViewHolder
import com.example.criconnect.ModelClasses.MatchModel
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.R
import com.google.gson.Gson

object Constants {
    const val ownTeamDataPreference = "TEAMDATA"
    const val teamDataKey = "team_data_key"
    const val dashboardOrganizer = "Organizer"
    const val dashboardCaptain = "Captain"
    const val dashboardAdmin = "Admin"


    fun getTeamData(context:Context): TeamModel? {
        val sharedPreferences =
            context.getSharedPreferences(Constants.ownTeamDataPreference, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(Constants.teamDataKey, null)
        return gson.fromJson(json, TeamModel::class.java)
    }

    fun storeTeamDataInSharedPreferences(context: Context,teamData: TeamModel): Boolean {
        try {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(Constants.ownTeamDataPreference, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            val gson = Gson()
            val teamDataJson = gson.toJson(teamData)
            editor.putString(Constants.teamDataKey, teamDataJson)
            editor.apply()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun deleteTeamDataFromSharedPreferences(context: Context): Boolean {
        try {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(Constants.ownTeamDataPreference, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.remove(Constants.teamDataKey) // Remove the data associated with the key
            editor.apply()
            return true
        } catch (e: Exception) {
            return false
        }
    }


    fun loadImage(context: Context,imgView:ImageView,imgURL:String?){
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.circlelogo)
            .error(R.drawable.circlelogo)

        Glide.with(context)
            .load(imgURL)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .apply(options)
            .into(imgView)

    }

     fun generateMatches(teamsList: List<TeamModel>?): List<MatchModel> {
        val matches = mutableListOf<MatchModel>()

        if (teamsList != null && teamsList.size >= 2) {

            when (teamsList.size) {
                3 -> {
                    matches.add(MatchModel(teamAId = teamsList[0].teamId,teamBId =  teamsList[1].teamId))
                    matches.add(MatchModel(teamAId = teamsList[1].teamId,teamBId =  teamsList[2].teamId))
                    matches.add(MatchModel(teamAId = teamsList[2].teamId,teamBId =  teamsList[0].teamId))

//                    val finalistMatch =
//                        MatchModel(teamAId = "Finalist 1", teamBId =  "Finalist 2")
//                    matches.add(finalistMatch)
                }

                4 -> { // For 4 teams
                    // Semi-finals

                    matches.add(MatchModel(teamAId = teamsList[0].teamId,teamBId =  teamsList[3].teamId))
                    matches.add(MatchModel(teamAId = teamsList[1].teamId,teamBId =  teamsList[2].teamId))


//                    // Semi-finals
//                    matches.add(
//                        MatchModel(
//                            teamAId =   "Winner 1",
//                            teamBId =   "Winner 2"
//                        )
//
//                    )
//                    matches.add(
//                        MatchModel(
//                            teamAId =  "Winner 3",
//                            teamBId =   "Winner 4"
//                        )
//                    )

                    matches.add(MatchModel(teamAId = teamsList[0].teamId,teamBId =  teamsList[1].teamId))
                    matches.add(MatchModel(teamAId = teamsList[0].teamId,teamBId =  teamsList[1].teamId))


//                    val finalistMatch =
//                        MatchModel(teamAId =  "Finalist 1", teamBId =  "Finalist 2")
//                    matches.add(finalistMatch)
                }

                8 -> { // For 8 teams
                    // Quarter-finals
                    val quarterFinalTeams = listOf(
                        MatchModel(teamAId = teamsList[0].teamId, teamBId =  teamsList[7].teamId),
                        MatchModel(teamAId = teamsList[1].teamId, teamBId =  teamsList[6].teamId),
                        MatchModel(teamAId=teamsList[2].teamId,teamBId= teamsList[5].teamId),
                        MatchModel(teamAId=teamsList[3].teamId, teamBId=teamsList[4].teamId)
                    )
                    matches.addAll(quarterFinalTeams)

//                    // Semi-finals
//                    matches.add(
//                        MatchModel(teamAId ="Winner 1", teamBId = "Winner 2")
//                    )
//                    matches.add(
//                        MatchModel(
//                            teamAId = "Winner 3",
//                            teamBId =   "Winner 4"
//                        )
//                    )
//
//                    // Final
//                    matches.add(
//                        MatchModel(
//                            teamAId =  "Finalist 1",
//                            teamBId =  "Finalist 2"
//                        )
//                    )
                }

                12 -> { // For 12 teams
                    // Preliminary Round
                    val preliminaryRoundMatches = mutableListOf<MatchModel>()
                    for (i in 0 until teamsList.size - 1) {
                        for (j in i + 1 until teamsList.size) {
                            preliminaryRoundMatches.add(MatchModel(teamAId = teamsList[i].teamId, teamBId =  teamsList[j].teamId))
                        }
                    }

                    // Distribute matches evenly for each team
                    val distributedMatches =
                        distributeMatchesEvenly(preliminaryRoundMatches, teamsList.size)

                    matches.addAll(distributedMatches)

                    // Quarter-finals
                    val quarterFinalTeams = listOf(
                        MatchModel(teamAId = teamsList[0]?.teamId, teamBId =  teamsList[11].teamId),
                        MatchModel(teamAId = teamsList[1].teamId, teamBId =  teamsList[10].teamId),
                        MatchModel(teamAId = teamsList[2].teamId, teamBId = teamsList[9].teamId),
                        MatchModel(teamAId = teamsList[3].teamId, teamBId = teamsList[8].teamId)
                    )
                    matches.addAll(quarterFinalTeams)

//                    // Semi-finals
//                    matches.add(
//                        MatchModel(
//                            teamAId =   "Winner 1",
//                            teamBId = "Winner 2"
//                        )
//                    )
//                    matches.add(
//                        MatchModel(
//                            teamAId =  "Winner 3",
//                            teamBId =  "Winner 4"
//                        )
//                    )
//
//                    // Final
//                    matches.add(
//                        MatchModel(
//                            teamAId =  "Finalist 1",
//                            teamBId =   "Finalist 2"
//                        )
//                    )
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

    private fun distributeMatchesEvenly(
        matches: List<MatchModel>,
        numTeams: Int
    ): List<MatchModel> {
        val distributedMatches = mutableListOf<MatchModel>()
        val matchesPerTeam = numTeams / 2

        for (i in 0 until matchesPerTeam) {
            for (j in 0 until numTeams step matchesPerTeam) {
                distributedMatches.add(matches[i + j])
            }
        }

        return distributedMatches
    }


}