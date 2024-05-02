package com.example.criconnect.ViewModels

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.tournamentDataClass
import com.example.criconnect.Repositories.TournamentRepository
import kotlinx.coroutines.launch

class TournamentViewModel(val repository: TournamentRepository) : ViewModel() {

    fun getTournaments(callback: (List<tournamentDataClass>?) -> Unit) {
        repository.getTournaments(callback)
    }

    fun registerTournament(selectedDrawable: Drawable?, tournamentData: tournamentDataClass, callback:(Boolean)->Unit) {
        repository.registerTournament(selectedDrawable,tournamentData,callback)
    }

    fun getRequestList(callback: (ArrayList<TeamModel>) -> Unit){
        repository.getRequestingTeams(callback)
    }

    fun acceptRequest(teamId:String?, callback: (Boolean) -> Unit){
        repository.removeFromRequestsList(teamId,callback)
    }

    fun getTournamentbyID(  callback: (tournament: tournamentDataClass?) -> Unit){
        repository.getTournamentById( callback)
    }
}