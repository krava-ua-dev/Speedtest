package ua.dev.krava.speedtest.presentation.features.main

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ua.dev.krava.speedtest.data.repository.DataRepositoryImpl
import ua.dev.krava.speedtest.domain.preferences.ValueSettings

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
@InjectViewState
class MainPresenter: MvpPresenter<MainView>() {
    private lateinit var valueSettings: ValueSettings

    fun onCreate(valueSettings: ValueSettings) {
        this.valueSettings = valueSettings

        if (valueSettings.needLoadServers()) {
            viewState.onStartLoadingServers()
            DataRepositoryImpl.loadServers()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ isSuccess ->
                        if (isSuccess) {
                            valueSettings.serversLoadedAt(System.currentTimeMillis())
                            viewState.onServersLoaded()
                        }
                    }, { throwable -> throwable.printStackTrace() })
        } else {
            viewState.onServersLoaded()
        }
    }
}