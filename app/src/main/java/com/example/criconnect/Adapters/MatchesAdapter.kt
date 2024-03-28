package com.example.criconnect.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.criconnect.Activities.OrganizeMatchesActivity
import com.example.criconnect.HelperClasses.Constants.loadImage
import com.example.criconnect.ModelClasses.MatchModel
import com.example.criconnect.R
import com.example.criconnect.databinding.ItemMatchBinding

class MatchesAdapter(
    private val ctxt: OrganizeMatchesActivity,
    private val matches: List<MatchModel>?,
    private val onWinnerSelected: (String?,String?, String?) -> Unit
) : RecyclerView.Adapter<MatchesAdapter.ViewHolder>() {

    private var expandedStates = BooleanArray(itemCount) { false }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = matches?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = matches?.get(position)
        holder.bind(match, position)
    }

    inner class ViewHolder(private val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: MatchModel?, position: Int) {
            match?.let { match ->
                binding.apply {
                    ctxt.dataViewModel.getSelectedTeamDetails(match.teamAId) { teamA ->
                        ctxt.dataViewModel.getSelectedTeamDetails(match.teamBId) { teamB ->
                            with(binding) {
                                homeTeamName.text = teamA?.teamName
                                loadImage(ctxt, homeTeamLogo, teamA?.teamLogo)

                                awayTeamName.text = teamB?.teamName
                                loadImage(ctxt, awayTeamLogo, teamB?.teamLogo)


                                radioGroup.setOnCheckedChangeListener(null) // Remove previous listener
                                if (match.winnerId == teamA?.teamId) {
                                    radioGroup.check(R.id.radio_team_a)
                                } else if (match.winnerId == teamB?.teamId) {
                                    radioGroup.check(R.id.radio_team_b)
                                } else {
                                    radioGroup.clearCheck()
                                }

                                radioGroup.setOnCheckedChangeListener { _, checkedId ->
                                    val winnerId = if (checkedId == R.id.radio_team_a) {
                                        teamA?.teamId
                                    } else {
                                        teamB?.teamId
                                    }
                                    val loserId = if (checkedId == R.id.radio_team_a) {
                                        teamB?.teamId
                                    } else {
                                        teamA?.teamId
                                    }
                                 }

                                root.setOnClickListener {
                                    expandedStates[position] = !expandedStates[position]
                                    notifyItemChanged(position)
                                }
                                subItem.visibility = if (expandedStates[position]) View.VISIBLE else View.GONE

                                okId.setOnClickListener {
                                    val checkedRadioButtonId = radioGroup.checkedRadioButtonId
                                    val winnerId = if (checkedRadioButtonId == R.id.radio_team_a) {
                                        teamA?.teamId
                                    } else if (checkedRadioButtonId == R.id.radio_team_b) {
                                        teamB?.teamId
                                    } else {
                                        null
                                    }
                                    match.winnerId=winnerId
                                    val loserId = if (checkedRadioButtonId == R.id.radio_team_a) {
                                        teamB?.teamId
                                    } else if (checkedRadioButtonId == R.id.radio_team_b) {
                                        teamA?.teamId
                                    } else {
                                        null
                                    }
                                    onWinnerSelected(match?.matchId,winnerId, loserId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
