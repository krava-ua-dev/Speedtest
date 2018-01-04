package ua.dev.krava.speedtest.presentation.features.history

import com.arellomobile.mvp.MvpView
import ua.dev.krava.speedtest.presentation.model.TestEntry

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
interface HistoryView: MvpView {

    fun onHistoryLoaded(history: List<TestEntry>)

    fun onHistoryEmpty()

    fun onLoadingError()
}