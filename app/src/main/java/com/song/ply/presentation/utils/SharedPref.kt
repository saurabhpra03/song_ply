package com.song.ply.presentation.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPref @Inject constructor(
    @param: ApplicationContext private val context: Context
) {
    private val PREF_NAME = "SongPly"
    private val KEY_IS_DARK_THEME = "is_dark_theme"


    fun setTheme(isDarkTheme: Boolean) =
        context.getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit {
            putBoolean(KEY_IS_DARK_THEME, isDarkTheme)
            apply()
        }

    fun getTheme(): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        return pref.getBoolean(KEY_IS_DARK_THEME, false)
    }

}