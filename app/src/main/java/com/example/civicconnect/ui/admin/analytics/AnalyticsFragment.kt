package com.example.civicconnect.ui.admin.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.civicconnect.R
import com.example.civicconnect.databinding.FragmentAnalyticsBinding
import com.example.civicconnect.ui.base.BaseFragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

@AndroidEntryPoint
class AnalyticsFragment : BaseFragment<FragmentAnalyticsBinding>() {

    private val viewModel: AnalyticsViewModel by viewModels()
    private lateinit var chartInteractionHandler: ChartInteractionHandler

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAnalyticsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()
        observeViewModel()
        setupToolbar()
    }

    private fun setupCharts() {
        chartInteractionHandler = ChartInteractionHandler(
            requireContext(),
            onDataPointSelected = { date, dataPoint ->
                showDataPointDetail(date, dataPoint)
            }
        )

        with(binding) {
            // Setup User Activity Chart
            userActivityChart.apply {
                ChartTheme.applyLineChartTheme(this, requireContext())
                marker = ChartMarkerView(requireContext(), R.layout.view_chart_marker)
                chartInteractionHandler.setupLineChartInteraction(this)
            }

            // Setup Issue Categories Chart
            issueCategoriesChart.apply {
                ChartTheme.applyPieChartTheme(this, requireContext())
            }

            // Setup Poll Engagement Chart
            pollEngagementChart.apply {
                ChartTheme.applyBarChartTheme(this, requireContext())
                marker = ChartMarkerView(requireContext(), R.layout.view_chart_marker)
                chartInteractionHandler.setupBarChartInteraction(this)
            }

            userTrendLine.onTrendLineToggled = { visible ->
                viewModel.toggleTrendLine(visible)
            }

            chartControls.apply {
                onZoomIn = { 
                    chartInteractionHandler.zoomIn(userActivityChart)
                }
                onZoomOut = {
                    chartInteractionHandler.zoomOut(userActivityChart)
                }
                onReset = {
                    chartInteractionHandler.resetZoom(userActivityChart)
                }
                onHighlightToggled = { highlight ->
                    chartInteractionHandler.setHighlightPeaks(userActivityChart, highlight)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userActivityData.observe(viewLifecycleOwner) { data ->
            binding.userActivityChart.data = data
            binding.userActivityChart.invalidate()
        }

        viewModel.issueCategoriesData.observe(viewLifecycleOwner) { data ->
            binding.issueCategoriesChart.data = data
            binding.issueCategoriesChart.invalidate()
        }

        viewModel.pollEngagementData.observe(viewLifecycleOwner) { data ->
            binding.pollEngagementChart.data = data
            binding.pollEngagementChart.invalidate()
        }

        viewModel.analyticsStats.observe(viewLifecycleOwner) { stats ->
            with(binding) {
                totalUsersValue.text = stats.totalUsers.toString()
                activeUsersValue.text = stats.activeUsers.toString()
                totalIssuesValue.text = stats.totalIssues.toString()
                totalPollsValue.text = stats.totalPolls.toString()
                engagementRateValue.text = "${stats.engagementRate}%"
                resolutionRateValue.text = "${stats.resolutionRate}%"
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
                viewModel.clearError()
            }
        }

        viewModel.userComparison.observe(viewLifecycleOwner) { comparison ->
            binding.userComparisonView.setComparison(comparison, "User Activity")
        }

        viewModel.issueComparison.observe(viewLifecycleOwner) { comparison ->
            binding.issueComparisonView.setComparison(comparison, "New Issues")
        }

        viewModel.pollComparison.observe(viewLifecycleOwner) { comparison ->
            binding.pollComparisonView.setComparison(comparison, "Poll Engagement")
        }

        viewModel.userTrend.observe(viewLifecycleOwner) { trend ->
            binding.userTrendLine.setTrend(trend)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_analytics)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_date_range -> {
                        showDateRangeDialog()
                        true
                    }
                    R.id.action_export -> {
                        exportData()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun showDateRangeDialog() {
        DateRangeDialog().apply {
            onDateRangeSelected = { startDate, endDate ->
                viewModel.updateDateRangeWithComparison(startDate, endDate)
            }
        }.show(childFragmentManager, "dateRange")
    }

    private fun exportData() {
        DateRangeDialog().apply {
            onDateRangeSelected = { startDate, endDate ->
                viewModel.exportData(startDate, endDate)
            }
        }.show(childFragmentManager, "export")
    }

    private fun showDataPointDetail(
        date: Date,
        dataPoint: ChartInteractionHandler.AnalyticsDataPoint
    ) {
        DataPointDetailDialog.newInstance(
            date = date,
            value = dataPoint.value,
            label = dataPoint.label
        ).show(childFragmentManager, "dataPointDetail")
    }

    private class DateAxisFormatter : ValueFormatter() {
        private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

        override fun getFormattedValue(value: Float): String {
            return dateFormat.format(value.toLong())
        }
    }
} 