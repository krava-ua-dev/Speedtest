package ua.dev.krava.speedtest.domain.interactor

import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.IRepeatListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import ua.dev.krava.speedtest.BuildConfig
import ua.dev.krava.speedtest.domain.utils.MathUtils

/**
 * Created by evheniikravchyna on 05.01.2018.
 */
class DownloadTestUseCase(private val url: String) {

    fun execute(): Flowable<Float> {
        val socket = SpeedTestSocket()
        return Flowable.create({
            socket.startDownloadRepeat(url.replace("upload.php", "random3000x3000.jpg"), BuildConfig.TEST_REPEAT_WINDOW, BuildConfig.TEST_REPORT_INTERVAL, object: IRepeatListener {
                override fun onCompletion(report: SpeedTestReport?) {
                    socket.closeSocket()
                    socket.clearListeners()

                    if (!it.isCancelled) it.onComplete()
                }

                override fun onReport(report: SpeedTestReport) {
                    if (!it.isCancelled) {
                        val speed = report.transferRateBit.toDouble() / MBITS_KOF
                        it.onNext(MathUtils.round(speed.toFloat(), 2))
                    } else {
                        socket.closeSocket()
                        socket.clearListeners()
                    }
                }
            })
        }, BackpressureStrategy.MISSING)
    }

    companion object {
        private val MBITS_KOF = 1048576
    }
}