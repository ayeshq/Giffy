package com.giffy.util

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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

/**
 * Creates a flow that emits the value of the ime action search event
 */
fun EditText.onImeActionSearch(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = TextView.OnEditorActionListener { v, actionId, _ ->
            if (EditorInfo.IME_ACTION_SEARCH == actionId) {
                hideKeyboard()
                trySend(v.text)
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        }
        setOnEditorActionListener(listener)
        awaitClose { setOnEditorActionListener(null) }
    }
}

/**
 * Creates a flow that emits the value of the changed text event
 */
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val textWatcher = addTextChangedListener(afterTextChanged = { text ->
            trySend(text)
        })
        awaitClose { removeTextChangedListener(textWatcher) }
    }
}

fun View.hideKeyboard() {
    val imm = context?.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
