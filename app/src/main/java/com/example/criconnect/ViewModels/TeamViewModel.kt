package com.example.criconnect.ViewModels

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.Repositories.TeamRepository
import com.google.firebase.auth.FirebaseUser

class TeamViewModel(val repository: TeamRepository) : ViewModel() {

    fun saveTeam(team: TeamModel,selectedDrawable: Drawable?,callback:(Boolean)->Unit) {
        repository.addTeamToFirebase(team,selectedDrawable,callback)
    }

    fun saveTournament(tournament: TournamentData,selectedDrawable: Drawable?,callback:(Boolean)->Unit) {
        repository.addTournamentToFirebase(tournament,selectedDrawable,callback)
    }

    fun registerTeamInTournament(tournamentId: String?,callback: (Boolean) -> Unit) {
        repository.registerTeamInTournament(tournamentId,callback)
    }

    fun getRegisteredTeamsInTournament(tournamentId: String?,callback: (List<TeamModel>?) -> Unit){
        repository.getRegisteredTeamsInTournament(tournamentId,callback)
    }

   /* fun registerOrganizedMatchesInTournament(tournamentId: String?,dummyTeams : List<TeamModel>?,callback: (Boolean) -> Unit){
        repository.registerOrganizedMatchesInTournament(tournamentId,dummyTeams,callback)
    }
*/
    fun getTournament(callback: (List<TournamentData>?)->Unit){
        repository.getAllTournamentsFromFirebase(callback)
    }

    fun savePlayer(player : PlayerData, selectedDrawable: Drawable?, callback: (Boolean) -> Unit){
        repository.addPlayerToTeamFirebase(player,selectedDrawable,callback)
    }

    fun getPlayersList(callback: (List<PlayerData>?)->Unit){
        repository.getListOfPlayersFromTeam(callback)
    }

    fun getTeamData(callback: (TeamModel?,Boolean) -> Unit){
        repository.getTeamFromFirebase(callback)
    }

    fun storeMatchesinFirebase(tournamentId: String?,matches: List<Pair<TeamModel, TeamModel>>, callback: (Boolean) -> Unit){
        repository.storeMatchesInDatabase(tournamentId,matches,callback)
    }

    fun getLoggedInUser() : FirebaseUser?{
       return repository.getLoggedInUser()
    }

    fun deletePlayerFromTeam(playerId:String?,callback: (Boolean) -> Unit){
        repository.deletePlayerFromTeam(playerId,callback)
    }

}