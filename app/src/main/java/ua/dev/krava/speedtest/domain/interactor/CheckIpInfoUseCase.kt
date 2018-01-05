package ua.dev.krava.speedtest.domain.interactor

import io.reactivex.Observable
import okhttp3.OkHttpClient
import org.json.JSONObject
import ua.dev.krava.speedtest.data.model.IpInfo
import ua.dev.krava.speedtest.data.utils.makeCall
import ua.dev.krava.speedtest.presentation.utils.readTextAndClose

/**
 * Created by evheniikravchyna on 05.01.2018.
 */
class CheckIpInfoUseCase {
    private val IP_INFO = "http://ip-api.com/json"

    fun execute(): Observable<IpInfo> {
        return Observable.create({
            val ipResponse = OkHttpClient().makeCall(IP_INFO)
            if (ipResponse.isSuccessful) try {
                val responseString = ipResponse.body()?.byteStream()?.readTextAndClose()
                it.onNext(IpInfo(JSONObject(responseString)))
                it.onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }
}