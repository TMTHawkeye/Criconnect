package com.example.criconnect.Fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.criconnect.Activities.TournamentDataActivity
import com.example.criconnect.Adapters.SliderAdapter
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.ViewModels.TournamentViewModel
import com.example.criconnect.databinding.FragmentHomeBinding
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    val teamViewModel: TeamViewModel by sharedViewModel()

    val tournamentViewModel : TournamentViewModel by viewModel()

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

        tournamentViewModel.getTournaments {registeredTournamentList->
            binding.progressBar.visibility = View.VISIBLE
            if (registeredTournamentList?.size != 0) {
                registeredTournamentList?.forEach { tournamentItem ->
                    slideModels.add(
                        SlideModel(
                            tournamentItem.name,
                            tournamentItem.name,
                            ScaleTypes.FIT
                        )
                    )
                    adapter.addItem(tournamentItem)
                }
                binding.noDataIdTVTournament.visibility = View.GONE
                binding.sliderView.visibility = View.VISIBLE

            } else {
                binding.sliderView.visibility = View.GONE
                binding.noDataIdTVTournament.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
        }

/*
        tournamentViewModel.tournamentListLiveData.observe(viewLifecycleOwner, Observer{registeredTournamentList->
            binding.progressBar.visibility = View.VISIBLE
            if (registeredTournamentList?.size != 0) {
                registeredTournamentList?.forEach { tournamentItem ->
                    slideModels.add(
                        SlideModel(
                            tournamentItem.name,
                            tournamentItem.name,
                            ScaleTypes.FIT
                        )
                    )
                    adapter.addItem(tournamentItem)
                }
                binding.noDataIdTVTournament.visibility = View.GONE
                binding.sliderView.visibility = View.VISIBLE

            } else {
                binding.sliderView.visibility = View.GONE
                binding.noDataIdTVTournament.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE

        })
*/


        /*teamViewModel.getTournament { tournamentList ->
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
                 binding.noDataIdTVTournament.visibility = View.GONE
                binding.sliderView.visibility = View.VISIBLE

            } else {
                binding.sliderView.visibility = View.GONE
                 binding.noDataIdTVTournament.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE

        }*/
        binding.sliderView.setSliderAdapter(adapter)
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_RTL)
        binding.sliderView.setIndicatorSelectedColor(requireContext().getColor(R.color.colorAccent))
        binding.sliderView.setIndicatorUnselectedColor(Color.GRAY)
        binding.sliderView.setScrollTimeInSec(4)

        binding.sliderView.startAutoCycle()

        setProfileInformation()

        val config = ShowcaseConfig()
        val sequence = MaterialShowcaseSequence(requireActivity(), getString(R.string.tournament))

//        if(binding.btnOrganizeMatches.isVisible) {
        sequence.addSequenceItem(
            MaterialShowcaseView.Builder(requireActivity())
                .setTarget(binding.cardSlider)
                .setContentText(getString(R.string.tournamentsDesc))
                .setDismissText(getString(R.string.got_it))
                .setMaskColour(requireContext().getColor(R.color.colorAccent))
                .build()
        )
//        }
//        if(binding.registerTeamId.isVisible) {
        sequence.addSequenceItem(
            MaterialShowcaseView.Builder(requireActivity())
                .setTarget(binding.constrainProfileId)
                .setContentText(getString(R.string.profileDesc))
                .setDismissText(getString(R.string.got_it))
                .setMaskColour(requireContext().getColor(R.color.colorAccent))
                .build()
        )
//        }

        sequence.start()
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