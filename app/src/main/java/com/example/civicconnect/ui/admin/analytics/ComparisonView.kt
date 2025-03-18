package com.example.civicconnect.ui.admin.analytics

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.example.civicconnect.R
import com.example.civicconnect.databinding.ViewComparisonBinding
import com.example.civicconnect.model.AnalyticsComparison
import java.text.SimpleDateFormat
import java.util.Locale

class ComparisonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewComparisonBinding.inflate(LayoutInflater.from(context), this, true)
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    fun setComparison(comparison: AnalyticsComparison, label: String) {
        with(binding) {
            titleText.text = label
            
            // Current period
            currentPeriodValue.text = String.format("%.0f", comparison.currentPeriod.value)
            currentPeriodRange.text = context.getString(
                R.string.date_range_format,
                dateFormat.format(comparison.currentPeriod.startDate),
                dateFormat.format(comparison.currentPeriod.endDate)
            )

            // Previous period
            previousPeriodValue.text = String.format("%.0f", comparison.previousPeriod.value)
            previousPeriodRange.text = context.getString(
                R.string.date_range_format,
                dateFormat.format(comparison.previousPeriod.startDate),
                dateFormat.format(comparison.previousPeriod.endDate)
            )

            // Change percentage
            val changeText = String.format("%+.1f%%", comparison.percentageChange)
            changePercentage.text = changeText
            changePercentage.setTextColor(
                ContextCompat.getColor(
                    context,
                    when {
                        comparison.percentageChange > 0 -> R.color.positive_change
                        comparison.percentageChange < 0 -> R.color.negative_change
                        else -> R.color.neutral_change
                    }
                )
            )
        }
    }
} 