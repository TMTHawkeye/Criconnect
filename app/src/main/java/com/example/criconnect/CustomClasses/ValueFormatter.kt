package com.example.criconnect.CustomClasses

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class ValueFormatter : IValueFormatter {

    var mformat: DecimalFormat? = null

   init {
        mformat = DecimalFormat("######.0")
    }

    override fun getFormattedValue(
        value: Float,
        entry: Entry?,
        dataSetIndex: Int,
        viewPortHandler: ViewPortHandler?
    ): String? {
        return mformat!!.format(value.toDouble()) + "$"
    }
}