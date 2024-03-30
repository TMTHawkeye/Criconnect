package com.example.criconnect.ModelClasses

import java.io.Serializable

data class MatchModel(
    var matchId: String ?= "",
    val teamAId: String?="",
    val teamBId: String?="",
    var winnerId: String? = "",
    var looserId: String? = "",
    var startDate: String? = "",
    var completeStatus : Boolean=false,
    var playerOfMatch : String = "",
    var winnerTeamWonby : String = "",
    var oversFormat : String=""
):Serializable
