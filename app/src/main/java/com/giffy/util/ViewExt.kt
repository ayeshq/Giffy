package com.giffy.util

import android.widget.TextView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel

/**
 * Applies age restriction style to this TextView
 */
fun TextView.asAgeRestrictionBadge() {
    val shapeAppearanceModel: ShapeAppearanceModel =  ShapeAppearanceModel()
        .toBuilder()
        .setAllCorners(RoundedCornerTreatment())
        .setAllCornerSizes(RelativeCornerSize(0.5f))
        .build()
    val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
    this.background = shapeDrawable
}
