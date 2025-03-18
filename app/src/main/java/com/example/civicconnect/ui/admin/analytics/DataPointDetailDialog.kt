package com.example.civicconnect.ui.admin.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.civicconnect.databinding.DialogDataPointDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataPointDetailDialog : BottomSheetDialogFragment() {
    private var _binding: DialogDataPointDetailBinding? = null
    private val binding get() = _binding!!

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDataPointDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { args ->
            val date = Date(args.getLong(ARG_DATE))
            val value = args.getFloat(ARG_VALUE)
            val label = args.getString(ARG_LABEL, "")
            
            setupUI(date, value, label)
        }
    }

    private fun setupUI(date: Date, value: Float, label: String) {
        with(binding) {
            dateText.text = dateFormat.format(date)
            valueText.text = String.format("%.0f", value)
            labelText.text = label

            closeButton.setOnClickListener { dismiss() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_DATE = "date"
        private const val ARG_VALUE = "value"
        private const val ARG_LABEL = "label"

        fun newInstance(date: Date, value: Float, label: String) = DataPointDetailDialog().apply {
            arguments = Bundle().apply {
                putLong(ARG_DATE, date.time)
                putFloat(ARG_VALUE, value)
                putString(ARG_LABEL, label)
            }
        }
    }
} 