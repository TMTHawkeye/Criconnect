package com.example.criconnect.ModelClasses

data class MatchModel(
    var matchId: String ?= "",
    val teamAId: String?="",
    val teamBId: String?="",
    var winnerId: String? = "",
    var startDate: String? = "",
)
