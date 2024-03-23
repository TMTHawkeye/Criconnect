package com.example.criconnect.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.criconnect.Activities.TournamentDetailActivity
import com.example.criconnect.HelperClasses.base64ToDrawable
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.databinding.ItemTournamentBinding

class RegisteredTeamsAdapter(
    val ctxt: TournamentDetailActivity,
    val teamsList: List<TeamModel>?
) : RecyclerView.Adapter<RegisteredTeamsAdapter.viewHolder>() {

    lateinit var binding : ItemTournamentBinding

    inner class viewHolder(val binding: ItemTournamentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding=ItemTournamentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return teamsList?.size?:0
     }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
//        val base64toDrawable = base64ToDrawable( teamsList?.get(position)?.teamLogo)
//        holder.binding.tournamentImage.setImageDrawable(base64toDrawable)
        holder.binding.recTitle.setText(teamsList!![position].teamName)
        holder.binding.recLocation.setText(teamsList[position].homeGround)
     }
}