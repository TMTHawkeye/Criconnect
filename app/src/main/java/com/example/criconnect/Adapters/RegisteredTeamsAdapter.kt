package com.example.criconnect.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.Activities.TeamDetailActivity
import com.example.criconnect.Activities.TournamentDetailActivity
import com.example.criconnect.HelperClasses.base64ToDrawable
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.R
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
        loadImage(position,holder)

        holder.itemView.setOnClickListener {
            ctxt.startActivity(Intent(ctxt,TeamDetailActivity::class.java)
                .putExtra("selectedTeam",teamsList?.get(position)))
        }
     }

    fun loadImage(position: Int, holder: RegisteredTeamsAdapter.viewHolder){
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.circlelogo)
            .error(R.drawable.circlelogo)

        Glide.with(ctxt)
            .load(teamsList?.get(position)?.teamLogo)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .apply(options)
            .into(holder.binding.tournamentImage)

    }
}