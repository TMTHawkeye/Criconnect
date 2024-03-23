package com.example.criconnect.ModelClasses

import android.graphics.drawable.Drawable

data class TeamModel(
    var teamId :String = "",
    var teamLogo : String?="",
    var teamName: String = "",
    var captainName: String = "",
    var city: String = "",
    var homeGround: String = "",
    var playerData : ArrayList<PlayerData>? = ArrayList()
)
