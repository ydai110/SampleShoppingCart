package com.example.sampleshoppingcart.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleshoppingcart.R

class ProductItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val itemMagrin =
        context.resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        outRect.left = itemMagrin
        outRect.right = itemMagrin
        outRect.top = itemMagrin
        outRect.bottom = itemMagrin
    }
}
