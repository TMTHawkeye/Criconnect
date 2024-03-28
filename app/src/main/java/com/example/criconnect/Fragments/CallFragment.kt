package com.example.criconnect.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.criconnect.CustomClasses.ValueFormatter
import com.example.criconnect.R
import com.example.criconnect.ViewModels.TeamViewModel
import com.example.criconnect.databinding.FragmentCallBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CallFragment : Fragment() {
    lateinit var binding : FragmentCallBinding
    val teamViewModel: TeamViewModel by sharedViewModel()

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding=FragmentCallBinding.inflate(layoutInflater,container,false)
         binding.chart1.setMaxVisibleValueCount(40)
//        setData(10)

//          binding.fab.setOnClickListener {
//            val graphForTournamentFragment = TournamentRecordGraphFragment()
//            val transaction = childFragmentManager.beginTransaction()
//            transaction.replace(R.id.frame_layout, graphForTournamentFragment)
//            transaction.addToBackStack(null) // Optional: Add to back stack
//            transaction.commit()
//        }
        return binding.root
    }

    fun setData(count: Int) {
        val yvalues: ArrayList<BarEntry> = ArrayList<BarEntry>()
        for (i in 0 until count) {
            val val1 = (Math.random() * count).toFloat() + 20
            val val2 = (Math.random() * count).toFloat() + 20
            val val3 = (Math.random() * count).toFloat() + 20
            yvalues.add(BarEntry(i.toFloat(), floatArrayOf(val1, val2, val3)))
        }
        val set1: BarDataSet
        set1 = BarDataSet(yvalues, "Statistics of Match")
        set1.setDrawIcons(false)
        set1.setStackLabels(arrayOf("Won", "Lose", "Draw"))
        set1.setColors(*ColorTemplate.JOYFUL_COLORS)
        val data = BarData(set1)
        data.setValueFormatter(ValueFormatter())
        binding.chart1.setData(data)
        binding.chart1.setFitBars(true)
        binding.chart1.invalidate()
    }

    override fun onResume() {
        super.onResume()

        fetchTeamData()

    }

    private fun fetchTeamData() {
        // Assuming getAllTeamsData() returns LiveData<List<Team>> or similar
        teamViewModel.getAllteamsData { teams ->
            // Convert teams data into a format suitable for the chart
            val barEntries = ArrayList<BarEntry>()
            val labels = ArrayList<String>() // List to hold team names
            teams.forEachIndexed { index, team ->
                // Assuming each team has won and loss statistics
                val won = team.wins.toFloat()
                val lose = team.loss.toFloat()
                barEntries.add(BarEntry(index.toFloat(), floatArrayOf(won, lose)))
                labels.add(team.teamName.toString()) // Adding team name to labels list
            }
            // Setting up BarDataSet and BarData
            val barDataSet = BarDataSet(barEntries, "Statistics of Match")
            barDataSet.setDrawIcons(false)
            barDataSet.setStackLabels(arrayOf("Won", "Lose"))
            // Set colors for wins and losses
            barDataSet.colors = mutableListOf(android.graphics.Color.GREEN, android.graphics.Color.RED)
            val data = BarData(barDataSet)
            data.setValueFormatter(ValueFormatter())

            // Setting up chart with data
            binding.chart1.data = data
            binding.chart1.setFitBars(true)
            binding.chart1.invalidate()

            // Setting labels on x-axis
            val xAxis = binding.chart1.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.setLabelCount(labels.size) // Set the number of labels to display
            xAxis.position = XAxis.XAxisPosition.BOTTOM // Position x-axis at the bottom
            xAxis.granularity = 1f // Ensure no skipping of labels
        }
    }

}
