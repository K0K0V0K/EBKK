package hu.koko.service

import android.content.Context
import androidx.core.content.edit

class ContextStorage(val applicationContext: Context) {

    fun getLastRun() = applicationContext
        .getSharedPreferences("ebkk", Context.MODE_PRIVATE)
        .getLong("last_run_time", 0L)

    fun updateLastRun() = applicationContext
        .getSharedPreferences("ebkk", Context.MODE_PRIVATE)
        .edit() {putLong("last_run_time", System.currentTimeMillis())}

    fun getUser() = applicationContext
        .getSharedPreferences("ebkk", Context.MODE_PRIVATE)
        .getString("user", "")

    fun setUser(user: String) = applicationContext
        .getSharedPreferences("ebkk", Context.MODE_PRIVATE)
        .edit() { putString("user", user) }
}