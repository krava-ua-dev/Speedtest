package ua.dev.krava.speedtest.presentation

import android.app.Application
import com.facebook.stetho.Stetho
import ua.dev.krava.speedtest.BuildConfig
import ua.dev.krava.speedtest.data.repository.DataRepositoryImpl
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.Crashlytics



/**
 * Created by evheniikravchyna on 03.01.2018.
 */
class SpeedTestApp: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
        val fabric = Fabric.Builder(this)
                .kits(Crashlytics())
                .debuggable(true)
                .build()
        Fabric.with(fabric)

        DataRepositoryImpl.init(applicationContext)
    }
}