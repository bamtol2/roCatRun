package com.eeos.rocatrun.ui.components

// 시간 포맷팅 함수
fun formatTime(timeInMillis: Long): String {
    val hours = timeInMillis / (1000 * 60 * 60)
    val minutes = (timeInMillis % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (timeInMillis % (1000 * 60)) / 1000
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

// 페이스 포맷팅 함수
fun formatPace(paceInMinutesPerKm: Double): String {
    val minutes = paceInMinutesPerKm.toInt()
    val seconds = ((paceInMinutesPerKm - minutes) * 60).toInt()
    return String.format("%02d'%02d\"", minutes, seconds)
}

fun formatTimeSec(timeInSecs: Long): String {
    val hours = timeInSecs / 3600
    val minutes = (timeInSecs % 3600) / 60
    val seconds = (timeInSecs % 60)
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}