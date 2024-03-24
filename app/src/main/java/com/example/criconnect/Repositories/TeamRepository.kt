package com.example.criconnect.Repositories

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import com.example.criconnect.HelperClasses.base64ToDrawable
import com.example.criconnect.HelperClasses.drawableToBase64
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class TeamRepository(val context: Context) {

    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    init {
        getDatabaseReference()

    }

    fun getDatabaseReference() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase?.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase?.getReference("TeamManagement");


    }

    fun storeMatchesInDatabase(
        tournamentId: String?,
        matches: List<Pair<TeamModel, TeamModel>>,
        callback: (Boolean) -> Unit
    ) {
        val matchesRef = firebaseDatabase?.getReference("TournamentMatches")?.child("$tournamentId")
        matchesRef?.setValue(matches.map { it.toList() })?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun addTeamToFirebase(
        team: TeamModel,
        selectedDrawable: Drawable?,
        callback: (Boolean) -> Unit
    ) {
        databaseReference = firebaseDatabase?.getReference("TeamManagement")

        val currentUser = getLoggedInUser()
        currentUser?.let { user ->
            val currentUserUUID = user.uid
            val userTeamReference = databaseReference?.child(currentUserUUID)
            userTeamReference?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Team already exists, update its data
                        val drawable = selectedDrawable
                        val bitmap = (drawable as BitmapDrawable).bitmap
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                        val data = byteArrayOutputStream.toByteArray()

                        // Upload byte array to Firebase Storage
                        val storage = Firebase.storage
                        val storageReference = storage.reference
                        val fileName = "${currentUserUUID}.png" // Generate unique file name
                        val logoRef = storageReference.child("teamLogos/$fileName")

                        val uploadTask = logoRef.putBytes(data)
                        uploadTask.addOnSuccessListener { taskSnapshot ->
                            // Get the download URL of the uploaded logo
                            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                                val downloadUrl = uri.toString()

                                val teamData = hashMapOf(
                                    "teamId" to currentUserUUID,
                                    "teamName" to team.teamName,
                                    "captainName" to team.captainName,
                                    "city" to team.city,
                                    "homeGround" to team.homeGround,
                                    "teamLogo" to downloadUrl // Save the URL of the logo
                                )

                                // Update team data in Firebase
                                userTeamReference.updateChildren(teamData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        callback.invoke(true) // Indicate success
                                    }
                                    .addOnFailureListener {
                                        callback.invoke(false) // Indicate error occurred
                                    }
                            }.addOnFailureListener { exception ->
                                // Handle any errors retrieving the download URL
                                Log.e("FirebaseStorage", "Error getting download URL", exception)
                                callback.invoke(false) // Indicate error occurred
                            }
                        }.addOnFailureListener { exception ->
                            // Handle unsuccessful logo uploads
                            Log.e("FirebaseStorage", "Error uploading logo", exception)
                            callback.invoke(false) // Indicate error occurred
                        }
                    } else {
                        // Team does not exist, create a new entry
                        // Convert drawable to byte array
                        val drawable = selectedDrawable
                        val bitmap = (drawable as BitmapDrawable).bitmap
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                        val data = byteArrayOutputStream.toByteArray()

                        // Upload byte array to Firebase Storage
                        val storage = Firebase.storage
                        val storageReference = storage.reference
                        val fileName = "${currentUserUUID}.png" // Generate unique file name
                        val logoRef = storageReference.child("teamLogos/$fileName")

                        val uploadTask = logoRef.putBytes(data)
                        uploadTask.addOnSuccessListener { taskSnapshot ->
                            // Get the download URL of the uploaded logo
                            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                                val downloadUrl = uri.toString()

                                val teamData = hashMapOf(
                                    "teamId" to currentUserUUID,
                                    "teamName" to team.teamName,
                                    "captainName" to team.captainName,
                                    "city" to team.city,
                                    "homeGround" to team.homeGround,
                                    "teamLogo" to downloadUrl // Save the URL of the logo
                                )

                                // Save team data to Firebase
                                userTeamReference.setValue(teamData)
                                    .addOnSuccessListener {
                                        callback.invoke(true) // Indicate success
                                    }
                                    .addOnFailureListener {
                                        callback.invoke(false) // Indicate error occurred
                                    }
                            }.addOnFailureListener { exception ->
                                // Handle any errors retrieving the download URL
                                Log.e("FirebaseStorage", "Error getting download URL", exception)
                                callback.invoke(false) // Indicate error occurred
                            }
                        }.addOnFailureListener { exception ->
                            // Handle unsuccessful logo uploads
                            Log.e("FirebaseStorage", "Error uploading logo", exception)
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

    private fun updateTournamentsWithTeam(
        teamId: String,
        team: TeamModel,
        callback: (Boolean) -> Unit
    ) {
        val tournamentRef = FirebaseDatabase.getInstance().getReference("TournamentManagement")

        tournamentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { tournamentSnapshot ->
                    val tournamentId = tournamentSnapshot.key
                    tournamentSnapshot.child("teams").children.forEach { teamSnapshot ->
                        if (teamSnapshot.key == teamId) {
                            // Found the team in this tournament, update its attributes
                            val teamData = hashMapOf(
                                "teamName" to team.teamName,
                                "captainName" to team.captainName,
                                "city" to team.city,
                                "homeGround" to team.homeGround,
                                "teamLogo" to team.teamLogo // You may need to update the logo URL if it has changed
                            )

                            // Update team data in this tournament
                            teamSnapshot.ref.updateChildren(teamData as Map<String, Any>)
                                .addOnSuccessListener {
                                    // Success
                                }
                                .addOnFailureListener {
                                    // Failure
                                }
                        }
                    }
                }
                callback.invoke(true) // Indicate success after updating all tournaments
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.invoke(false) // Indicate error occurred
            }
        })
    }


    fun addTournamentToFirebase(
        tournament: TournamentData,
        selectedDrawable: Drawable?,
        callback: (Boolean) -> Unit
    ) {
        databaseReference = firebaseDatabase?.getReference("TournamentManagement");

        // Generate a unique ID for the tournament
        val tournamentId = databaseReference?.push()?.key

        tournamentId?.let { id ->
            tournament.tournamentId = id

            // Team already exists, update its data
            val drawable = selectedDrawable
            val bitmap = (drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val data = byteArrayOutputStream.toByteArray()

            // Upload byte array to Firebase Storage
            val storage = Firebase.storage
            val storageReference = storage.reference
            val fileName = "${id}.png" // Generate unique file name
            val logoRef = storageReference.child("tournamentLogos/$fileName")

            val uploadTask = logoRef.putBytes(data)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded logo
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    val tournamentData = hashMapOf(
                        "tournamentId" to id,
                        "tournamentName" to tournament.tournamentName,
                        "tournamentLocation" to tournament.tournamentLocation,
                        "tournamentEntryFee" to tournament.tournamentEntryFee,
                        "tournamentWinningPrize" to tournament.tournamentWinningPrize,
                        "tournamentLogo" to downloadUrl
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
        }
    }


    fun addPlayerToTeamFirebase(
        player: PlayerData,
        playerLogoDrawable: Drawable?,
        callback: (Boolean) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().getReference("TeamManagement")

        currentUser?.let { user ->
            val currentUserUUID = user.uid
            val userTeamReference = databaseReference.child(currentUserUUID).child("playerData")
            userTeamReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val playerList = dataSnapshot.getValue(object :
                        GenericTypeIndicator<ArrayList<PlayerData>>() {})
                    val newPlayerList = playerList ?: ArrayList()

                    // Generate unique key for the new player
                    val playerKey = userTeamReference.push().key ?: ""
                    player.playerId = playerKey

                    // Upload player logo to Firebase Storage
                    uploadPlayerLogo(playerLogoDrawable, playerKey) { success, logoUrl ->
                        if (success) {
                            player.playerLogo = logoUrl
                            // Add player data to the list
                            newPlayerList.add(player)
                            // Update the database with the new player list
                            userTeamReference.setValue(newPlayerList)
                                .addOnSuccessListener {
                                    // Update team in tournaments
                                    updateTeamInTournaments(
                                        currentUserUUID,
                                        newPlayerList
                                    ) { success ->
                                        if (success) {
                                            callback.invoke(true)
                                        } else {
                                            callback.invoke(false)
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    callback.invoke(false)
                                }
                        } else {
                            // If logo upload fails, invoke callback with false
                            callback.invoke(false)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.invoke(false)
                }
            })
        } ?: callback.invoke(false) // If currentUser is null, callback with false
    }

    private fun uploadPlayerLogo(
        playerLogoDrawable: Drawable?,
        playerKey: String,
        callback: (Boolean, String) -> Unit
    ) {
        val storageReference = FirebaseStorage.getInstance().reference.child("playersLogos")
        val fileName = "${playerKey}.png"
        val logoRef = storageReference.child(fileName)

        // Convert player logo drawable to bitmap
        val bitmap = (playerLogoDrawable as BitmapDrawable).bitmap

        // Convert bitmap to byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        // Upload byte array to Firebase Storage
        val uploadTask = logoRef.putBytes(data)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            logoRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val downloadUrl = downloadUri.toString()
                callback.invoke(true, downloadUrl) // Invoke callback with success and URL
            } else {
                // If logo upload fails, invoke callback with false
                Log.e("FirebaseStorage", "Error getting download URL", task.exception)
                callback.invoke(false, "")
            }
        }
    }

    fun registerTeamInTournament(tournamentId: String?, callback: (Boolean) -> Unit) {
        val tournamentRef =
            firebaseDatabase?.getReference("TournamentManagement")?.child("$tournamentId")
        val currentUser = getLoggedInUser()

        currentUser?.let { user ->
            val currentUserUUID = user.uid
            val userTeamReference =
                firebaseDatabase?.getReference("TeamManagement")?.child(currentUserUUID)

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
                        val tournamentLocation =
                            tournamentData["tournamentLocation"] as? String ?: ""
                        val tournamentEntryFee =
                            tournamentData["tournamentEntryFee"] as? String ?: ""
                        val tournamentWinningPrize =
                            tournamentData["tournamentWinningPrize"] as? String ?: ""
                        val tournamentLogo= tournamentData["tournamentLogo"] as? String

                        // Convert base64 encoded team logo to Drawable

                        val tournament = TournamentData(
                            tournamentId = tournamentId,
                            tournamentLogo = tournamentLogo,
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


    fun getRegisteredTeamsInTournament(
        tournamentId: String?,
        callback: (List<TeamModel>?) -> Unit
    ) {
        val tournamentRef =
            firebaseDatabase?.getReference("TournamentManagement")?.child("$tournamentId")
                ?.child("teams")

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
            val userTeamReference = databaseReference?.child(currentUserUUID)?.child("playerData")
            userTeamReference?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val playerList = dataSnapshot.getValue(object :
                        GenericTypeIndicator<ArrayList<PlayerData>>() {})
                    callback.invoke(playerList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.invoke(null) // Indicates error occurred
                }
            })
        }
    }


    fun getLoggedInUser(): FirebaseUser? {
        val auth = FirebaseAuth.getInstance()
        val firebaseuser = auth.currentUser
        return firebaseuser

    }


    fun getTeamFromFirebase(callback: (TeamModel?, Boolean) -> Unit) {
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
                            val teamId = teamData["teamId"] as String ?: ""
                            val teamName = teamData["teamName"] as? String ?: ""
                            val captainName = teamData["captainName"] as? String ?: ""
                            val city = teamData["city"] as? String ?: ""
                            val homeGround = teamData["homeGround"] as? String ?: ""
                            val teamLogo = teamData["teamLogo"] as String ?: ""

                            val team = TeamModel(
                                teamId = teamId,
                                teamLogo = teamLogo,
                                teamName = teamName,
                                captainName = captainName,
                                city = city,
                                homeGround = homeGround
                            )

                            callback.invoke(team, true)
                        } else {
                            callback.invoke(null, false) // No team data found
                        }
                    } else {
                        callback.invoke(null, false) // No team data found
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.invoke(null, false) // Indicate error occurred
                }
            })
        } ?: callback.invoke(null, false) // If currentUser is null, callback with null
    }

    fun deletePlayerFromTeam(playerId: String?, callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().getReference("TeamManagement")

        currentUser?.let { user ->
            val currentUserUUID = user.uid
            val userTeamReference = databaseReference.child(currentUserUUID).child("playerData")

            userTeamReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val playerList = dataSnapshot.getValue(object :
                        GenericTypeIndicator<ArrayList<PlayerData>>() {})

                    // Check if the playerList is not null
                    if (playerList != null) {
                        // Find the player with the given playerId
                        val filteredList = playerList.filter { it.playerId == playerId }

                        // Check if the player exists
                        if (filteredList.isNotEmpty()) {
                            playerList.removeAll(filteredList)

                            // Update the database with the new player list
                            userTeamReference.setValue(playerList)
                                .addOnSuccessListener {
                                    // If deletion from team is successful, update team in tournaments
                                    updateTeamInTournaments(
                                        currentUserUUID,
                                        playerList
                                    ) { success ->
                                        if (success) {
                                            callback.invoke(true)
                                        } else {
                                            callback.invoke(false)
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    callback.invoke(false)
                                }
                        } else {
                            // Player not found
                            callback.invoke(false)
                        }
                    } else {
                        // Player list is null
                        callback.invoke(false)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback.invoke(false)
                }
            })
        } ?: callback.invoke(false) // If currentUser is null, callback with false
    }


    private fun updateTeamInTournaments(
        teamId: String,
        playerList: ArrayList<PlayerData>,
        callback: (Boolean) -> Unit
    ) {
        val tournamentRef = FirebaseDatabase.getInstance().getReference("TournamentManagement")

        tournamentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { tournamentSnapshot ->
                    val tournamentId = tournamentSnapshot.key
                    tournamentSnapshot.child("teams").children.forEach { teamSnapshot ->
                        if (teamSnapshot.key == teamId) {
                            // Get existing team data
                            val existingTeamData = teamSnapshot.getValue(TeamModel::class.java)

                            // Update only the player list
                            existingTeamData?.let { team ->
                                team.playerData =
                                    playerList // Assuming "players" is the correct field name
                                // Update team in this tournament
                                teamSnapshot.ref.setValue(team)
                                    .addOnSuccessListener {
                                        // Success
                                    }
                                    .addOnFailureListener {
                                        // Failure
                                    }
                            }
                        }
                    }
                }
                callback.invoke(true) // Indicate success after updating all tournaments
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.invoke(false) // Indicate error occurred
            }
        })
    }


}