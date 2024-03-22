package com.example.criconnect.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.criconnect.Activities.TournamentDataActivity
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        val slideModels = ArrayList<SlideModel>()
        slideModels.add(SlideModel(R.drawable.image5, "image0", ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.image4, "image1", ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.image3, "image2", ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.images, "image3", ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.imag1, "image4", ScaleTypes.FIT))
        binding.imageslider.setImageList(slideModels, ScaleTypes.FIT)



        binding.iconImageView.setOnClickListener {
            val intent = Intent(requireContext(), TournamentDataActivity::class.java)
            startActivity(intent)

//            teamViewModel.getTeamData {
//                binding.rv.setBackgroundDrawable(it?.teamLogo)
//            }
        }


        return binding.root
    }

}