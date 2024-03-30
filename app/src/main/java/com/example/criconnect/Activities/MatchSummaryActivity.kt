package com.example.criconnect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.criconnect.HelperClasses.Constants.loadImage
import com.example.criconnect.ModelClasses.MatchModel
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityMatchSummaryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MatchSummaryActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivityMatchSummaryBinding
    var match: MatchModel? = null
    val teamViewModel: TeamViewModel by viewModel()
    var team: TeamModel? = null
    var selectedTournamentId: String? = null
    var isOwner: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        match = intent?.getSerializableExtra("selectedMatch") as MatchModel?
        selectedTournamentId = intent?.getStringExtra("selectedTournamentId")
        isOwner = intent.getBooleanExtra("isOwner", false)
        Log.d("TAG_winnerId", "onCreate: ${isOwner}")

        match?.let {
            if (it.completeStatus) {
                // Disable the fields when complete status is true
                binding.wonByValueId.isEnabled = false
                binding.oversFormatvalueId.isEnabled = false
                binding.POMSpinnerId.isEnabled = false

                // Set values when complete status is true
                binding.wonByValueId.setText(it.winnerTeamWonby.toString())
                binding.oversFormatvalueId.setText(it.oversFormat.toString())

                // Set spinner value from playerOfTheMatch if available
                val playerOfTheMatch = it.playerOfMatch
                if (!playerOfTheMatch.isNullOrEmpty()) {
                    val playerPosition =
                        team?.playerData?.indexOfFirst { player -> player.playerName == playerOfTheMatch }
                    if (playerPosition != null && playerPosition != -1) {
                        binding.POMSpinnerId.setSelection(playerPosition)
                    }
                }
                binding.saveData.visibility = View.GONE
            } else {
                if (isOwner) {
                    binding.POMSpinnerId.isEnabled = true
                    binding.wonByValueId.isEnabled = true
                    binding.oversFormatvalueId.isEnabled = true
                    binding.saveData.visibility = View.VISIBLE
                } else {
                    binding.POMSpinnerId.isEnabled = false
                    binding.wonByValueId.isEnabled = false
                    binding.oversFormatvalueId.isEnabled = false
                    binding.saveData.visibility = View.GONE
                }

            }
        }

        teamViewModel.getSelectedTeamDetails(match?.winnerId) {
            if (it != null) {
                team = it
                team?.let {
                    loadImage(this@MatchSummaryActivity, binding.profilePhotoId, it.teamLogo)
                    binding.winnerId.text = it.teamName
                    setPOMSpinner(it)
                }
            } else {
                Toast.makeText(
                    this@MatchSummaryActivity,
                    "Owner has not updated the match yet!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        binding.saveData.setOnClickListener {
            if (validateFields()) {
                selectedTournamentId?.let {
                    match?.oversFormat = binding.oversFormatvalueId.text.toString()
                    match?.winnerTeamWonby = binding.wonByValueId.text.toString()
                    match?.completeStatus = true
                    teamViewModel.updateMatch(selectedTournamentId, match?.matchId, match) {
                        if (it) {
                            updateTeamStats()
                            Toast.makeText(
                                this@MatchSummaryActivity,
                                "Match Summary Updated!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()

                        } else {
                            Toast.makeText(
                                this@MatchSummaryActivity,
                                "Unknown error occured!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }


        binding.backbtnId.setOnClickListener {
            finish()
        }
    }

    private fun setPOMSpinner(teamModel: TeamModel) {
        binding.POMSpinnerId.setSelection(0)
        val playerNames = teamModel.playerData?.map { it.playerName } ?: listOf()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, playerNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.POMSpinnerId.adapter = adapter
        binding.POMSpinnerId.onItemSelectedListener = this
    }

    private fun validateFields(): Boolean {
        val fields = listOf(
            binding.wonByValueId,
            binding.oversFormatvalueId
        )

        for (field in fields) {
            if (field.text.isNullOrBlank()) {
                field.error = "This field is required"
                field.requestFocus()
                return false
            }
        }
        return true
    }

    fun updateTeamStats() {
        teamViewModel.updateTeamStats(match?.winnerId, match?.looserId) { success ->
            if (success) {
                Toast.makeText(
                    this@MatchSummaryActivity,
                    "Team stats updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MatchSummaryActivity,
                    "Failed to update team stats",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position).toString()
        match?.playerOfMatch = selectedItem

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.with(applicationContext).clear(binding.profilePhotoId)
    }
}