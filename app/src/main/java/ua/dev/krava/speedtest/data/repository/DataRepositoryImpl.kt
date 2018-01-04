package ua.dev.krava.speedtest.data.repository

import okhttp3.OkHttpClient
import org.jetbrains.anko.coroutines.experimental.bg
import org.xmlpull.v1.XmlPullParser
import ua.dev.krava.speedtest.data.model.ServerEntity
import ua.dev.krava.speedtest.data.utils.makeCall
import ua.dev.krava.speedtest.domain.repository.IDataRepository
import ua.dev.krava.speedtest.presentation.utils.createXmlParser
import ua.dev.krava.speedtest.data.repository.database.AppDatabase
import android.arch.persistence.room.Room
import android.content.Context
import io.reactivex.Observable
import ua.dev.krava.speedtest.presentation.model.TestEntry
import java.io.IOException
import java.util.ArrayList


/**
 * Created by evheniikravchyna on 03.01.2018.
 */
object DataRepositoryImpl: IDataRepository {
    private val SERVERS_LIST_URL = "http://www.speedtest.net/speedtest-servers.php"
    private lateinit var db: AppDatabase


    fun init(context: Context) {
        this.db = Room.databaseBuilder(context, AppDatabase::class.java, "db-speedtest").build()
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

    override fun loadServers(): Observable<Boolean> {
        return Observable.create<Boolean> { emitter ->
            try {
                val response = OkHttpClient().makeCall(SERVERS_LIST_URL)
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
                        if (servers.size > 0) {
                            saveServers(servers)
                            emitter.onNext(true)
                        } else {
                            emitter.onNext(false)
                        }
                    }
                }
            } catch (e: IOException) {
                emitter.onError(e)
            }
        }
    }
}