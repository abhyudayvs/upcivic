package com.example.civicconnect.ui.admin.analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicconnect.data.repository.AnalyticsRepository
import com.example.civicconnect.model.AnalyticsStats
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.civicconnect.util.TrendAnalyzer
import com.example.civicconnect.data.local.entity.AnalyticsDataEntity
import com.example.civicconnect.model.AnalyticsComparison
import android.app.Application
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.R

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _userActivityData = MutableLiveData<LineData>()
    val userActivityData: LiveData<LineData> = _userActivityData

    private val _issueCategoriesData = MutableLiveData<PieData>()
    val issueCategoriesData: LiveData<PieData> = _issueCategoriesData

    private val _pollEngagementData = MutableLiveData<BarData>()
    val pollEngagementData: LiveData<BarData> = _pollEngagementData

    private val _analyticsStats = MutableLiveData<AnalyticsStats>()
    val analyticsStats: LiveData<AnalyticsStats> = _analyticsStats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _userTrend = MutableLiveData<TrendAnalyzer.Trend>()
    val userTrend: LiveData<TrendAnalyzer.Trend> = _userTrend

    private val _issueTrend = MutableLiveData<TrendAnalyzer.Trend>()
    val issueTrend: LiveData<TrendAnalyzer.Trend> = _issueTrend

    private val _pollTrend = MutableLiveData<TrendAnalyzer.Trend>()
    val pollTrend: LiveData<TrendAnalyzer.Trend> = _pollTrend

    private val _userComparison = MutableLiveData<AnalyticsComparison>()
    val userComparison: LiveData<AnalyticsComparison> = _userComparison

    private val _issueComparison = MutableLiveData<AnalyticsComparison>()
    val issueComparison: LiveData<AnalyticsComparison> = _issueComparison

    private val _pollComparison = MutableLiveData<AnalyticsComparison>()
    val pollComparison: LiveData<AnalyticsComparison> = _pollComparison

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _userActivityData.value = analyticsRepository.getUserActivityData()
                _issueCategoriesData.value = analyticsRepository.getIssueCategoriesData()
                _pollEngagementData.value = analyticsRepository.getPollEngagementData()
                _analyticsStats.value = analyticsRepository.getAnalyticsStats()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun exportData(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val data = analyticsRepository.getExportData(startDate, endDate)
                generateCsvFile(data)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateCsvFile(data: AnalyticsExportData) {
        viewModelScope.launch {
            try {
                val csvContent = buildString {
                    // Header
                    appendLine("Date,Active Users,New Issues,Resolved Issues,Poll Votes")
                    
                    // Data rows
                    data.dailyStats.forEach { stat ->
                        appendLine("${dateFormat.format(stat.date)},${stat.activeUsers},${stat.newIssues},${stat.resolvedIssues},${stat.pollVotes}")
                    }
                }
                
                // Save to file and trigger download
                // Implementation depends on your file storage strategy
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateDateRange(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _userActivityData.value = analyticsRepository.getUserActivityData(startDate, endDate)
                _issueCategoriesData.value = analyticsRepository.getIssueCategoriesData(startDate, endDate)
                _pollEngagementData.value = analyticsRepository.getPollEngagementData(startDate, endDate)
                _analyticsStats.value = analyticsRepository.getAnalyticsStats(startDate, endDate)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun analyzeTrends(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            try {
                val data = analyticsRepository.getAnalyticsData(startDate, endDate)
                
                _userTrend.value = TrendAnalyzer.analyzeTrend(data) { it.activeUsers }
                _issueTrend.value = TrendAnalyzer.analyzeTrend(data) { it.newIssues }
                _pollTrend.value = TrendAnalyzer.analyzeTrend(data) { it.pollVotes }
                
                // Update chart data with moving averages
                updateChartsWithTrends(data)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun updateChartsWithTrends(data: List<AnalyticsDataEntity>) {
        val movingAverages = TrendAnalyzer.calculateMovingAverage(data) { it.activeUsers }
        val anomalies = TrendAnalyzer.detectAnomalies(data) { it.activeUsers }
        
        // Update charts with trend lines and anomaly markers
        // Implementation depends on your chart library
    }

    fun updateDateRangeWithComparison(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Calculate previous period
                val periodLength = endDate.time - startDate.time
                val previousStartDate = Date(startDate.time - periodLength)
                val previousEndDate = Date(endDate.time - periodLength)

                // Get current and previous period data
                val currentData = analyticsRepository.getAnalyticsData(startDate, endDate)
                val previousData = analyticsRepository.getAnalyticsData(previousStartDate, previousEndDate)

                // Update charts with comparison data
                updateChartsWithComparison(currentData, previousData)
                
                // Calculate comparisons
                calculateComparisons(
                    currentPeriod = currentData,
                    previousPeriod = previousData,
                    currentRange = startDate to endDate,
                    previousRange = previousStartDate to previousEndDate
                )

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateComparisons(
        currentPeriod: List<AnalyticsDataEntity>,
        previousPeriod: List<AnalyticsDataEntity>,
        currentRange: Pair<Date, Date>,
        previousRange: Pair<Date, Date>
    ) {
        // Users comparison
        _userComparison.value = createComparison(
            currentPeriod = currentPeriod,
            previousPeriod = previousPeriod,
            currentRange = currentRange,
            previousRange = previousRange,
            valueSelector = { it.activeUsers.toFloat() }
        )

        // Issues comparison
        _issueComparison.value = createComparison(
            currentPeriod = currentPeriod,
            previousPeriod = previousPeriod,
            currentRange = currentRange,
            previousRange = previousRange,
            valueSelector = { it.newIssues.toFloat() }
        )

        // Polls comparison
        _pollComparison.value = createComparison(
            currentPeriod = currentPeriod,
            previousPeriod = previousPeriod,
            currentRange = currentRange,
            previousRange = previousRange,
            valueSelector = { it.pollVotes.toFloat() }
        )
    }

    private fun createComparison(
        currentPeriod: List<AnalyticsDataEntity>,
        previousPeriod: List<AnalyticsDataEntity>,
        currentRange: Pair<Date, Date>,
        previousRange: Pair<Date, Date>,
        valueSelector: (AnalyticsDataEntity) -> Float
    ): AnalyticsComparison {
        val currentValue = currentPeriod.sumOf { valueSelector(it).toDouble() }.toFloat()
        val previousValue = previousPeriod.sumOf { valueSelector(it).toDouble() }.toFloat()
        
        val percentageChange = if (previousValue > 0) {
            ((currentValue - previousValue) / previousValue) * 100
        } else {
            0f
        }

        return AnalyticsComparison(
            currentPeriod = AnalyticsComparison.PeriodData(
                startDate = currentRange.first,
                endDate = currentRange.second,
                value = currentValue
            ),
            previousPeriod = AnalyticsComparison.PeriodData(
                startDate = previousRange.first,
                endDate = previousRange.second,
                value = previousValue
            ),
            percentageChange = percentageChange
        )
    }

    fun toggleTrendLine(visible: Boolean) {
        viewModelScope.launch {
            try {
                val data = analyticsRepository.getAnalyticsData(startDate, endDate)
                val movingAverages = TrendAnalyzer.calculateMovingAverage(data) { it.activeUsers }
                
                _userActivityData.value = _userActivityData.value?.let { currentData ->
                    val entries = currentData.getDataSetByIndex(0).entries
                    updateChartWithTrendLine(entries, movingAverages, visible)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun updateChartWithTrendLine(
        entries: List<Entry>,
        movingAverages: List<Float>,
        showTrendLine: Boolean
    ): LineData {
        val mainDataSet = LineDataSet(entries, "Active Users").apply {
            ChartTheme.applyLineDataSetTheme(this, getApplication())
        }

        return if (showTrendLine) {
            val trendEntries = entries.mapIndexed { index, entry ->
                Entry(entry.x, movingAverages.getOrNull(index) ?: entry.y)
            }
            
            val trendDataSet = LineDataSet(trendEntries, "Trend").apply {
                color = getApplication<Application>().getColor(R.color.trend_line)
                setDrawCircles(false)
                lineWidth = 2f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(false)
            }

            LineData(listOf(mainDataSet, trendDataSet))
        } else {
            LineData(mainDataSet)
        }
    }
} 