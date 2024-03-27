package com.example.criconnect.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.criconnect.Activities.OrganizeMatchesActivity
import com.example.criconnect.HelperClasses.Constants.loadImage
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.databinding.ItemMatchBinding

class MatchesAdapter(
    val ctxt: OrganizeMatchesActivity,
    val matches: List<Pair<TeamModel, TeamModel>>?
) : RecyclerView.Adapter<MatchesAdapter.viewHolder>() {
    lateinit var binding : ItemMatchBinding
    private var expandedStates = BooleanArray(itemCount) { false }

    inner class viewHolder(val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return matches?.size?:0
     }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val match = matches?.get(position)

        holder.binding.apply {
            homeTeamName.text = match?.first?.teamName
            awayTeamName.text = match?.second?.teamName

            loadImage(ctxt, homeTeamLogo, match?.first?.teamLogo)
            loadImage(ctxt, awayTeamLogo, match?.second?.teamLogo)

            subItem.visibility = if (expandedStates[position]) View.VISIBLE else View.GONE
        }

        holder.binding.root.setOnClickListener {
            expandedStates[position] = !expandedStates[position]
            notifyItemChanged(position)
        }
    }
}