package ua.dev.krava.speedtest.data.model

import org.json.JSONObject

/**
 * Created by evheniikravchyna on 05.01.2018.
 */
class IpInfo {
    lateinit var country: String
    lateinit var cc: String
    lateinit var city: String
    lateinit var region: String
    lateinit var isp: String
    var lat: Double = 0.0
    var lon: Double = 0.0


    constructor(json: JSONObject) {
        country = json.getString("country")
        cc = json.getString("countryCode")
        city = json.getString("city")
        region = json.getString("regionName")
        lat = json.getDouble("lat")
        lon = json.getDouble("lon")
        isp = json.getString("isp")
    }

    constructor() {

    }
}