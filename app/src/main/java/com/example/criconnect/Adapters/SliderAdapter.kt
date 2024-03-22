package com.example.criconnect.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.criconnect.HelperClasses.base64ToDrawable
import com.example.criconnect.ModelClasses.TournamentData
import com.example.criconnect.databinding.ImageSliderLayoutItemBinding
import com.smarteist.autoimageslider.SliderViewAdapter


class SliderAdapter(context: Context) :
    SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {
    private val context: Context
    private var mSliderItems: MutableList<TournamentData> = ArrayList<TournamentData>()

    lateinit var binding: ImageSliderLayoutItemBinding

    init {
        this.context = context
    }

    fun renewItems(sliderItems: MutableList<TournamentData>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: TournamentData) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        binding =
            ImageSliderLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderAdapterVH(binding)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem: TournamentData = mSliderItems[position]
        viewHolder.binding.tournamentNameId.text = sliderItem.tournamentName

        val logo= base64ToDrawable(sliderItem.tournamentLogo)
        Glide.with(viewHolder.itemView)
            .load(logo)
            .fitCenter()
            .into(viewHolder.binding.ivAutoImageSlider)

        viewHolder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(context, "This is item in position $position", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    inner class SliderAdapterVH(val binding: ImageSliderLayoutItemBinding) :
        ViewHolder(binding.root)
}