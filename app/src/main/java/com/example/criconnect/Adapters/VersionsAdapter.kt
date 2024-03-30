package com.example.criconnect.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.criconnect.ModelClasses.versions
import com.example.criconnect.R
import com.example.criconnect.databinding.ItemFaqBinding

class VersionsAdapter(val versionsList: ArrayList<versions>?)  : RecyclerView.Adapter<VersionsAdapter.VersionVH>() {
    var isExpanded = false // Add this flag


    lateinit var binding : ItemFaqBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VersionVH {
        binding=ItemFaqBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VersionVH(binding)
    }

    override fun onBindViewHolder(holder: VersionVH, position: Int) {
        val versions = versionsList?.get(position)

        holder.binding.linearLayout.setOnClickListener {
            isExpanded = !isExpanded
            holder.binding.expandableLayout.visibility =
                if (isExpanded) View.VISIBLE else View.GONE
        }

        holder.binding.codeName.setText(versions?.codeName)
        holder.binding.description.setText(versions?.description)
        binding.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return versionsList?.size?:0
    }

    class VersionVH(val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root)
}
