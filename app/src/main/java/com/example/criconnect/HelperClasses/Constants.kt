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
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.R
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

}