package ua.dev.krava.speedtest.domain.interactor

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ua.dev.krava.speedtest.data.repository.DataRepositoryImpl
import ua.dev.krava.speedtest.presentation.features.speedtest.TestState

/**
 * Created by evheniikravchyna on 05.01.2018.
 */
class SaveTestResultUseCase(private val testState: TestState) {

    fun execute() {
        Observable
                .create<Any> {
                    testState.date = System.currentTimeMillis()
                    DataRepositoryImpl.saveTest(testState.createEntry())
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }
}