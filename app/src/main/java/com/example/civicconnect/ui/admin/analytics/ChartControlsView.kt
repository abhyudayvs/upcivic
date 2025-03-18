package com.example.civicconnect.ui.admin.analytics

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.civicconnect.databinding.ViewChartControlsBinding

class ChartControlsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewChartControlsBinding.inflate(LayoutInflater.from(context), this, true)
    
    var onZoomIn: (() -> Unit)? = null
    var onZoomOut: (() -> Unit)? = null
    var onReset: (() -> Unit)? = null
    var onHighlightToggled: ((Boolean) -> Unit)? = null

    init {
        with(binding) {
            zoomInButton.setOnClickListener { onZoomIn?.invoke() }
            zoomOutButton.setOnClickListener { onZoomOut?.invoke() }
            resetButton.setOnClickListener { onReset?.invoke() }
            highlightSwitch.setOnCheckedChangeListener { _, isChecked ->
                onHighlightToggled?.invoke(isChecked)
            }
        }
    }
} 