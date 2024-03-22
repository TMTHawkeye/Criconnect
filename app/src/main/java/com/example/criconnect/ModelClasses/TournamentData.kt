package com.example.criconnect.ModelClasses

import android.graphics.drawable.Drawable

data class TournamentData(
    var tournamentLogo : Drawable?=null,
    val tournamentName : String="",
    val tournamentLocation : String="",
    val tournamentEntryFee : String="",
    val tournamentWinningPrize : String=""
)
