package ua.dev.krava.speedtest.presentation.features.main

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.Disposable
import ua.dev.krava.speedtest.data.repository.DataRepositoryImpl
import ua.dev.krava.speedtest.domain.preferences.ValueSettings

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {
    private lateinit var valueSettings: ValueSettings
    private var loadingTask: Disposable? = null

    fun onCreate(valueSettings: ValueSettings) {
        this.valueSettings = valueSettings

        checkServers()
    }

    private fun checkServers(autoStartTest: Boolean = false) {
        if (valueSettings.needLoadServers()) {
            loadServers()
        } else {
            viewState.onServersLoaded(autoStartTest)
        }
    }

    fun loadServers() {
        viewState.onStartLoadingServers()
        this.loadingTask = DataRepositoryImpl.loadServers()
                .subscribe({
                            valueSettings.serversLoadedAt(System.currentTimeMillis())
                            viewState.onServersLoaded()
                        },
                        {
                            viewState.onServersLoadingError()
                        })
    }

    fun onFastStartSpeedtest() {
        checkServers(true)
    }

    override fun onDestroy() {
        this.loadingTask?.let {
            if (!it.isDisposed) it.dispose()
        }
        super.onDestroy()
    }
}