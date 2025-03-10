package com.example.planitgo_finalproject.utils

import java.util.Locale

fun getDeviceLanguage(): String {
    val currentLocale = Locale.getDefault()
    return if (currentLocale.language == "iw") "he" else "en"
}