package com.example.criconnect.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criconnect.Adapters.VersionsAdapter
import com.example.criconnect.ModelClasses.versions
import com.example.criconnect.databinding.FragmentFAQBinding

class FAQFragment : Fragment() {
    lateinit var binding : FragmentFAQBinding
     var versionsList: ArrayList<versions>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFAQBinding.inflate(layoutInflater,container,false)

         versionsList = ArrayList()
        initData{
            setRecyclerView()
        }
        return binding.root
    }

    private fun setRecyclerView() {
        val versionsAdapter = VersionsAdapter(versionsList)
        binding.recyclerView.adapter = versionsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initData(callback:(Boolean)->Unit) {
        versionsList?.add(
            versions(
                "How can I create a profile for my cricket team?",
                "Click on the + button and select create team from there just enter the required details and team will be created"
            )
        )
        versionsList?.add(
            versions(
                "Can I upload a team logo for my profile?",
                "Yes! Captain can Upload the logo for team"
            )
        )
        versionsList?.add(
            versions(
                "Is there a limit to the number of players I can add to my profile?",
                "No captain have no limit of adding players profile"
            )
        )
        versionsList?.add(
            versions(
                "How are tournament matches scheduled?",
                "On the basis of no of team our system will automatically generate scedule for teams"
            )
        )
        versionsList?.add(
            versions(
                "What are the rules and regulations for tournament participation?",
                "The rules and regulations will be defined by the organizer of that tournament"
            )
        )
        versionsList?.add(
            versions(
                "How can I become an organizer for a cricket tournament",
                "Captain cannot be organizer for that user have to registered as organizer"
            )
        )
        versionsList?.add(
            versions(
                "Are there any fees associated with becoming an organizer?",
                "No fee is included"
            )
        )
        versionsList?.add(
            versions(
                "How can I search for specific players",
                "By there name palyers can be searched"
            )
        )
        versionsList?.add(
            versions(
                "What are the rules and regulations for tournament participation?",
                "Description"
            )
        )
        versionsList?.add(
            versions(
                "What are the rules and regulations for tournament participation?",
                "Description"
            )
        )
        versionsList?.add(
            versions(
                "Is there a limit to the number of players I can add to my profile?",
                "Description"
            )
        )
        versionsList?.add(versions("How can I create a profile for my cricket team?", "Description"))
        versionsList?.add(
            versions(
                "How can I become an organizer for a cricket tournament",
                "Description"
            )
        )
        callback.invoke(true)

    }

 }