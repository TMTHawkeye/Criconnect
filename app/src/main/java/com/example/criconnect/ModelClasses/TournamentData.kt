package com.example.criconnect.ModelClasses

import android.graphics.drawable.Drawable
import java.io.Serializable

data class TournamentData(
    var tournamentId : String?=null,
    var tournamentLogo : String?=null,
    val tournamentName : String="",
    val tournamentLocation : String="",
    val tournamentEntryFee : String="",
    val tournamentWinningPrize : String="",
    val teamList : ArrayList<TeamModel>?=ArrayList()
) : Serializable
