package com.example.myapplication

import android.content.Context

object ScreenUtils {
    fun getScreenWidthInCm(context: Context): Float {
        val displayMetrics = context.resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels
        val densityDpi = displayMetrics.densityDpi.toFloat()

        // Convert pixels to inches and then to cm
        val widthInInches = widthPixels / densityDpi
        val widthInCm = widthInInches * 2.54f
        return widthInCm
    }
}