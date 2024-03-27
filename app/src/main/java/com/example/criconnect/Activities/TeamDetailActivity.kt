package com.example.criconnect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.Adapters.MyViewHolder
import com.example.criconnect.Adapters.PlayerAdapter
import com.example.criconnect.Interfaces.PlayerListner
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.ActivityTeamDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TeamDetailActivity : AppCompatActivity(), PlayerListner {
    lateinit var binding: ActivityTeamDetailBinding
    var selectedTeam: TeamModel? = null
    var selectedTeamId: String? = null
    var selectedTournament: String? = null
    private var adapter: PlayerAdapter? = null

    val teamViewModel: TeamViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedTeamId = intent?.getStringExtra("selectedTeamId")
        selectedTournament = intent?.getStringExtra("selectedTournament")

        Log.d("TAGselecteditem", "onCreate: $selectedTeamId and $selectedTournament")

        teamViewModel.getSelectedTeamDetails(selectedTeamId) { team ->
            selectedTeam = team

            binding.teamNameId.text = selectedTeam?.teamName
            binding.captainNameTxt.text = selectedTeam?.captainName
            binding.ageId.text = selectedTeam?.city
            binding.phoneId.text = selectedTeam?.homeGround
            loadImage()

            setAdapter(selectedTeam?.playerData)
        }
    }

    fun loadImage() {
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.circlelogo)
            .error(R.drawable.circlelogo)

        Glide.with(this@TeamDetailActivity)
            .load(selectedTeam?.teamLogo)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .apply(options)
            .into(binding.profilePhotoId)

    }

    private fun setAdapter(playerList: List<PlayerData>?) {
        val gridLayoutManager = GridLayoutManager(this@TeamDetailActivity, 1)
        binding.teamsRV.setLayoutManager(gridLayoutManager)

        val user = teamViewModel.getLoggedInUser()
        if(user?.uid?.equals(selectedTeam?.teamId) == true){
            adapter = PlayerAdapter(this@TeamDetailActivity, playerList,this)
        }
        else{
            adapter = PlayerAdapter(this@TeamDetailActivity, playerList,null)
        }

//        binding.progressBar.visibility= View.GONE
        binding.teamsRV.setAdapter(adapter)
    }

    override fun onDeletePlayer(player: PlayerData) {
        teamViewModel.deletePlayerFromTeam(player?.playerId) { success ->
            if (success) {
                val updatedList = selectedTeam?.playerData?.filter { it.playerId != player.playerId }
                adapter?.updateList(updatedList)
                Toast.makeText(this@TeamDetailActivity, "Deleted Successfully!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this@TeamDetailActivity,
                    "Unable to delete, tryagain!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}