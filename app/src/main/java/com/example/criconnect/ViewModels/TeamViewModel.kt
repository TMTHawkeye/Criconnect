package com.example.criconnect.ViewModels

import androidx.lifecycle.ViewModel
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.Repositories.TeamRepository

class TeamViewModel(val repository: TeamRepository) : ViewModel() {

    fun saveTeam(team: TeamModel,callback:(Boolean)->Unit) {
        repository.addTeamToFirebase(team,callback)
    }

    fun saveTournament(tournament: TournamentData,callback:(Boolean)->Unit) {
        repository.addTournamentToFirebase(tournament,callback)
    }

    fun registerTeamInTournament(tournamentId: String?,callback: (Boolean) -> Unit) {
        repository.registerTeamInTournament(tournamentId,callback)
    }

    fun getTournament(callback: (List<TournamentData>?)->Unit){
        repository.getAllTournamentsFromFirebase(callback)
    }

    fun savePlayer(player : PlayerData,callback: (Boolean) -> Unit){
        repository.addPlayerToTeamFirebase(player,callback)
    }

    fun getPlayersList(callback: (List<PlayerData>?)->Unit){
        repository.getListOfPlayersFromTeam(callback)
    }

    fun getTeamData(callback: (TeamModel?) -> Unit){
        repository.getTeamFromFirebase(callback)
    }


}