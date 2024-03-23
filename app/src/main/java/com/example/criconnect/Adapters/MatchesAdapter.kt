package com.example.criconnect.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.criconnect.Activities.OrganizeMatchesActivity
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
//        val teamName = matches?.get(position)?.first?.teamName
//        if (!teamName.equals("Finalist 1", ignoreCase = true) && !teamName.equals("Winner 1", ignoreCase = true)) {
//            val homeLogo = base64ToDrawable(matches?.get(position)?.first?.teamLogo)
//            holder.binding.homeTeamLogo.setImageDrawable(homeLogo)
//            Glide.with(ctxt).load(homeLogo).into(holder.binding.homeTeamLogo)
//
//            val awayLogo = base64ToDrawable(matches?.get(position)?.second?.teamLogo)
//            Glide.with(ctxt).load(awayLogo).into(holder.binding.awayTeamLogo)
//        }



        holder.binding.homeTeamName.text = matches?.get(position)?.first?.teamName
        holder.binding.awayTeamName.text = matches?.get(position)?.second?.teamName
    }
}