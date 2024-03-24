package com.example.criconnect.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.Activities.DetailActivity
import com.example.criconnect.Activities.TournamentDetailActivity
import com.example.criconnect.HelperClasses.base64ToDrawable
import com.example.criconnect.ModelClasses.DataClass
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.R
import com.example.criconnect.databinding.ItemTournamentBinding
import com.example.criconnect.databinding.RecyclerItemBinding
import java.io.Serializable

class MyAdapter(val ctxt:Context ,val dataListt: List<TournamentData>?) :
    RecyclerView.Adapter<MyViewHolder>(),Serializable {

    var dataList: List<TournamentData>? = dataListt

    fun setSearchList(dataSearchList: List<TournamentData>) {
        this.dataList = dataSearchList
        notifyDataSetChanged()
    }

   lateinit var binding:ItemTournamentBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding=ItemTournamentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val base64toDrawable = base64ToDrawable( dataList?.get(position)?.tournamentLogo)
        holder.binding.recTitle.setText(dataList!![position].tournamentName)
        holder.binding.recLocation.setText(dataList!![position].tournamentLocation)
        loadImage(position,holder)

        Log.d("TAGTournamentid", "onCreate: ${dataList?.get(position)?.tournamentId}")

        holder.binding.recCard.setOnClickListener {
            val intent = Intent(ctxt, TournamentDetailActivity::class.java)
                .putExtra("selectedTournament",dataList?.get(position))
            ctxt.startActivity(intent)
        }
    }

    fun loadImage(position: Int, holder: MyViewHolder){
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.circlelogo)
            .error(R.drawable.circlelogo)

        Glide.with(ctxt)
            .load(dataList?.get(position)?.tournamentLogo)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .apply(options)
            .into(holder.binding.tournamentImage)

    }

    override fun getItemCount(): Int {
        return dataList?.size?:0
    }
}

class MyViewHolder(val binding: ItemTournamentBinding) : RecyclerView.ViewHolder(binding.root)