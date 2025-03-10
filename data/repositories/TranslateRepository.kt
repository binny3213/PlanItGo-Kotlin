package com.example.planitgo_finalproject.data.repositories
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslateRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun getSavedLanguages(): Pair<String, String> {
        val source = sharedPreferences.getString("source_language", null) ?: "he"
        val target = sharedPreferences.getString("target_language", null) ?: "en"
        return Pair(source, target)
    }

    fun saveLanguages(source: String, target: String) {
        val editor = sharedPreferences.edit()
        editor.putString("source_language", source)
        editor.putString("target_language", target)
        editor.apply()
    }
}