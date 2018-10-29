package ua.dev.krava.speedtest.data.repository

import android.arch.persistence.room.Room
import android.content.Context
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import ua.dev.krava.speedtest.BuildConfig
import ua.dev.krava.speedtest.data.model.IpInfo
import ua.dev.krava.speedtest.data.model.ServerEntity
import ua.dev.krava.speedtest.data.repository.database.AppDatabase
import ua.dev.krava.speedtest.data.utils.makeCall
import ua.dev.krava.speedtest.domain.repository.IDataRepository
import ua.dev.krava.speedtest.presentation.model.TestEntry
import ua.dev.krava.speedtest.presentation.utils.createXmlParser
import ua.dev.krava.speedtest.presentation.utils.readTextAndClose
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by evheniikravchyna on 03.01.2018.
 */
object DataRepositoryImpl : IDataRepository {
    private lateinit var db: AppDatabase


    fun init(context: Context) {
        this.db = Room.databaseBuilder(context, AppDatabase::class.java, "db-speedtest").build()
    }


    override fun findServerByCountry(countryCode: String): List<ServerEntity> {
        return db.servers().findByCountryCode(countryCode)
    }

    override fun saveServers(servers: List<ServerEntity>) {
        db.servers().insertAll(servers)
    }

    override fun loadTestHistory(): List<TestEntry> {
        return db.history().getAll().map { it.toEntry() }
    }

    override fun saveTest(test: TestEntry) {
        db.history().insert(test.toEntity())
    }

    override fun checkIpInfo(): Single<IpInfo> {
        return Single
                .create { emitter: SingleEmitter<IpInfo> ->
                    val ipResponse = OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .build()
                            .makeCall(BuildConfig.IP_INFO_URL)
                    if (!emitter.isDisposed) {
                        if (ipResponse.isSuccessful) {
                            val responseString = ipResponse.body()?.byteStream()?.readTextAndClose()
                            emitter.onSuccess(IpInfo(JSONObject(responseString)))
                        } else {
                            emitter.onError(Exception("Speedtest Unknown Exception"))
                        }
                    }
                }
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadServers(): Completable {
        return Completable.create { emitter ->
            try {
                val response = OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .addInterceptor(getLoggingInterceptor())
                        .build()
                        .makeCall(BuildConfig.SERVERS_LIST_URL)
                if (response.isSuccessful) {
                    val bodyStream = response.body()?.byteStream()
                    bodyStream?.use {
                        val parser = it.createXmlParser()
                        parser.require(XmlPullParser.START_TAG, null, "settings")
                        parser.next()
                        val servers = ArrayList<ServerEntity>()
                        do {
                            if (parser.name == "server" && parser.eventType == XmlPullParser.START_TAG) {
                                val server = ServerEntity()
                                server.id = parser.getAttributeValue(null, "id").toInt()
                                server.cc = parser.getAttributeValue(null, "cc")
                                server.country = parser.getAttributeValue(null, "country")
                                server.host = parser.getAttributeValue(null, "host")
                                server.lat = parser.getAttributeValue(null, "lat").toDouble()
                                server.lon = parser.getAttributeValue(null, "lon").toDouble()
                                server.name = parser.getAttributeValue(null, "name")
                                server.sponsor = parser.getAttributeValue(null, "sponsor")
                                server.url = parser.getAttributeValue(null, "url")
                                servers.add(server)
                            }
                            parser.next()
                        } while (parser.name != "servers" || parser.eventType != XmlPullParser.END_TAG)
                        if (!emitter.isDisposed) {
                            if (servers.size > 0) {
                                saveServers(servers)
                                emitter.onComplete()
                            } else {
                                emitter.onError(Throwable("Error during loading servers"))
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                if (!emitter.isDisposed) {
                    emitter.onError(e)
                }
            }
        }
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
    }


    private fun getLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}