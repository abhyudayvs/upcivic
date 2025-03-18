package com.example.civicconnect.ui.admin.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.civicconnect.databinding.DialogAnnotationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AnnotationDialog : BottomSheetDialogFragment() {
    private var _binding: DialogAnnotationBinding? = null
    private val binding get() = _binding!!

    var onAnnotationAdded: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAnnotationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            saveButton.setOnClickListener {
                val text = annotationInput.text.toString()
                if (text.isNotBlank()) {
                    onAnnotationAdded?.invoke(text)
                    dismiss()
                }
            }
            cancelButton.setOnClickListener { dismiss() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 