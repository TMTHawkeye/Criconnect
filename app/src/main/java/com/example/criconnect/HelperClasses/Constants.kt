package com.example.criconnect.HelperClasses

import android.content.Context
import android.content.SharedPreferences
import com.example.criconnect.ModelClasses.TeamModel
import com.google.gson.Gson

object Constants {
    const val ownTeamDataPreference = "TEAMDATA"
    const val teamDataKey = "team_data_key"


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
}