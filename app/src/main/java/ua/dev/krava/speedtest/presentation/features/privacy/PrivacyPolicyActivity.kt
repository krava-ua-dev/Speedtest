package ua.dev.krava.speedtest.presentation.features.privacy

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import ua.dev.krava.speedtest.R

/**
 * Date: 10/26/18
 *
 * @author evheniikravchyna
 */
class PrivacyPolicyActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_web_view)

        val webView = findViewById<WebView>(R.id.webview)
        webView.loadUrl("file:///android_res/raw/privacy_policy.html")
    }

}