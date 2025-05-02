package hu.koko.service

import android.util.Log

class LoggerService {
    companion object {
        fun log(msg: Any) {
            Log.i("EBKK", msg.toString())
        }
        fun error(exception: Exception) {
            Log.i("EBKK", exception.toString(), exception)
        }
    }
}