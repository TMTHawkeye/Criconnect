package com.example.criconnect.ModelClasses

import android.graphics.drawable.Drawable
import java.io.Serializable

data class PlayerData(
    var playerId : String = "",
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
