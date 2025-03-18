package com.example.civicconnect.ui.admin.analytics

import android.content.Context
import android.widget.TextView
import com.example.civicconnect.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChartMarkerView(
    context: Context,
    layoutResource: Int
) : MarkerView(context, layoutResource) {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val valueText: TextView = findViewById(R.id.valueText)
    private val dateText: TextView = findViewById(R.id.dateText)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            valueText.text = String.format("%.0f", e.y)
            dateText.text = dateFormat.format(Date(e.x.toLong()))
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
} 