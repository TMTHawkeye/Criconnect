package com.example.criconnect.Repositories

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import com.example.criconnect.HelperClasses.base64ToDrawable
import com.example.criconnect.HelperClasses.drawableToBase64
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.TournamentData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream


class TeamRepository(val context: Context) {

    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    init {
        getDatabaseReference()

    }

    fun getDatabaseReference() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase?.getReference("TeamManagement");

    }

    fun storeMatchesInDatabase(tournamentId: String?, matches: List<Pair<TeamModel, TeamModel>>) {
        val matchesRef = firebaseDatabase?.getReference("TournamentMatches")?.child("$tournamentId")

        matches.forEachIndexed { index, match ->
            matchesRef?.child("Match${index + 1}")?.apply {
                child("Team1").setValue(match.first)
                child("Team2").setValue(match.second)
            }
        }
    }

    fun addTeamToFirebase(team: TeamModel, callback: (Boolean) -> Unit) {
        databaseReference = firebaseDatabase?.getReference("TeamManagement");

        val currentUser = getLoggedInUser()
        currentUser?.let { user ->
            val currentUserUUID = user.uid
            val userTeamReference = databaseReference?.child(currentUserUUID)
            userTeamReference?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        callback.invoke(false)
                    } else {
                         val teamData = hashMapOf(
                             "teamId" to currentUserUUID,
                            "teamName" to team.teamName,
                            "captainName" to team.captainName,
                            "city" to team.city,
                            "homeGround" to team.homeGround,
                            "teamLogo" to team.teamLogo
                        )

                        // Save team data to Firebase
                        userTeamReference.setValue(teamData)
                            .addOnSuccessListener {
                                callback.invoke(true) // Indicate success
                            }
                            .addOnFailureListener {
                                callback.invoke(false) // Indicate error occurred
                            }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.invoke(false) // Indicate error occurred
                }
            })
        } ?: callback.invoke(false) // If currentUser is null, callback with false
    }

    fun addTournamentToFirebase(tournament: TournamentData, callback: (Boolean) -> Unit) {
        databaseReference = firebaseDatabase?.getReference("TournamentManagement");

        // Generate a unique ID for the tournament
        val tournamentId = databaseReference?.push()?.key

        tournamentId?.let { id ->
            tournament.tournamentId=id

            val tournamentData = hashMapOf(
                "tournamentId" to id,
                "tournamentName" to tournament.tournamentName,
                "tournamentLocation" to tournament.tournamentLocation,
                "tournamentEntryFee" to tournament.tournamentEntryFee,
                "tournamentWinningPrize" to tournament.tournamentWinningPrize,
                "tournamentLogo" to tournament.tournamentLogo
            )

            // Save tournament data under the generated unique ID
            databaseReference?.child(id)?.setValue(tournamentData)
                ?.addOnSuccessListener {
                    callback.invoke(true) // Indicate success
                }
                ?.addOnFailureListener {
                    callback.invoke(false) // Indicate error occurred
                }
        } ?: run {
            callback.invoke(false) // Indicate error occurred
        }
    }

    fun addPlayerToTeamFirebase(player: PlayerData, callback: (Boolean) -> Unit) {
        databaseReference = firebaseDatabase?.getReference("TeamManagement");
        val currentUser = getLoggedInUser()
        currentUser?.let { user ->
            val currentUserUUID = user.uid
            val userTeamReference = databaseReference?.child(currentUserUUID)?.child("players")
            userTeamReference?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val playerList = dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<PlayerData>>() {})
                    if (playerList != null) {
                        playerList.add(player)
                        userTeamReference.setValue(playerList)
                        callback.invoke(true)
                    } else {
                        val newPlayerList = ArrayList<PlayerData>()
                        newPlayerList.add(player)
                        userTeamReference.setValue(newPlayerList)
                        callback.invoke(true)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.invoke(false)
                }
            })
        } ?: callback.invoke(false) // If currentUser is null, callback with false
    }

    fun registerTeamInTournament(tournamentId: String?, callback: (Boolean) -> Unit) {
        val tournamentRef = firebaseDatabase?.getReference("TournamentManagement")?.child("$tournamentId")
        val currentUser = getLoggedInUser()

        currentUser?.let { user ->
            val currentUserUUID = user.uid
            val userTeamReference = firebaseDatabase?.getReference("TeamManagement")?.child(currentUserUUID)

            userTeamReference?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Team exists, proceed with registration
                        val teamId = dataSnapshot.key
                        val teamData = dataSnapshot.value as? Map<String, Any>

                        // Store team data under the tournament management
                        teamData?.let { team ->
                            tournamentRef?.child("teams")?.child("$teamId")?.setValue(team)
                                ?.addOnSuccessListener {
                                    callback.invoke(true) // Indicate success
                                }
                                ?.addOnFailureListener {
                                    callback.invoke(false) // Indicate error occurred
                                }
                        }
                    } else {
                        callback.invoke(false) // Team does not exist, registration failed
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.invoke(false) // Indicate error occurred
                }
            })
        } ?: callback.invoke(false) // If currentUser is null, callback with false
    }

    fun getAllTournamentsFromFirebase(callback: (List<TournamentData>?) -> Unit) {
        databaseReference = firebaseDatabase?.getReference("TournamentManagement")

        databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tournamentList = mutableListOf<TournamentData>()

                dataSnapshot.children.forEach { tournamentSnapshot ->
                    val tournamentData = tournamentSnapshot.value as? Map<*, *>

                    // Check if tournament data exists
                    if (tournamentData != null) {
                        val tournamentId = tournamentData["tournamentId"] as? String ?: ""
                        val tournamentName = tournamentData["tournamentName"] as? String ?: ""
                        val tournamentLocation = tournamentData["tournamentLocation"] as? String ?: ""
                        val tournamentEntryFee = tournamentData["tournamentEntryFee"] as? String ?: ""
                        val tournamentWinningPrize = tournamentData["tournamentWinningPrize"] as? String ?: ""
                        val tournamentLogoBase64 = tournamentData["tournamentLogo"] as? String

                        // Convert base64 encoded team logo to Drawable

                        val tournament = TournamentData(
                            tournamentId=tournamentId,
                            tournamentLogo = tournamentLogoBase64,
                            tournamentName = tournamentName,
                            tournamentLocation = tournamentLocation,
                            tournamentEntryFee = tournamentEntryFee,
                            tournamentWinningPrize = tournamentWinningPrize
                        )

                        tournamentList.add(tournament)
                    }
                }

                // Invoke callback with the list of tournaments
                if (tournamentList.isNotEmpty()) {
                    callback.invoke(tournamentList)
                } else {
                    callback.invoke(null) // No tournament data found
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.invoke(null) // Indicate error occurred
            }
        })
    }


    fun getRegisteredTeamsInTournament(tournamentId: String?, callback: (List<TeamModel>?) -> Unit) {
        val tournamentRef = firebaseDatabase?.getReference("TournamentManagement")?.child("$tournamentId")?.child("teams")

        tournamentRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val teamsList = mutableListOf<TeamModel>()

                for (teamSnapshot in dataSnapshot.children) {
                    val teamData = teamSnapshot.getValue(TeamModel::class.java)
                    teamData?.let {
                        teamsList.add(it)
                    }
                }

                callback.invoke(teamsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.invoke(null) // Indicate error occurred
            }
        })
    }



    fun getListOfPlayersFromTeam(callback: (List<PlayerData>?) -> Unit) {
        databaseReference = firebaseDatabase?.getReference("TeamManagement");

        val currentUser = getLoggedInUser()
        currentUser?.let { user ->
            val currentUserUUID = user.uid
            val userTeamReference = databaseReference?.child(currentUserUUID)?.child("players")
            userTeamReference?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val playerList = dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<PlayerData>>() {})
                    callback.invoke(playerList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.invoke(null) // Indicates error occurred
                }
            })
        }
    }


    fun getLoggedInUser() : FirebaseUser?{
        val auth = FirebaseAuth.getInstance()
        val firebaseuser = auth.currentUser
        return firebaseuser

    }


    fun getTeamFromFirebase(callback: (TeamModel?) -> Unit) {
        databaseReference = firebaseDatabase?.getReference("TeamManagement");

        val currentUser = getLoggedInUser()
        currentUser?.let { user ->
            val currentUserUUID = user.uid
            val userTeamReference = databaseReference?.child(currentUserUUID)
            userTeamReference?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val teamData = dataSnapshot.value as? Map<*, *>
                        if (teamData != null) {
                            val teamId = teamData["teamId"] as String?:""
                            val teamName = teamData["teamName"] as? String ?: ""
                            val captainName = teamData["captainName"] as? String ?: ""
                            val city = teamData["city"] as? String ?: ""
                            val homeGround = teamData["homeGround"] as? String ?: ""
                            val teamLogoBase64 = teamData["teamLogo"] as? String

                            val team = TeamModel(
                                teamId=teamId,
                                teamLogo = teamLogoBase64,
                                teamName = teamName,
                                captainName = captainName,
                                city = city,
                                homeGround = homeGround
                            )

                            callback.invoke(team)
                        } else {
                            callback.invoke(null) // No team data found
                        }
                    } else {
                        callback.invoke(null) // No team data found
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.invoke(null) // Indicate error occurred
                }
            })
        } ?: callback.invoke(null) // If currentUser is null, callback with null
    }

/*
    fun registerOrganizedMatchesInTournament(tournamentId: String?, teamsList: List<TeamModel>?, callback: (Boolean) -> Unit) {
        val tournamentRef = firebaseDatabase?.getReference("TournamentManagement")?.child("$tournamentId")

        // Check if the tournament reference exists
        tournamentRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Tournament exists, proceed with registration
                    val tournamentData = dataSnapshot.value as? Map<String, Any>

                    // Store dummy team data under the tournament management
                    tournamentData?.let { tournament ->
                        val teamsNodeRef = tournamentRef.child("teams")
                        teamsList?.forEachIndexed { index, team ->
                            val teamId = "dummy_team_$index" // Generate a unique ID for each dummy team
                            teamsNodeRef.child(teamId).setValue(team)
                        }

                        callback.invoke(true) // Indicate success
                    }
                } else {
                    callback.invoke(false) // Tournament does not exist, registration failed
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.invoke(false) // Indicate error occurred
            }
        })
    }
*/



}