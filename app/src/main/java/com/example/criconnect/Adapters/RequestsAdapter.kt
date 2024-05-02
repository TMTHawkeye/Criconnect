package com.example.criconnect.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.Activities.TeamDetailActivity
import com.example.criconnect.Interfaces.RequestAcceptListner
import com.example.criconnect.ModelClasses.TeamModel
import com.example.criconnect.R
import com.example.criconnect.databinding.RecyclerItemBinding

class RequestsAdapter(val ctxt: Context, val teamsList: ArrayList<TeamModel>?) :
    RecyclerView.Adapter<RequestsAdapter.viewHolder>() {
    lateinit var binding: RecyclerItemBinding
    lateinit var listnerrequest : RequestAcceptListner

    inner class viewHolder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return teamsList?.size ?: 0
    }

    fun setListner(listner : RequestAcceptListner){
        this.listnerrequest=listner
    }



    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.recTitle.text=teamsList?.get(position)?.teamName
        loadImage(position,holder)

        holder.binding.recLocation.setOnClickListener {
            teamsList?.get(position)?.let {
                listnerrequest.requestAcceptedTeam(it)
                teamsList.removeAt(position)
                notifyDataSetChanged()
            }
        }




    }

    fun loadImage(position: Int, holder: viewHolder){
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.no_image_found)
            .error(R.drawable.no_image_found)

        Glide.with(ctxt)
            .load(teamsList?.get(position)?.teamLogo)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .apply(options)
            .into(holder.binding.recImage)

    }
}