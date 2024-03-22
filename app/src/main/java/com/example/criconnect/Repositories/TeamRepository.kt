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
                        // Convert team logo to base64 encoded string
                        val teamLogoBase64 = team.teamLogo?.let { drawableToBase64(it) }

                        // Create a map to represent the team data
                        val teamData = hashMapOf(
                            "teamName" to team.teamName,
                            "captainName" to team.captainName,
                            "city" to team.city,
                            "homeGround" to team.homeGround,
                            "teamLogo" to teamLogoBase64
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
            val tournamentLogoBase64 = tournament.tournamentLogo?.let { drawableToBase64(it) }

            val tournamentData = hashMapOf(
                "tournamentName" to tournament.tournamentName,
                "tournamentLocation" to tournament.tournamentLocation,
                "tournamentEntryFee" to tournament.tournamentEntryFee,
                "tournamentWinningPrize" to tournament.tournamentWinningPrize,
                "tournamentLogo" to tournamentLogoBase64
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

    fun getAllTournamentsFromFirebase(callback: (List<TournamentData>?) -> Unit) {
        databaseReference = firebaseDatabase?.getReference("TournamentManagement")

        databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tournamentList = mutableListOf<TournamentData>()

                dataSnapshot.children.forEach { tournamentSnapshot ->
                    val tournamentData = tournamentSnapshot.value as? Map<*, *>

                    // Check if tournament data exists
                    if (tournamentData != null) {
                        val tournamentName = tournamentData["tournamentName"] as? String ?: ""
                        val tournamentLocation = tournamentData["tournamentLocation"] as? String ?: ""
                        val tournamentEntryFee = tournamentData["tournamentEntryFee"] as? String ?: ""
                        val tournamentWinningPrize = tournamentData["tournamentWinningPrize"] as? String ?: ""
                        val tournamentLogoBase64 = tournamentData["tournamentLogo"] as? String

                        // Convert base64 encoded team logo to Drawable
                        val teamLogoDrawable = tournamentLogoBase64?.let { base64ToDrawable(it) }

                        val tournament = TournamentData(
                            tournamentLogo = teamLogoDrawable,
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



    private fun drawableToBase64(drawable: Drawable): String {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
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


    /*   fun getTeamId(teamName: String, captainName: String, city: String, homeGround: String, callback: (String?) -> Unit) {
           val query = databaseReference?.orderByChild("teamName")?.equalTo(teamName)
           query?.addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(dataSnapshot: DataSnapshot) {
                   for (snapshot in dataSnapshot.children) {
                       val team = snapshot.getValue(TeamModel::class.java)
                       if (team != null && team.captainName == captainName && team.city == city && team.homeGround == homeGround) {
                           callback(snapshot.key) // Passes the team ID to the callback function
                           return
                       }
                   }
                   callback(null) // Indicates team not found
               }

               override fun onCancelled(databaseError: DatabaseError) {
                   // Handle error
                   callback(null) // Indicates error occurred
               }
           })
       }*/

    private fun getLoggedInUser() : FirebaseUser?{
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
                            val teamName = teamData["teamName"] as? String ?: ""
                            val captainName = teamData["captainName"] as? String ?: ""
                            val city = teamData["city"] as? String ?: ""
                            val homeGround = teamData["homeGround"] as? String ?: ""
                            val teamLogoBase64 = teamData["teamLogo"] as? String

                            // Convert base64 encoded team logo to Drawable
                            val teamLogoDrawable = teamLogoBase64?.let { base64ToDrawable(it) }

                            val team = TeamModel(
                                teamLogo = teamLogoDrawable,
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

    // Function to convert base64 encoded string to Drawable
    private fun base64ToDrawable(base64: String): Drawable? {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        return BitmapDrawable(Resources.getSystem(), BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size))
    }




}