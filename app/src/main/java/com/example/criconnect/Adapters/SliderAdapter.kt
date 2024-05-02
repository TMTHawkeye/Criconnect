package com.example.criconnect.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.criconnect.Activities.TournamentDetailActivity
import com.example.criconnect.HelperClasses.base64ToDrawable
import com.example.criconnect.ModelClasses.tournamentDataClass
import com.example.criconnect.R
import com.example.criconnect.databinding.ImageSliderLayoutItemBinding
import com.smarteist.autoimageslider.SliderViewAdapter


class SliderAdapter(context: Context) :
    SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {
    private val context: Context
    private var mSliderItems: MutableList<tournamentDataClass> = ArrayList<tournamentDataClass>()

    lateinit var binding: ImageSliderLayoutItemBinding

    init {
        this.context = context
    }

    fun renewItems(sliderItems: MutableList<tournamentDataClass>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: tournamentDataClass) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        binding =
            ImageSliderLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderAdapterVH(binding)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem: tournamentDataClass = mSliderItems[position]
        viewHolder.binding.tournamentNameId.text = sliderItem.name

        loadImage(position, viewHolder)

        viewHolder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, TournamentDetailActivity::class.java)
                    .putExtra("selectedTournament", mSliderItems?.get(position))
                context.startActivity(intent)
            }
        })
    }

    fun loadImage(position: Int, holder: SliderAdapterVH) {
        val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.no_image_found)
            .error(R.drawable.no_image_found)

        Glide.with(context)
            .load(mSliderItems?.get(position)?.tournamentLogo)
            .transition(DrawableTransitionOptions.withCrossFade(factory))
            .apply(options)
            .into(holder.binding.ivAutoImageSlider)

    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    inner class SliderAdapterVH(val binding: ImageSliderLayoutItemBinding) :
        ViewHolder(binding.root)
}