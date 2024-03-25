package com.example.criconnect.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.criconnect.Activities.OrganizeMatchesActivity
import com.example.criconnect.HelperClasses.Constants.loadImage
import com.example.criconnect.HelperClasses.base64ToDrawable
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.databinding.ItemMatchBinding

class MatchesAdapter(
    val ctxt: OrganizeMatchesActivity,
    val matches: List<Pair<TeamModel, TeamModel>>?
) : RecyclerView.Adapter<MatchesAdapter.viewHolder>() {
    lateinit var binding : ItemMatchBinding

    inner class viewHolder(val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return matches?.size?:0
     }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.binding.homeTeamName.text = matches?.get(position)?.first?.teamName
        holder.binding.awayTeamName.text = matches?.get(position)?.second?.teamName

        loadImage(ctxt,holder.binding.homeTeamLogo,matches?.get(position)?.first?.teamLogo)
        loadImage(ctxt,holder.binding.awayTeamLogo,matches?.get(position)?.second?.teamLogo)
    }
}