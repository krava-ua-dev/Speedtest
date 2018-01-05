package ua.dev.krava.speedtest.data.model

import org.json.JSONObject

/**
 * Created by evheniikravchyna on 05.01.2018.
 */
class IpInfo(json: JSONObject) {
    val country = json.getString("country")
    val cc = json.getString("countryCode")
    val city = json.getString("city")
    val region = json.getString("regionName")
    val lat = json.getDouble("lat")
    val lon = json.getDouble("lon")
    val isp = json.getString("isp")
}