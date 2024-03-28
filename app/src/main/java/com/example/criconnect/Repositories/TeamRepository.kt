package com.example.criconnect.Repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import com.example.criconnect.ModelClasses.MatchModel
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
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.UUID


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
        matches: List<MatchModel>,
        callback: (Boolean) -> Unit
    ) {
        val matchesRef = firebaseDatabase?.getReference("TournamentMatches")?.child("$tournamentId")
        matchesRef?.setValue(matches)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun getMatchesForTournament(tournamentId: String?, callback: (List<MatchModel>?) -> Unit) {
        val tournamentMatchesRef =
            firebaseDatabase?.getReference("TournamentMatches")?.child("$tournamentId")
                ?.child("matches")

        tournamentMatchesRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val matches = mutableListOf<MatchModel>()

                for (matchSnapshot in dataSnapshot.children) {
                    val matchId = matchSnapshot.key
                    val matchData = matchSnapshot.value as? Map<String, Any>
                    val teamAId = matchData?.get("teamAId") as? String ?: ""
                    val teamBId = matchData?.get("teamBId") as? String ?: ""
                    val winnerId = matchData?.get("winnerId") as? String ?: ""
                    val startDate = matchData?.get("startDate") as? String ?: ""

                    val matchModel =
                        MatchModel(matchId ?: "", teamAId, teamBId, winnerId, startDate)
                    matches.add(matchModel)
                }

                callback.invoke(matches)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.invoke(null)
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
                        "tournamentLogo" to downloadUrl,
                        "teams" to tournament.teamList
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
                                    callback(true)
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
            val userTeamReference =
                firebaseDatabase?.getReference("TeamManagement")?.child(user.uid)

            userTeamReference?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Team exists, proceed with registration
                        val teamId = dataSnapshot.key // Assuming team ID is the key
                        tournamentRef?.child("teams")?.child(teamId!!)?.setValue(teamId)
                            ?.addOnSuccessListener {
                                callback.invoke(true) // Indicate success
                            }
                            ?.addOnFailureListener {
                                callback.invoke(false) // Indicate error occurred
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
                        val tournamentId =
                            tournamentSnapshot.child("tournamentId").getValue(String::class.java)
                        val tournamentName =
                            tournamentSnapshot.child("tournamentName").getValue(String::class.java)
                        val tournamentLocation =
                            tournamentSnapshot.child("tournamentLocation")
                                .getValue(String::class.java)
                        val tournamentEntryFee =
                            tournamentSnapshot.child("tournamentEntryFee")
                                .getValue(String::class.java)
                        val tournamentWinningPrize =
                            tournamentSnapshot.child("tournamentWinningPrize")
                                .getValue(String::class.java)
                        val tournamentLogo =
                            tournamentSnapshot.child("tournamentLogo").getValue(String::class.java)
                        val teams =
                            ArrayList<String>()

                        tournamentSnapshot.child("teams").children.forEach { teamSnapshot ->
                            val team = teamSnapshot.getValue(String::class.java)
                            team?.let {
                                teams.add(it)
                            }
                        }

                        // Convert base64 encoded team logo to Drawable

                        val tournament = TournamentData(
                            tournamentId = tournamentId,
                            tournamentLogo = tournamentLogo,
                            tournamentName = tournamentName,
                            tournamentLocation = tournamentLocation,
                            tournamentEntryFee = tournamentEntryFee,
                            tournamentWinningPrize = tournamentWinningPrize,
                            teamList = teams
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

        tournamentRef?.child("teams")?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val teamIds = mutableListOf<String>()

                for (teamSnapshot in dataSnapshot.children) {
                    val teamId = teamSnapshot.key
                    teamId?.let { teamIds.add(it) }
                }

                if (teamIds.isNotEmpty()) {
                    fetchTeamsDetails(teamIds, callback)
                } else {
                    callback.invoke(emptyList())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.invoke(null)
            }
        })
    }

    private fun fetchTeamsDetails(teamIds: List<String>, callback: (List<TeamModel>?) -> Unit) {
        val teamsReference = firebaseDatabase?.getReference("TeamManagement")

        val teamsQuery = teamsReference?.orderByKey()

        teamsQuery?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val teams = mutableListOf<TeamModel>()

                for (teamSnapshot in dataSnapshot.children) {
                    val teamId = teamSnapshot.key
                    // Check if the teamId exists in the list of teamIds
                    if (teamIds.contains(teamId)) {
                        val teamData = teamSnapshot.value as? Map<String, Any>
                        val teamModel =
                            parseTeamModel(teamId, teamData) // You need to define this method
                        teamModel?.let { teams.add(it) }
                    }
                }

                callback.invoke(teams)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.invoke(null)
            }
        })
    }


    fun parseTeamModel(teamId: String?, teamData: Map<String, Any>?): TeamModel? {
        // Ensure teamData is not null
        if (teamData == null) {
            return null
        }

        // Extract attributes from teamData map
        val teamLogo = teamData["teamLogo"] as? String
        val teamName = teamData["teamName"] as? String
        val captainName = teamData["captainName"] as? String
        val city = teamData["city"] as? String
        val homeGround = teamData["homeGround"] as? String
        val playerDataList = teamData["playerData"] as? List<Map<String, Any>>
        val wins = (teamData["wins"] as? Long)?.toInt() ?: 0
        val loss = (teamData["loss"] as? Long)?.toInt() ?: 0

        // Ensure teamName is not null or empty
        if (teamName.isNullOrEmpty()) {
            return null
        }

        // Convert playerDataList to ArrayList<PlayerData> if not null
        val playerData = playerDataList?.map { playerDataMap ->
            // Parse each player data map into PlayerData object
            val playerId = playerDataMap["playerId"] as? String ?: ""
            val playerName = playerDataMap["playerName"] as? String ?: ""
            val playerAge = (playerDataMap["playerAge"] as? String) ?: ""
            val playerCity = (playerDataMap["playerCity"] as? String) ?: ""
            val playerLogo = (playerDataMap["playerLogo"] as? String) ?: ""
            val batsmanhand = (playerDataMap["batsmanhand"] as? String) ?: ""
            val details = (playerDataMap["details"] as? String) ?: ""
            val fatherName = (playerDataMap["fatherName"] as? String) ?: ""
            val speciality = (playerDataMap["speciality"] as? String) ?: ""
            val playerPhone = (playerDataMap["playerPhone"] as? String) ?: ""

            // Create PlayerData object
            PlayerData(
                playerId = playerId,
                playerName = playerName,
                playerAge = playerAge,
                playerPhone = playerPhone,
                playerCity = playerCity,
                playerLogo = playerLogo,
                batsmanhand = batsmanhand,
                details = details,
                fatherName = fatherName,
                speciality = speciality
            )
        }?.toCollection(ArrayList())

        // Create and return a new TeamModel object
        return TeamModel(
            teamId = teamId,
            teamLogo = teamLogo,
            teamName = teamName,
            captainName = captainName ?: "",
            city = city ?: "",
            homeGround = homeGround ?: "",
            playerData = playerData,
            wins = wins,
            loss = loss
        )
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
                            val wins = (teamData["wins"] as? Long?)?.toInt()?: 0
                            val loss = (teamData["loss"] as? Long?)?.toInt() ?: 0

                            val team = TeamModel(
                                teamId = teamId,
                                teamLogo = teamLogo,
                                teamName = teamName,
                                captainName = captainName,
                                city = city,
                                homeGround = homeGround,
                                wins = wins,
                                loss = loss
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
                                    callback.invoke(true)

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

    fun addTeamToFirebase(
        team: TeamModel?,
        selectedDrawable: Drawable?,
        callback: (Boolean) -> Unit
    ) {
        val databaseReference = firebaseDatabase?.getReference("TeamManagement")

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
                                team?.teamId = currentUserUUID
                                team?.teamLogo = downloadUrl

                                val teamData = hashMapOf(
                                    "teamId" to currentUserUUID,
                                    "teamName" to team?.teamName,
                                    "captainName" to team?.captainName,
                                    "city" to team?.city,
                                    "homeGround" to team?.homeGround,
                                    "teamLogo" to downloadUrl
                                )

                                // Update team data in Firebase
                                userTeamReference.updateChildren(teamData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        callback.invoke(true)

                                    }
                                    .addOnFailureListener {
                                        // Indicate error occurred
                                        callback.invoke(false)
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
                                team?.teamId = currentUserUUID
                                team?.teamLogo = downloadUrl

                                val teamData = hashMapOf(
                                    "teamId" to currentUserUUID,
                                    "teamName" to team?.teamName,
                                    "captainName" to team?.captainName,
                                    "city" to team?.city,
                                    "homeGround" to team?.homeGround,
                                    "teamLogo" to downloadUrl,
                                    "wins" to team?.wins,
                                    "loss" to team?.loss
                                )

                                // Save team data to Firebase
                                userTeamReference.setValue(teamData)
                                    .addOnSuccessListener {
                                        callback.invoke(true)
                                    }
                                    .addOnFailureListener {
                                        // Indicate error occurred
                                        callback.invoke(false)
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


    fun getSelectedTeam(teamId: String?, callback: (TeamModel?) -> Unit) {
        // Reference to the specific team in the TeamManagement node
        val teamRef = firebaseDatabase?.getReference("TeamManagement")?.child(teamId ?: "")

        // Check if the teamRef is not null and the teamId is not null or empty
        if (teamRef != null && !teamId.isNullOrEmpty()) {
            // Query to fetch the selected team's details based on its ID
            teamRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if the team data exists
                    if (dataSnapshot.exists()) {
                        // Retrieve team data as a map
                        val teamData = dataSnapshot.value as? Map<String, Any>
                        // Parse the team data into a TeamModel object
                        val selectedTeam = parseTeamModel(teamId, teamData)
                        // Invoke the callback with the selected team
                        callback.invoke(selectedTeam)
                    } else {
                        // No data found for the selected team ID
                        callback.invoke(null)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    callback.invoke(null)
                }
            })
        } else {
            // Invalid input (null team ID)
            callback.invoke(null)
        }
    }

   /* fun saveMatchesToFirebase(
        tournamentId: String?,
        matches: List<MatchModel>?,
        callback: (Boolean) -> Unit
    ) {
        val tournamentMatchesRef =
            firebaseDatabase?.getReference("TournamentMatches")?.child("$tournamentId")
                ?.child("matches")

        // Counter to keep track of successful match saves
        var savedCount = 0
        var matchesReturnedList = ArrayList<MatchModel>()

        matches?.forEachIndexed { index, match ->
            // Generate a unique match ID
            val matchId = generateUniqueMatchId()

            // Create a map with match data
            val matchData = mapOf(
                "teamAId" to match.teamAId,
                "teamBId" to match.teamBId,
                "winnerId" to match.winnerId,
                "startDate" to match.startDate,
                "matchId" to matchId
            )

            // Save match data to Firebase
            tournamentMatchesRef?.child(matchId)?.setValue(matchData)
                ?.addOnSuccessListener {
                    savedCount++

                    // Check if all matches were saved successfully
                    if (savedCount == matches?.size) {
                        callback.invoke(false) // All matches saved successfully
                    }
                }
                ?.addOnFailureListener {
                    callback.invoke(false) // Failed to save matches
                }
        }
    }
*/

    fun saveMatchesToFirebase(
        tournamentId: String?,
        matches: List<MatchModel>?,
        callback: (Boolean, List<MatchModel>) -> Unit
    ) {
        val tournamentMatchesRef =
            firebaseDatabase?.getReference("TournamentMatches")?.child("$tournamentId")
                ?.child("matches")

        if (tournamentMatchesRef == null) {
            // Error getting reference to tournament matches
            callback.invoke(false, emptyList())
            return
        }
        // Counter to keep track of successful match saves
        var savedCount = 0
        var matchesReturnedList = ArrayList<MatchModel>()

        matches?.forEachIndexed { index, match ->
            // Generate a unique match ID
            val matchId = generateUniqueMatchId()

            // Create a map with match data
            val matchData = mapOf(
                "teamAId" to match.teamAId,
                "teamBId" to match.teamBId,
                "winnerId" to match.winnerId,
                "startDate" to match.startDate,
                "matchId" to matchId
            )

            // Save match data to Firebase
            tournamentMatchesRef?.child(matchId)?.setValue(matchData)
                ?.addOnSuccessListener {
                    savedCount++
                    matchesReturnedList.add(match.copy(matchId = matchId)) // Update matchId and add to the list

                    // Check if all matches were saved successfully
                    if (savedCount == matches?.size) {
                        callback.invoke(true, matchesReturnedList) // All matches saved successfully
                    }
                }
                ?.addOnFailureListener {
                    callback.invoke(false, emptyList()) // Failed to save matches
                }
        }
    }

    fun updateMatchWinner(
        tournamentId: String?,
        matchId: String?,
        winnerId: String?,
        callback: (Boolean) -> Unit
    ) {
        if (tournamentId.isNullOrEmpty() || matchId.isNullOrEmpty() || winnerId.isNullOrEmpty()) {
            // Ensure that all required parameters are provided
            callback.invoke(false)
            return
        }

        val tournamentMatchesRef =
            firebaseDatabase?.getReference("TournamentMatches")?.child(tournamentId)?.child("matches")
                ?.child(matchId)

        if (tournamentMatchesRef == null) {
            // Error getting reference to the match
            callback.invoke(false)
            return
        }

        tournamentMatchesRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                // Get the current winner ID as a HashMap
                val currentWinnerIdMap = currentData.getValue() as? HashMap<*, *>

                // Check if the value retrieved is a HashMap
                if (currentWinnerIdMap != null) {
                    // Extract the winnerId from the HashMap
                    val currentWinnerId = currentWinnerIdMap["winnerId"] as? String

                    // Handle the case where winnerId is null or not a String
                    if (currentWinnerId != null) {
                        // Update winnerId only if it's different from the new winnerId
                        if (currentWinnerId != winnerId) {
                            // Create a new HashMap with updated winnerId
                            val updatedWinnerIdMap = hashMapOf("winnerId" to winnerId)
                            // Set the value in MutableData
                            currentData.value = updatedWinnerIdMap
                            return Transaction.success(currentData)
                        } else {
                            // No need to update, return as success
                            return Transaction.success(currentData)
                        }
                    }
                } else {
                    // Handle the case where the value retrieved is not a HashMap
                    // Set the winnerId directly
                    currentData.value = winnerId
                    return Transaction.success(currentData)
                }

                // Return success by default
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                callback.invoke(committed)
            }
        })
    }


    // Function to generate a unique match ID
    private fun generateUniqueMatchId(): String {
        return UUID.randomUUID().toString() // Generate a UUID as a unique match ID
    }


    fun updateTeamStats(winnerId: String?, loserId: String?, callback: (Boolean) -> Unit) {
        if (winnerId != null && loserId != null) {
            // Update winner's wins
            firebaseDatabase?.getReference("TeamManagement")?.child(winnerId)?.child("wins")
                ?.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(data: MutableData): Transaction.Result {
                        val currentWins = data.getValue(Int::class.java) ?: 0
                        data.value = currentWins + 1
                        return Transaction.success(data)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        dataSnapshot: DataSnapshot?
                    ) {
                        callback.invoke(committed)
                    }
                })

            // Update loser's losses
            firebaseDatabase?.getReference("TeamManagement")?.child(loserId)?.child("loss")
                ?.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(data: MutableData): Transaction.Result {
                        val currentLosses = data.getValue(Int::class.java) ?: 0
                        data.value = currentLosses + 1
                        return Transaction.success(data)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        dataSnapshot: DataSnapshot?
                    ) {
                        // Ignoring the result for loser's transaction
                    }
                })
        }
    }


    fun getAllTeamsFromFirebase(callback: (List<TeamModel>) -> Unit) {
        val databaseReference = firebaseDatabase?.getReference("TeamManagement")

        databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val teams = mutableListOf<TeamModel>()
                for (teamSnapshot in dataSnapshot.children) {
                    val teamData = teamSnapshot.value as HashMap<*, *>
                    val teamId = teamData["teamId"] as String
                    val teamName = teamData["teamName"] as String
                    val captainName = teamData["captainName"] as String
                    val city = teamData["city"] as String
                    val homeGround = teamData["homeGround"] as String
                    val teamLogo = teamData["teamLogo"] as String
                    val wins = teamData["wins"] as Long
                    val loss = teamData["loss"] as Long

                    val team = TeamModel(teamId=teamId,teamName= teamName, captainName =  captainName, city =  city, homeGround =  homeGround,teamLogo= teamLogo, wins = wins.toInt(),loss= loss.toInt())
                    teams.add(team)
                }
                callback.invoke(teams)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                callback.invoke(emptyList()) // Return empty list in case of error
            }
        })
    }


}