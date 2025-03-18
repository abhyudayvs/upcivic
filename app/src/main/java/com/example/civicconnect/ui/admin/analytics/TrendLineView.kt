package com.example.civicconnect.ui.admin.analytics

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.civicconnect.R
import com.example.civicconnect.databinding.ViewTrendLineBinding
import com.example.civicconnect.util.TrendAnalyzer

class TrendLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewTrendLineBinding.inflate(LayoutInflater.from(context), this, true)
    private var isEnabled = true

    var onTrendLineToggled: ((Boolean) -> Unit)? = null

    init {
        binding.trendSwitch.setOnCheckedChangeListener { _, isChecked ->
            isEnabled = isChecked
            onTrendLineToggled?.invoke(isChecked)
            updateTrendVisibility()
        }
    }

    fun setTrend(trend: TrendAnalyzer.Trend) {
        with(binding) {
            // Set trend direction icon
            trendIcon.setImageResource(
                when (trend.direction) {
                    TrendAnalyzer.Direction.UP -> R.drawable.ic_trend_up
                    TrendAnalyzer.Direction.DOWN -> R.drawable.ic_trend_down
                    TrendAnalyzer.Direction.STABLE -> R.drawable.ic_trend_stable
                }
            )

            // Set trend color
            val colorRes = when (trend.direction) {
                TrendAnalyzer.Direction.UP -> R.color.positive_change
                TrendAnalyzer.Direction.DOWN -> R.color.negative_change
                TrendAnalyzer.Direction.STABLE -> R.color.neutral_change
            }
            trendIcon.setColorFilter(ContextCompat.getColor(context, colorRes))

            // Set trend percentage
            trendValue.text = context.getString(
                R.string.trend_percentage_format,
                trend.percentage
            )
            trendValue.setTextColor(ContextCompat.getColor(context, colorRes))
        }
    }

    private fun updateTrendVisibility() {
        binding.trendLineGroup.alpha = if (isEnabled) 1f else 0.5f
    }
} 