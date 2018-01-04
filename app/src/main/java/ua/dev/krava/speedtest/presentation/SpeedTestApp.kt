package ua.dev.krava.speedtest.presentation

import android.app.Application
import com.facebook.stetho.Stetho
import ua.dev.krava.speedtest.data.repository.DataRepositoryImpl

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
class SpeedTestApp: Application() {

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

        DataRepositoryImpl.init(applicationContext)
    }
}