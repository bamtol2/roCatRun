package com.eeos.rocatrun.util

import kotlin.math.roundToInt

class FormatUtils {

    fun formatPace(pace: Double): String {
        val minutes = pace.toInt()
        val seconds = ((pace - minutes) * 60).roundToInt()
        return String.format("%d'%02d\"", minutes, seconds)
    }

    fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60))

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}