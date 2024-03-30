package com.example.criconnect.Adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.criconnect.Activities.MatchSummaryActivity
import com.example.criconnect.Activities.OrganizeMatchesActivity
import com.example.criconnect.HelperClasses.Constants.loadImage
import com.example.criconnect.ModelClasses.MatchModel
import com.example.criconnect.R
import com.example.criconnect.databinding.ItemMatchBinding

class MatchesAdapter(
    private val ctxt: OrganizeMatchesActivity,
    private val matches: List<MatchModel>?,
    private val selectedTournamentId: String?,
    private val selectedTournamentOwnerName: String?,
    private val isOwner: Boolean,
) : RecyclerView.Adapter<MatchesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = matches?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = matches?.get(position)

        match?.let { match ->
            ctxt.dataViewModel.getSelectedTeamDetails(match.teamAId) { teamA ->
                ctxt.dataViewModel.getSelectedTeamDetails(match.teamBId) { teamB ->
                    holder.binding.homeTeamName.text = teamA?.teamName
//                    holder.binding.homeTeamLogo.setImageURI(Uri.parse(teamA?.teamLogo))
//                    Glide.with(holder.itemView.context).load(teamA?.teamLogo)
//                        .into(holder.binding.homeTeamLogo)
//                    Glide.with(holder.itemView.context).load(teamB?.teamLogo)
//                        .into(holder.binding.awayTeamLogo)
//                                loadImage(ctxt, homeTeamLogo, teamA?.teamLogo)
                    holder.binding.awayTeamName.text = teamB?.teamName
//                    holder.binding.awayTeamLogo.setImageURI(Uri.parse(teamB?.teamLogo))
//                                loadImage(ctxt, awayTeamLogo, teamB?.teamLogo)

                    if(isOwner){
                        holder.binding.selectWinnerConstrainId.visibility=View.VISIBLE
                        holder.binding.matchSummaryId.visibility=View.GONE
                        if (match.completeStatus) {
                            holder.binding.selectWinnerConstrainId.visibility = View.GONE
                            holder.binding.matchSummaryId.visibility = View.VISIBLE
                        } else {
                            holder.binding.selectWinnerConstrainId.visibility = View.VISIBLE
                            holder.binding.matchSummaryId.visibility = View.GONE
                        }
                    }
                    else{
                        holder.binding.selectWinnerConstrainId.visibility=View.GONE
                        holder.binding.matchSummaryId.visibility=View.VISIBLE
                    }


                    var winnerTeam: String? = null
                    var looserTeam: String? = null
                    holder.binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                        winnerTeam = if (checkedId == R.id.radio_team_a) {
                            teamA?.teamId
                        } else {
                            teamB?.teamId
                        }
                        looserTeam = if (checkedId == R.id.radio_team_a) {
                            teamB?.teamId
                        } else {
                            teamA?.teamId
                        }
                    }

                    holder.binding.okId.setOnClickListener { v ->
                        if (winnerTeam != null) {
                            match.winnerId = winnerTeam
                            match.looserId = looserTeam
                            holder.binding.selectWinnerConstrainId.setVisibility(View.GONE)
                            holder.binding.matchSummaryId.setVisibility(View.VISIBLE)
                        } else {
                            Toast.makeText(
                                ctxt,
                                "Please select the winner!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    holder.binding.matchSummaryId.setOnClickListener {
                        ctxt.startActivity(
                            Intent(ctxt, MatchSummaryActivity::class.java)
                                .putExtra("selectedMatch", match)
                                .putExtra(
                                    "selectedTournamentId",
                                    selectedTournamentId
                                )
                                .putExtra(
                                    "selectedTournamentOwnerName",
                                    selectedTournamentOwnerName
                                )
                                .putExtra("isOwner",isOwner)
                        )
                    }

                }
            }

        }



    }

    inner class ViewHolder(val binding: ItemMatchBinding) :
        RecyclerView.ViewHolder(binding.root)


    /*  override fun onViewRecycled(holder: ViewHolder) {
          Glide.with(ctxt).clear(holder.binding.homeTeamLogo)
          Glide.with(ctxt).clear(holder.binding.awayTeamLogo)
          super.onViewRecycled(holder)
      }*/


}
