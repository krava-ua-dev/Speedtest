package ua.dev.krava.speedtest.presentation.features.history

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ua.dev.krava.speedtest.data.repository.DataRepositoryImpl
import ua.dev.krava.speedtest.presentation.model.TestEntry

/**
 * Created by evheniikravchyna on 04.01.2018.
 */
@InjectViewState
class HistoryPresenter: MvpPresenter<HistoryView>() {
    private var loadingTask: Disposable? = null

    fun loadHistory() {
        this.loadingTask = Observable.create<List<TestEntry>> {
            it.onNext(DataRepositoryImpl.loadTestHistory())
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ history ->
                    if (history.isEmpty()) {
                        viewState.onHistoryEmpty()
                    } else {
                        viewState.onHistoryLoaded(history)
                    }
                }, {
                    viewState.onLoadingError()
                })
    }

    override fun onDestroy() {
        loadingTask?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        super.onDestroy()
    }
}