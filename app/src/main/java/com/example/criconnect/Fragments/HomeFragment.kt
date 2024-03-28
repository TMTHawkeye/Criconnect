package com.example.criconnect.Fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.criconnect.Activities.TournamentDataActivity
import com.example.criconnect.Adapters.SliderAdapter
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.FragmentHomeBinding
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    val teamViewModel: TeamViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        binding.iconImageView.setOnClickListener {
            val intent = Intent(requireContext(), TournamentDataActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val adapter = SliderAdapter(requireContext())
        val slideModels = ArrayList<SlideModel>()

        teamViewModel.getTournament { tournamentList ->
            binding.progressBar.visibility = View.VISIBLE
            if (tournamentList?.size != 0) {
                tournamentList?.forEach { tournamentItem ->
                    slideModels.add(
                        SlideModel(
                            tournamentItem.tournamentName,
                            tournamentItem.tournamentName,
                            ScaleTypes.FIT
                        )
                    )
                    adapter.addItem(tournamentItem)
                }
                binding.noDataId.visibility = View.GONE
                binding.sliderView.visibility = View.VISIBLE

            } else {
                binding.sliderView.visibility = View.GONE
                binding.noDataId.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE

        }
        binding.sliderView.setSliderAdapter(adapter)
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_RTL)
        binding.sliderView.setIndicatorSelectedColor(requireContext().getColor(R.color.dark_red))
        binding.sliderView.setIndicatorUnselectedColor(Color.GRAY)
        binding.sliderView.setScrollTimeInSec(4)

        binding.sliderView.startAutoCycle()

        setProfileInformation()
    }

    private fun setProfileInformation() {
        teamViewModel.getTeamData { teamData, isAvailable ->
            if (isAvailable) {
                if (teamData != null) {
                    binding.noDataIdTV.visibility = View.GONE
                    binding.constrainProfileId.visibility = View.VISIBLE
                    binding.captionname.text = teamData.captainName + "(Captain)"
                    binding.wonId.text = teamData.wins.toString()
                    binding.lossId.text = teamData.loss.toString()
                    binding.matchesPlayedId.text = (teamData.wins + teamData.loss).toString()
                } else {
                    binding.noDataIdTV.visibility = View.VISIBLE
                    binding.constrainProfileId.visibility = View.GONE
                }
            } else {
                binding.noDataIdTV.visibility = View.VISIBLE
                binding.constrainProfileId.visibility = View.GONE
            }
        }
    }

}