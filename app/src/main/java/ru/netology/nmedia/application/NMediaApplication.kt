package ru.netology.nmedia.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.netology.nmedia.auth.AppAuth

@HiltAndroidApp
class NMediaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        AppAuth.initAuth(this)
//        AppAuth.getInstance().setAuth(5, "x-token")

    }
}