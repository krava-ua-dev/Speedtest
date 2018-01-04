package ua.dev.krava.speedtest.presentation.features.speedtest

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import org.jetbrains.anko.coroutines.experimental.bg
import ua.dev.krava.speedtest.data.repository.DataRepositoryImpl
import ua.dev.krava.speedtest.presentation.utils.readTextAndClose

/**
 * Created by evheniikravchyna on 02.01.2018.
 */
@InjectViewState
class SpeedTestPresenter: MvpPresenter<TestView>() {
    private lateinit var currentTest: TestState

    fun startTest() {
        bg {
            this.currentTest = TestState()
            viewState.onStartCheckingPing()
            currentTest.host = "www.speedtest.kyivstar.ua"
            val pingResult = pingHost("www.speedtest.kyivstar.ua", 8000)
            if (pingResult == -1) {
                viewState.onPingError()
                return@bg
            } else {
                currentTest.ping = pingResult
                viewState.onPingSuccess(pingResult)
            }

            viewState.onStartDownload()
            val testSocket = SpeedTestSocket()
            testSocket.addSpeedTestListener(object : ISpeedTestListener {
                override fun onError(speedTestError: SpeedTestError?, errorMessage: String?) {
                    Log.e("Main", "error: $errorMessage")
                    viewState.onDownloadComplete()
                }

                override fun onCompletion(report: SpeedTestReport?) {
                    viewState.onDownloadComplete()
                    startUpload()
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
                    val speed = report.transferRateBit.toFloat() / 1048576
                    currentTest.downloadSpeed = speed
                    viewState.onDownloadUpdate(speed)
                }
            })
            testSocket.startFixedDownload("http://2.testdebit.info/fichiers/100Mo.dat", 15000, 100)
        }
    }

    private fun startUpload() {
        viewState.onStartUpload()
        val testSocket = SpeedTestSocket()
        testSocket.addSpeedTestListener(object : ISpeedTestListener {
            override fun onError(speedTestError: SpeedTestError?, errorMessage: String?) {
                Log.e("Main", "error: $errorMessage")
                viewState.onUploadComplete()
            }

            override fun onCompletion(report: SpeedTestReport?) {
                viewState.onUploadComplete()
                currentTest.date = System.currentTimeMillis()
                DataRepositoryImpl.saveTest(currentTest.createEntry())
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                val speed = report.transferRateBit.toFloat() / 1048576
                currentTest.uploadSpeed = speed
                viewState.onUploadUpdate(speed)
            }
        })
        testSocket.startFixedUpload ("http://2.testdebit.info/", 1000000, 10000, 100);
    }

    private fun pingHost(host: String, timeout: Int): Int {
        val runtime = Runtime.getRuntime()
        val cmd = "ping -c 4 -w ${timeout / 1000} $host"
        val proc = runtime.exec(cmd)
        proc.waitFor()
        val exitValue = proc.exitValue()
        return if (exitValue == 0) {
            val commandResult = proc.inputStream.readTextAndClose().split('\n')
            if (commandResult.size > 4) {
                val time1 = commandResult[1].substring(commandResult[1].indexOf("time=") + 5, commandResult[1].indexOf(" ms")).toFloat()
                val time2 = commandResult[2].substring(commandResult[2].indexOf("time=") + 5, commandResult[2].indexOf(" ms")).toFloat()
                val time3 = commandResult[3].substring(commandResult[3].indexOf("time=") + 5, commandResult[3].indexOf(" ms")).toFloat()
                val time4 = commandResult[4].substring(commandResult[4].indexOf("time=") + 5, commandResult[4].indexOf(" ms")).toFloat()

                ((time1 + time2 + time3 + time4) / 4).toInt()
            } else {
                -1
            }
        } else {
            -1
        }
    }
}