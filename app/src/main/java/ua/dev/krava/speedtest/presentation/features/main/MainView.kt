package ua.dev.krava.speedtest.presentation.features.main

import com.arellomobile.mvp.MvpView

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
interface MainView: MvpView {
    fun onStartLoadingServers()
    fun onServersLoaded(autoStart: Boolean = false)
    fun onServersLoadingError()
}