package com.example.civicconnect.ui.admin.analytics

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.civicconnect.databinding.DialogDateRangeBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateRangeDialog : BottomSheetDialogFragment() {
    private var _binding: DialogDateRangeBinding? = null
    private val binding get() = _binding!!
    
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    private var startDate: Date = calendar.apply { add(Calendar.MONTH, -1) }.time
    private var endDate: Date = calendar.apply { add(Calendar.MONTH, 1) }.time
    
    var onDateRangeSelected: ((Date, Date) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDateRangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        updateDateTexts()
    }

    private fun setupViews() {
        with(binding) {
            startDateButton.setOnClickListener { showStartDatePicker() }
            endDateButton.setOnClickListener { showEndDatePicker() }
            
            applyButton.setOnClickListener {
                onDateRangeSelected?.invoke(startDate, endDate)
                dismiss()
            }
            
            cancelButton.setOnClickListener { dismiss() }

            // Preset buttons
            lastWeekButton.setOnClickListener { setLastWeek() }
            lastMonthButton.setOnClickListener { setLastMonth() }
            last3MonthsButton.setOnClickListener { setLast3Months() }
            yearToDateButton.setOnClickListener { setYearToDate() }
        }
    }

    private fun showStartDatePicker() {
        val calendar = Calendar.getInstance().apply { time = startDate }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                startDate = calendar.time
                updateDateTexts()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showEndDatePicker() {
        val calendar = Calendar.getInstance().apply { time = endDate }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                endDate = calendar.time
                updateDateTexts()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateTexts() {
        binding.startDateButton.text = dateFormat.format(startDate)
        binding.endDateButton.text = dateFormat.format(endDate)
    }

    private fun setLastWeek() {
        val cal = Calendar.getInstance()
        endDate = cal.time
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        startDate = cal.time
        updateDateTexts()
    }

    private fun setLastMonth() {
        val cal = Calendar.getInstance()
        endDate = cal.time
        cal.add(Calendar.MONTH, -1)
        startDate = cal.time
        updateDateTexts()
    }

    private fun setLast3Months() {
        val cal = Calendar.getInstance()
        endDate = cal.time
        cal.add(Calendar.MONTH, -3)
        startDate = cal.time
        updateDateTexts()
    }

    private fun setYearToDate() {
        val cal = Calendar.getInstance()
        endDate = cal.time
        cal.set(Calendar.MONTH, 0)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        startDate = cal.time
        updateDateTexts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 