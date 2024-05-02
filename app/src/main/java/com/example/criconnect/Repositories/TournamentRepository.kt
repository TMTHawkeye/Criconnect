package com.example.criconnect.Repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.tournamentDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class TournamentRepository(val context: Context) {

    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    init {
        getDatabaseReference()
    }

    fun getLoggedInUser(): FirebaseUser? {
        val auth = FirebaseAuth.getInstance()
        val firebaseuser = auth.currentUser
        return firebaseuser

    }

    fun getDatabaseReference() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase?.getReference("TournamentDetails");
    }

    fun registerTournament(
        selectedDrawable: Drawable?,
        tournamentData: tournamentDataClass,
        callback: (Boolean) -> Unit
    ) {
        val currentUser = getLoggedInUser()
        currentUser?.let { user ->
            // Generate a unique key for the tournament
            val tournamentId = user.uid
            tournamentData.tournamentId = tournamentId
            // Check if the generated key is not null
            tournamentId?.let {
                val tournamentRef = databaseReference?.child("RegisteredTournaments")?.child(it)
                tournamentData.tournamentId = it


                val drawable = selectedDrawable
                val bitmap = (drawable as BitmapDrawable).bitmap
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val data = byteArrayOutputStream.toByteArray()

                // Upload byte array to Firebase Storage
                val storage = Firebase.storage
                val storageReference = storage.reference
                val fileName = "${it}.png" // Generate unique file name
                val logoRef = storageReference.child("tournamentLogos/$fileName")

                val uploadTask = logoRef.putBytes(data)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // Get the download URL of the uploaded logo
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        tournamentRef?.child("tournamentId")?.setValue(tournamentId)
                        tournamentRef?.child("tournamentLogo")?.setValue(downloadUrl)
                        tournamentRef?.child("name")?.setValue(tournamentData.name)
                        tournamentRef?.child("organizerName")
                            ?.setValue(tournamentData.organizerName)
                        tournamentRef?.child("city")?.setValue(tournamentData.city)
                        tournamentRef?.child("ground")?.setValue(tournamentData.ground)
                        tournamentRef?.child("organizerEmail")
                            ?.setValue(tournamentData.organizerEmail)
                        tournamentRef?.child("eventStartDate")
                            ?.setValue(tournamentData.eventStartDate)
                        tournamentRef?.child("eventEndDate")?.setValue(tournamentData.eventEndDate)
                        tournamentRef?.child("ballType")?.setValue(tournamentData.ballType)
                        tournamentRef?.child("pitchType")?.setValue(tournamentData.pitchType)
                        tournamentRef?.child("details")?.setValue(tournamentData.details)

                        tournamentRef?.setValue(tournamentData)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                callback.invoke(true)
                            } else {
                                callback.invoke(false)
                            }
                        }
                    }
                }
            }
        }
    }

    fun getTournaments(callback: (List<tournamentDataClass>?) -> Unit) {
        // Reference to the "RegisteredTournaments" node
        val tournamentsRef = databaseReference?.child("RegisteredTournaments")

        // Listener to fetch data
        tournamentsRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tournamentsList = mutableListOf<tournamentDataClass>()

                // Iterate through each child node
                for (tournamentSnapshot in snapshot.children) {
                    // Deserialize the tournament data from snapshot
                    val tournamentData =
                        tournamentSnapshot.getValue(tournamentDataClass::class.java)
                    tournamentData?.let {
                        tournamentsList.add(it)
                    }
                }

                // Invoke the callback with the list of tournaments
                callback.invoke(tournamentsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors here
                callback.invoke(null)
            }
        })
    }


    fun getRequestingTeams(callback: (ArrayList<TeamModel>) -> Unit) {
        val currentUser = getLoggedInUser()
        currentUser?.let { user ->
            val tournamentRef =
                firebaseDatabase?.getReference("TournamentDetails")?.child("RegisteredTournaments")
                    ?.child(user.uid)

            // Initialize a list to hold the team details
            val teamList = mutableListOf<TeamModel>()

            tournamentRef?.child("requestsList")
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val teamIds = mutableListOf<String>()
                            for (teamSnapshot in dataSnapshot.children) {
                                val teamId = teamSnapshot.getValue(String::class.java)
                                teamId?.let {
                                    teamIds.add(it)
                                }
                            }
                            // Now fetch team details for each teamId
                            fetchTeamDetails(teamIds) { teams ->
                                // Invoke the callback function with the list of team details
                                callback.invoke(teams)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle onCancelled event
                    }
                })
        }
    }

    private fun fetchTeamDetails(teamIds: List<String>, callback: (ArrayList<TeamModel>) -> Unit) {
        val teamList = ArrayList<TeamModel>()

        val teamManagementRef = firebaseDatabase?.getReference("TeamManagement")

        teamManagementRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (teamSnapshot in dataSnapshot.children) {
                    val teamId = teamSnapshot.key
                    if (teamIds.contains(teamId)) {
                        val teamData = teamSnapshot.getValue(TeamModel::class.java)
                        teamData?.let {
                            teamList.add(it)
                        }
                    }
                }
                // Invoke the callback function with the list of team details
                callback.invoke(teamList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }

    fun removeFromRequestsList(teamId: String?, callback: (Boolean) -> Unit) {
        val currentUser = getLoggedInUser()
        currentUser?.let { user ->
            val tournamentRef =
                firebaseDatabase?.getReference("TournamentDetails")?.child("RegisteredTournaments")
                    ?.child(user.uid)

            // Remove the teamId from the requestsList array
            tournamentRef?.child("requestsList")
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val requestsList = dataSnapshot.getValue(object :
                                GenericTypeIndicator<ArrayList<String>>() {})
                            requestsList?.remove(teamId)

                            // Update the requestsList in Firebase
                            tournamentRef.child("requestsList").setValue(requestsList)
                                .addOnSuccessListener {
                                    tournamentRef.child("teams").child("${teamId}").setValue(teamId)
                                        .addOnSuccessListener {
                                            callback.invoke(true) // Indicate success
                                        }
                                        .addOnFailureListener {
                                            callback.invoke(false) // Indicate error occurred
                                        }
                                 }
                                .addOnFailureListener {
                                    callback.invoke(false)
                                }


                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle onCancelled event
                    }
                })
        }
    }

    fun getTournamentById( callback: (tournament: tournamentDataClass?) -> Unit) {

        val currentUser = getLoggedInUser()
        currentUser?.let { user ->
            // Reference to the specific tournament using its ID
            val tournamentRef =
                databaseReference?.child("RegisteredTournaments")?.child("${currentUser.uid}")

            // Listener to fetch the tournament data
            tournamentRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Deserialize the tournament data from the snapshot
                    val tournamentData = snapshot.getValue(tournamentDataClass::class.java)
                    // Invoke the callback with the tournament data
                    callback.invoke(tournamentData)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors here
                    callback.invoke(null)
                }
            })
        }
    }



}