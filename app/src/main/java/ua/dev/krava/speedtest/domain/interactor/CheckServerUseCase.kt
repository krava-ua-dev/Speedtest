package ua.dev.krava.speedtest.domain.interactor

import android.location.Location
import io.reactivex.Observable
import ua.dev.krava.speedtest.data.model.ServerEntity
import ua.dev.krava.speedtest.data.repository.DataRepositoryImpl

/**
 * Created by evheniikravchyna on 05.01.2018.
 */
class CheckServerUseCase(private val countryCode: String, private val city: String, private val region: String, private val location: Location) {

    fun execute(): Observable<ServerEntity> {
        return Observable.create {
            val serversInCountry = DataRepositoryImpl.findServerByCountry(countryCode)
            val currentCity = serversInCountry.filter { it.name == city }
            val server: ServerEntity
            if (currentCity.isNotEmpty()) {
                server = currentCity.first()
            } else {
                val currentRegion = serversInCountry.filter { it.name == region }
                if (currentRegion.isNotEmpty()) {
                    server = currentRegion.first()
                } else {
                    server = serversInCountry.minBy {
                        val loc2 = Location("")
                        loc2.latitude = it.lat
                        loc2.longitude = it.lon
                        location.distanceTo(loc2)
                    } ?: serversInCountry.first()
                }
            }
            it.onNext(server)
            it.onComplete()
        }
    }
}