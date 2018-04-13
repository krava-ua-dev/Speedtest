package ua.dev.krava.speedtest.domain.interactor

import io.reactivex.Observable
import ua.dev.krava.speedtest.presentation.utils.readTextAndClose

/**
 * Created by evheniikravchyna on 05.01.2018.
 */
class CheckPingUseCase(private val host: String) {

    fun execute(): Observable<Int> {
        return Observable.create({
            val runtime = Runtime.getRuntime()
            val cmd = "ping -c 4 -w 8 $host"
            try {
                val proc = runtime.exec(cmd)
                proc.waitFor()
                val exitValue = proc.exitValue()
                it.onNext(if (exitValue == 0) {
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
                })
            } catch (e: Exception) {
                it.onNext(-1)
            }
        })
    }
}