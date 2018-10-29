package ua.dev.krava.speedtest.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ua.dev.krava.speedtest.data.model.IpInfo
import ua.dev.krava.speedtest.data.model.ServerEntity
import ua.dev.krava.speedtest.presentation.model.TestEntry

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
interface IDataRepository {

    fun checkIpInfo(): Single<IpInfo>

    fun saveTest(test: TestEntry)

    fun loadServers(): Completable

    fun saveServers(servers: List<ServerEntity>)

    fun loadTestHistory(): List<TestEntry>

    fun findServerByCountry(countryCode: String): List<ServerEntity>
}