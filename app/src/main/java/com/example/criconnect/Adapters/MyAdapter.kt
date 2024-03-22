package com.example.criconnect.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.criconnect.Activities.DetailActivity
import com.example.criconnect.ModelClasses.DataClass
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.R
import com.example.criconnect.databinding.RecyclerItemBinding

class MyAdapter(val ctxt:Context ,val dataListt: List<TournamentData>?) :
    RecyclerView.Adapter<MyViewHolder>() {

    val context: Context? = ctxt
    var dataList: List<TournamentData>? = dataListt

    fun setSearchList(dataSearchList: List<TournamentData>) {
        this.dataList = dataSearchList
        notifyDataSetChanged()
    }

   lateinit var binding:RecyclerItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding=RecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.recImage.setImageDrawable(dataList!![position].tournamentLogo)
        holder.binding.recTitle.setText(dataList!![position].tournamentName)
        holder.binding.recLocation.setText(dataList!![position].tournamentLocation)
        holder.binding.recCard.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
//            intent.putExtra("Image", dataList!![holder.adapterPosition].dataImage)
//            intent.putExtra("Title", dataList!![holder.adapterPosition].dataTitle)
//            intent.putExtra("Desc", dataList!![holder.adapterPosition].dataDesc)
            context!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size?:0
    }
}

class MyViewHolder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)