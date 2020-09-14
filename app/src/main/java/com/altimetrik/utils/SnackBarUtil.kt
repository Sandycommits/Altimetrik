package com.altimetrik.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * This extension is used to display the SnackBar
 */
fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}