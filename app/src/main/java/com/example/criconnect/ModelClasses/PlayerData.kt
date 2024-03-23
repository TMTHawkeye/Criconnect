package com.example.criconnect.ModelClasses

import java.io.Serializable

data class PlayerData(
    var playerLogo: String?=null,
    var playerName : String = "",
    var fatherName : String = "",
    var playerCity : String = "",
    var playerAge : String = "",
    var playerPhone : String = "",
    var speciality : String = "",
    var batsmanhand : String = "",
    var details : String = ""
):Serializable
