package ua.dev.krava.speedtest.domain.interactor

import android.util.Log
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import ua.dev.krava.speedtest.domain.utils.MathUtils

/**
 * Created by evheniikravchyna on 05.01.2018.
 */
class DownloadTestUseCase(private val url: String) {

    fun execute(): Flowable<Float> {
        val socket = SpeedTestSocket()
        return Flowable.create({
            socket.addSpeedTestListener(object : ISpeedTestListener {
                override fun onError(speedTestError: SpeedTestError?, errorMessage: String?) {
                    if (!it.isCancelled) {
                        it.onError(Throwable(errorMessage))
                    }
                    Log.e("Main", "error: $errorMessage")
                    socket.closeSocket()
                    socket.clearListeners()
                }

                override fun onCompletion(report: SpeedTestReport?) {
                    socket.closeSocket()
                    socket.clearListeners()
                    if (!it.isCancelled) it.onComplete()
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
                    if (!it.isCancelled) {
                        val speed = report.transferRateBit.toDouble() / 1048576
                        it.onNext(MathUtils.round(speed.toFloat(), 2))
                    } else {
                        socket.closeSocket()
                        socket.clearListeners()
                    }
                }
            })
            socket.startFixedDownload(url.replace("upload.php", "random3000x3000.jpg"), 15000, 250)
        }, BackpressureStrategy.MISSING)
    }
}