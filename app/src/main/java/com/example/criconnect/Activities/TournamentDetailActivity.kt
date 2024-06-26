package com.example.criconnect.Activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.Adapters.RegisteredTeamsAdapter
import com.example.criconnect.HelperClasses.Constants
import com.example.criconnect.HelperClasses.Constants.dashboardCaptain
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ModelClasses.tournamentDataClass


import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityTournamentDetailBinding
import com.google.android.material.snackbar.Snackbar
import io.paperdb.Paper
import org.koin.androidx.viewmodel.ext.android.viewModel
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import java.io.Serializable

class TournamentDetailActivity : AppCompatActivity(), Serializable {
    lateinit var binding: ActivityTournamentDetailBinding

    val dataViewModel: TeamViewModel by viewModel()
    var selectedTournament: tournamentDataClass? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTournamentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedTournament =
            intent?.getSerializableExtra("selectedTournament") as tournamentDataClass?
        Log.d("TAGTournamentid", "onCreate: ${selectedTournament?.tournamentId}")

        if (selectedTournament != null) {
            setTournamentAttributes()
//            getRegisteredTeamsInTournament()
        } else {
            Toast.makeText(this@TournamentDetailActivity, "No data Found", Toast.LENGTH_SHORT)
                .show()
            binding.teamsRV.visibility = View.GONE
            binding.btnOrganizeMatches.visibility = View.GONE
//            binding.registerTeamId.visibility=View.GONE
        }

        binding.registerTeamId.setOnClickListener {
            val dialog = ProgressDialog.show(
                this@TournamentDetailActivity, "",
                "Sending request, Please Wait... ", true
            )
//            registerTeam()
            sendRegisterRequest(dialog)
        }

        binding.btnOrganizeMatches.setOnClickListener {
//            Log.d("TAGttt", "onCreate: ${selectedTournament?.teamList?.size}")
            startActivity(
                Intent(this@TournamentDetailActivity, OrganizeMatchesActivity::class.java)
                    .putExtra("selectedTournament", selectedTournament)
//                    .putExtra("tournamentOwnerID", selectedTournament?.tournamentOwnerTeam)
//                    .putExtra("tournamentName",selectedTournament?.name)
            )

        }

        binding.backbtnId.setOnClickListener {
            finish()
        }


//        val config = ShowcaseConfig()
//        val sequence =
//            MaterialShowcaseSequence(this@TournamentDetailActivity, getString(R.string.matches))

//        if(binding.btnOrganizeMatches.isVisible) {
//        sequence.addSequenceItem(
//            MaterialShowcaseView.Builder(this@TournamentDetailActivity)
//                .setTarget(binding.btnOrganizeMatches)
//                .setContentText(getString(R.string.matches_button_showcase_value))
//                .setDismissText(getString(R.string.got_it))
//                .setMaskColour(getColor(R.color.dark_red))
//                .build()
//        )
//        }
//        if(binding.registerTeamId.isVisible) {
//            sequence.addSequenceItem(
//                MaterialShowcaseView.Builder(this@TournamentDetailActivity)
//                    .setTarget(binding.registerTeamId)
//                    .setContentText(getString(R.string.registerDetails))
//                    .setDismissText(getString(R.string.got_it))
//                    .setMaskColour(getColor(R.color.dark_red))
//                    .build()
//            )
//        }

//        sequence.start()



    }

    private fun setTournamentAttributes() {
        loadImage()
        binding.titleTournament.text = selectedTournament?.name
        binding.locationId.text = selectedTournament?.ground
        binding.entryId.text = selectedTournament?.ballType
        binding.winningId.text = selectedTournament?.organizerEmail
        binding.organizerNameId.text = selectedTournament?.organizerName
        binding.winningPrizeId.text = selectedTournament?.tournamentWinningPrize
        binding.eventStartId.text = selectedTournament?.eventStartDate
        binding.eventEndId.text = selectedTournament?.eventEndDate
        binding.pitchTypeId.text = selectedTournament?.pitchType
        binding.detailsId.text = selectedTournament?.details
    }

    fun loadImage() {
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.no_image_found)
            .error(R.drawable.no_image_found)

        Glide.with(this@TournamentDetailActivity)
            .load(selectedTournament?.tournamentLogo)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .apply(options)
            .into(binding.tournamentImageId)

    }

    private fun registerTeam() {
        binding.progressBar.visibility = View.VISIBLE
        dataViewModel.registerTeamInTournament(selectedTournament?.tournamentId) {
            if (it) {
                dataViewModel.getRegisteredTeamsInTournament(selectedTournament?.tournamentId) { registeredList ->
                    Log.d("TAGregisteredListSize", "registerTeam: ${registeredList?.size}")
                    binding.progressBar.visibility = View.GONE
                    binding.registerTeamId.visibility = View.GONE
                    //                    if (registeredList?.size!! > 1) {
                    if (registeredList?.size!! >= 3) {
                        binding.btnOrganizeMatches.visibility = View.VISIBLE
                    } else {
                        binding.btnOrganizeMatches.visibility = View.GONE
                    }
                    setAdapter(registeredList)
                    //                    } else {
                    //                        binding.btnOrganizeMatches.visibility = View.GONE
                    //                        binding.teamsRV.visibility = View.GONE
                    //                    }
                }
            } else {
                Toast.makeText(
                    this@TournamentDetailActivity,
                    "Unable to register, Tryagain or create a team first!",
                    Toast.LENGTH_SHORT
                ).show()

                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getRegisteredTeamsInTournament()
    }

    private fun getRegisteredTeamsInTournament() {
        val user = dataViewModel.getLoggedInUser()
        dataViewModel.getRegisteredTeamsInTournament(selectedTournament?.tournamentId) { listTeam ->
            if (listTeam?.size != 0) {
                val isTeamIdMatched = listTeam?.any { it.teamId == user?.uid } ?: false
                val isListTeamSizeGreaterThanTwelve = listTeam?.size ?: 0 > 12

                if (isTeamIdMatched || isListTeamSizeGreaterThanTwelve) {
                    binding.registerTeamId.visibility = View.GONE
                } else {
                    binding.registerTeamId.visibility = View.VISIBLE
                }


                val intentFrom = Paper.book().read<String>("INTENTFROM")
//                if(intentFrom.equals(dashboardCaptain)){
////                    binding.btnOrganizeMatches.visibility=View.GONE
//                }
//                else {
                    if (listTeam?.size!! < 3 || listTeam?.size!! > 12) {
                        binding.btnOrganizeMatches.visibility = View.GONE
                    } else {
                        binding.btnOrganizeMatches.visibility = View.VISIBLE

                    }
//                }
                setAdapter(listTeam)

            } else {
//                binding.btnOrganizeMatches.visibility = View.GONE
                binding.teamsRV.visibility = View.GONE
                binding.registerTeamId.visibility = View.VISIBLE
            }
        }


    }

    fun setAdapter(teamsList: List<TeamModel>?) {
        binding.teamsRV.visibility = View.VISIBLE
        binding.teamsRV.layoutManager = LinearLayoutManager(this@TournamentDetailActivity)
        binding.teamsRV.adapter = RegisteredTeamsAdapter(
            this@TournamentDetailActivity,
            teamsList,
            selectedTournament?.tournamentId
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.with(applicationContext).clear(binding.tournamentImageId)
    }

    fun sendRegisterRequest(dialog: ProgressDialog) {
        dataViewModel.registerTeamInTournament(selectedTournament?.tournamentId){
            dialog.dismiss()
            if(it){
//                Toast.makeText(this@TournamentDetailActivity, "Your request for register in tournament has been sent", Toast.LENGTH_SHORT).show()
                Snackbar.make(binding.root, "Your request for register in tournament has been sent", Snackbar.LENGTH_SHORT).show()
            }
        }
    }


}