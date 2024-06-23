package com.giffy.list

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GifsGridSpacingItemDecoration(
    private val margin: Int,
    private val spanCount: Int
) : RecyclerView.ItemDecoration() {

    private val halfMargin = margin / 2

    override fun getItemOffsets(
        outRect: Rect,
        childView: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val left = if (isFirstColumn(childView, parent)) {
            margin
        } else {
            halfMargin
        }

        val right = if (isLastColumn(childView, parent)) {
            margin
        } else {
            halfMargin
        }

        outRect.set(left, halfMargin, right, halfMargin)
    }

    private fun isFirstColumn(
        childView: View,
        parent: RecyclerView
    ) = parent.getChildAdapterPosition(childView) % spanCount == 0

    private fun isLastColumn(
        childView: View,
        parent: RecyclerView
    ) = parent.getChildAdapterPosition(childView) % spanCount == spanCount - 1
}
