package com.example.criconnect.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.criconnect.Activities.DetailActivity
import com.example.criconnect.ModelClasses.DataClass
import com.example.criconnect.ModelClasses.PlayerData
import com.example.criconnect.R
import com.example.criconnect.databinding.PlayerrecyclerItemBinding

class PlayerAdapter(val ctxt:Context,val dataListt : List<PlayerData>?) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    var context: Context? = ctxt
    var dataList: List<PlayerData>? = dataListt

    fun setSearchList(dataSearchList: List<PlayerData>) {
        this.dataList = dataSearchList
        notifyDataSetChanged()
    }

    lateinit var binding : PlayerrecyclerItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.playerrecycler_item, parent, false)

        binding=PlayerrecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
//        holder.binding.recImage.setImageResource(dataList!![position].dataImage)
        holder.binding.recTitle.setText(dataList!![position].playerName)
        holder.binding.recLang.setText(dataList!![position].speciality)



        holder.binding.recCard.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("model",dataList?.get(position))
//            intent.putExtra("Image", dataList!![holder.getAdapterPosition()].dataImage)
//            intent.putExtra("Title", dataList!![holder.getAdapterPosition()].dataTitle)
//            intent.putExtra("Desc", dataList!![holder.getAdapterPosition()].dataDesc)
            context!!.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return dataList?.size?:0
    }

    class PlayerViewHolder(val binding: PlayerrecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)

}