package com.example.criconnect.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.criconnect.CustomClasses.ValueFormatter
import com.example.criconnect.R
import com.example.criconnect.databinding.FragmentCallBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

class CallFragment : Fragment() {
    lateinit var binding : FragmentCallBinding

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding=FragmentCallBinding.inflate(layoutInflater,container,false)
         binding.chart1.setMaxVisibleValueCount(40)
        setData(10)

          binding.fab.setOnClickListener {
            val graphForTournamentFragment = TournamentRecordGraphFragment()
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, graphForTournamentFragment)
            transaction.addToBackStack(null) // Optional: Add to back stack
            transaction.commit()
        }
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
}
