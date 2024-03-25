package com.example.criconnect.Adapters

 import android.content.Context
import android.content.Intent
 import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
 import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
 import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
 import com.bumptech.glide.request.RequestOptions
 import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.Activities.PlayerDetailsActivity
 import com.example.criconnect.Fragments.TeamManagementFragment
 import com.example.criconnect.Interfaces.PlayerListner
 import com.example.criconnect.ModelClasses.PlayerData
 import com.example.criconnect.R
import com.example.criconnect.databinding.PlayerrecyclerItemBinding


class PlayerAdapter(
    val ctxt: Context,
    val dataListt: List<PlayerData>?,
    val listner: PlayerListner?
) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    var context: Context? = ctxt
    var dataList: List<PlayerData>? = dataListt


    fun setSearchList(dataSearchList: List<PlayerData>) {
        this.dataList = dataSearchList
        notifyDataSetChanged()
    }

    lateinit var binding: PlayerrecyclerItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.playerrecycler_item, parent, false)

        binding =
            PlayerrecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
//        holder.binding.recImage.setImageResource(dataList!![position].dataImage)
//        val base64toDrawable = base64ToDrawable( dataList?.get(position)?.playerLogo)
        Glide.with(ctxt).load(dataList!!.get(position).playerLogo)
            .error(ctxt.getDrawable(R.drawable.circlelogo)).into(holder.binding.recImage)
        holder.binding.recTitle.setText(dataList!![position].playerName)
        holder.binding.recLang.setText(dataList!![position].speciality)

        loadImage(position, holder)


        if(listner==null) {
            holder.binding.deleteItem.visibility=View.GONE
        }
            holder.binding.deleteItem.setOnClickListener {
                deletePlayer(position)
            }


        holder.binding.recCard.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, PlayerDetailsActivity::class.java)
            intent.putExtra("model", dataList?.get(position))
            context!!.startActivity(intent)
        })
    }

    private fun deletePlayer(position: Int) {
        dataList?.get(position)?.let { playerToDelete ->
            listner?.onDeletePlayer(playerToDelete)
        }
    }

    fun updateList(newList: List<PlayerData>?) {
        dataList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList?.size?:0
    }

    class PlayerViewHolder(val binding: PlayerrecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)


    fun loadImage(position: Int, holder: PlayerViewHolder){
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.circlelogo)
            .error(R.drawable.circlelogo)

        Glide.with(ctxt)
            .load(dataList?.get(position)?.playerLogo)
            .transition(withCrossFade(factory))
            .apply(options)
            .into(holder.binding.recImage)

    }
}