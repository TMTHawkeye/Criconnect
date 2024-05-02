package com.example.criconnect.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.criconnect.Activities.TournamentDetailActivity
import com.example.criconnect.databinding.ItemTournamentInfoBinding

class TeamRequestsAdapter(
    val ctxt: Context,
    val selectedTournamentList: ArrayList<Pair<String, String>>
) : RecyclerView.Adapter<TeamRequestsAdapter.viewHolder>() {
    lateinit var binding :ItemTournamentInfoBinding
    inner class viewHolder(val binding: ItemTournamentInfoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding=ItemTournamentInfoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return selectedTournamentList?.size?:0
     }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.attribName.text=selectedTournamentList?.get(position)?.first
        holder.binding.attribName.text=selectedTournamentList?.get(position)?.second
     }
}