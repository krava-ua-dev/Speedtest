package ua.dev.krava.speedtest.presentation.features.speedtest

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ua.dev.krava.speedtest.data.repository.DataRepositoryImpl
import ua.dev.krava.speedtest.domain.interactor.*

/**
 * Created by evheniikravchyna on 02.01.2018.
 */
@InjectViewState
class SpeedTestPresenter: MvpPresenter<TestView>() {
    private lateinit var currentTest: TestState
    private var currentDisposable: Disposable? = null
    var autoStartTest = false


    fun onViewCreated() {
        if (autoStartTest) {
            autoStartTest = false
            startTest()
        } else {
            viewState.showDefaultState()
        }
    }

    fun startTest() {
        this.viewState.onTestStarted()
        this.currentTest = TestState()
        checkIpInfo()
    }

    private fun checkIpInfo() {
        viewState.onCheckLocation()
        this.currentDisposable = DataRepositoryImpl.checkIpInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ ipInfo ->
                    viewState.onLocation(ipInfo.city)
                    val myLocation = Location("")
                    myLocation.latitude = ipInfo.lat
                    myLocation.longitude = ipInfo.lon
                    checkServer(ipInfo.cc, ipInfo.city, ipInfo.region, myLocation)
                }, {
                    it.printStackTrace()
                })
    }

    private fun checkServer(countryCode: String, city: String, region: String, location: Location) {
        viewState.onCheckServer()
        this.currentDisposable = CheckServerUseCase(countryCode, city, region, location)
                .execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    currentTest.server = it
                    var tempHost = currentTest.server.host
                    if (tempHost.contains(':')) {
                        tempHost = tempHost.substring(0, tempHost.indexOf(':'))
                    }
                    viewState.onServerReady(tempHost)
                    viewState.onStartCheckingPing()
                    pingHost(tempHost)
                }, {
                    it.printStackTrace()
                })
    }

    private fun startDownload() {
        viewState.onStartDownload()
        this.currentDisposable = DownloadTestUseCase(currentTest.server.url)
                .execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ speed ->
                    currentTest.downloadSpeed = speed
                    viewState.onDownloadUpdate(speed)
                }, {
                    it.printStackTrace()
                }, {
                    viewState.onDownloadComplete()
                    startUpload()
                })
    }

    private fun startUpload() {
        viewState.onStartUpload()
        this.currentDisposable = UploadTestUseCase(currentTest.server.url)
                .execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ speed ->
                    currentTest.uploadSpeed = speed
                    viewState.onUploadUpdate(speed)
                }, {
                    it.printStackTrace()
                }, {
                    viewState.onUploadComplete()
                    saveTestResult()
                })
    }

    private fun pingHost(host: String) {
        this.currentDisposable = CheckPingUserCase(host)
                .execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pingResult ->
                    if (pingResult == -1) {
                        viewState.onPingError()
                    } else {
                        currentTest.ping = pingResult
                        viewState.onPingSuccess(pingResult)
                        startDownload()
                    }
                }, {
                    viewState.onPingError()
                })
    }

    private fun saveTestResult() {
        SaveTestResultUseCase(currentTest).execute()
    }

    override fun onDestroy() {
        currentDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
        super.onDestroy()
    }
}