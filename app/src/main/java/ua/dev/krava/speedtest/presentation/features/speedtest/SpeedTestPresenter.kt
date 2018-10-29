package ua.dev.krava.speedtest.presentation.features.speedtest

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.CompositeDisposable
import ua.dev.krava.speedtest.BuildConfig
import ua.dev.krava.speedtest.data.repository.DataRepositoryImpl
import ua.dev.krava.speedtest.domain.interactor.*

/**
 * Created by evheniikravchyna on 02.01.2018.
 */
@InjectViewState
class SpeedTestPresenter: MvpPresenter<TestView>() {
    private lateinit var currentTest: TestState
    private var currentDisposable = CompositeDisposable()
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
        currentDisposable.add(DataRepositoryImpl.checkIpInfo()
                .subscribe({ ipInfo ->
                    viewState.onLocation(ipInfo.city)
                    val myLocation = Location("")
                    myLocation.latitude = ipInfo.lat
                    myLocation.longitude = ipInfo.lon
                    checkServer(ipInfo.cc, ipInfo.city, ipInfo.region, myLocation)
                }, {
                    if (BuildConfig.DEBUG) it.printStackTrace()
                    viewState.onServerError()
                }))
    }

    private fun checkServer(countryCode: String, city: String, region: String, location: Location) {
        viewState.onCheckServer()
        currentDisposable.add(CheckServerUseCase(countryCode, city, region, location)
                .execute()
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
                    if (BuildConfig.DEBUG) it.printStackTrace()
                    viewState.onServerError()
                }))
    }

    private fun startDownload() {
        viewState.onStartDownload()
        this.currentDisposable.add(DownloadTestUseCase(currentTest.server.url)
                .execute()
                .subscribe({ speed ->
                    currentTest.downloadSpeed = speed
                    viewState.onDownloadUpdate(speed)
                }, {
                    if (BuildConfig.DEBUG) it.printStackTrace()
                }, {
                    viewState.onDownloadComplete()
                    startUpload()
                }))
    }

    private fun startUpload() {
        viewState.onStartUpload()
        this.currentDisposable.add(UploadTestUseCase(currentTest.server.url)
                .execute()

                .subscribe({ speed ->
                    currentTest.uploadSpeed = speed
                    viewState.onUploadUpdate(speed)
                }, {
                    if (BuildConfig.DEBUG) it.printStackTrace()
                }, {
                    viewState.onUploadComplete()
                    saveTestResult()
                }))
    }

    private fun pingHost(host: String) {
        this.currentDisposable.add(CheckPingUseCase(host)
                .execute()
                .subscribe({ pingResult ->
                    currentTest.ping = pingResult
                    viewState.onPingSuccess(pingResult)
                    startDownload()
                }, {
                    viewState.onPingError()
                }))
    }

    private fun saveTestResult() {
        SaveTestResultUseCase(currentTest).execute()
    }

    override fun onDestroy() {
        if (!currentDisposable.isDisposed) currentDisposable.clear()
        super.onDestroy()
    }
}