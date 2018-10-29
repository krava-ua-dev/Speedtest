package ua.dev.krava.speedtest.domain.interactor

import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.IRepeatListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ua.dev.krava.speedtest.BuildConfig
import ua.dev.krava.speedtest.domain.utils.MathUtils

/**
 * Created by evheniikravchyna on 05.01.2018.
 */
class DownloadTestUseCase(private val url: String) {

    fun execute(): Flowable<Float> {
        val socket = SpeedTestSocket()
        return Flowable
                .create({ emitter: FlowableEmitter<Float> ->
                    socket.startDownloadRepeat(url.replace("upload.php", "random3000x3000.jpg"), BuildConfig.TEST_REPEAT_WINDOW, BuildConfig.TEST_REPORT_INTERVAL, object : IRepeatListener {
                        override fun onCompletion(report: SpeedTestReport?) {
                            socket.closeSocket()
                            socket.clearListeners()
                            if (!emitter.isCancelled) emitter.onComplete()
                        }

                        override fun onReport(report: SpeedTestReport) {
                            if (!emitter.isCancelled) {
                                val speed = report.transferRateBit.toDouble() / MBITS_KOF
                                emitter.onNext(MathUtils.round(speed.toFloat(), 2))
                            } else {
                                socket.closeSocket()
                                socket.clearListeners()
                            }
                        }
                    })
                }, BackpressureStrategy.MISSING)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
    }

    companion object {
        private val MBITS_KOF = 1048576
    }
}