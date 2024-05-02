package com.example.criconnect.Interfaces

import com.example.criconnect.ModelClasses.TeamModel

interface RequestAcceptListner {

    fun requestAcceptedTeam(team : TeamModel?)
}