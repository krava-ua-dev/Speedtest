package ua.dev.krava.speedtest.domain.interactor

import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.IRepeatListener
import fr.bmartel.speedtest.model.UploadStorageType
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import ua.dev.krava.speedtest.BuildConfig
import ua.dev.krava.speedtest.domain.utils.MathUtils


/**
 * Created by evheniikravchyna on 05.01.2018.
 */
class UploadTestUseCase(private val url: String) {

    fun execute(): Flowable<Float> {
        val socket = SpeedTestSocket()
        socket.uploadStorageType = UploadStorageType.FILE_STORAGE
        return Flowable.create({
            socket.startUploadRepeat (url, BuildConfig.TEST_REPEAT_WINDOW, BuildConfig.TEST_REPORT_INTERVAL, BuildConfig.FILE_SIZE_OCTET, object: IRepeatListener {
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