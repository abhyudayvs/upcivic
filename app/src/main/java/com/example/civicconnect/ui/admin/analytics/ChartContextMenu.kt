package com.example.civicconnect.ui.admin.analytics

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import com.example.civicconnect.R
import com.github.mikephil.charting.data.Entry
import java.util.Date

class ChartContextMenu(
    private val context: Context,
    private val anchor: View,
    private val entry: Entry,
    private val date: Date,
    private val onAddAnnotation: (Entry, String) -> Unit,
    private val onExportPoint: (Entry) -> Unit,
    private val onCompareWithPrevious: (Date) -> Unit
) {
    private val popup = PopupMenu(context, anchor)

    init {
        popup.menuInflater.inflate(R.menu.menu_chart_context, popup.menu)
        setupMenuClickListeners()
    }

    private fun setupMenuClickListeners() {
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add_annotation -> {
                    showAnnotationDialog()
                    true
                }
                R.id.action_export_point -> {
                    onExportPoint(entry)
                    true
                }
                R.id.action_compare -> {
                    onCompareWithPrevious(date)
                    true
                }
                else -> false
            }
        }
    }

    private fun showAnnotationDialog() {
        AnnotationDialog().apply {
            onAnnotationAdded = { text ->
                onAddAnnotation(entry, text)
            }
        }.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager, "annotation")
    }

    fun show() {
        popup.show()
    }
} 