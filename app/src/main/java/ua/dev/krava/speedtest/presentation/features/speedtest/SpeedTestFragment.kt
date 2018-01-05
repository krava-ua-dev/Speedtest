package ua.dev.krava.speedtest.presentation.features.speedtest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_test.*
import ua.dev.krava.speedtest.R
import ua.dev.krava.speedtest.presentation.widget.SpeedometerView

/**
 * Created by evheniikravchyna on 01.01.2018.
 */
class SpeedTestFragment: MvpAppCompatFragment(), TestView {
    @InjectPresenter
    lateinit var presenter: SpeedTestPresenter
    private lateinit var btnStartTest: View
    private lateinit var btnRepeatTest: View
    private lateinit var progressView: SpeedometerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test, container, false)
        progressView = view.findViewById(R.id.progressView)
        btnStartTest = view.findViewById(R.id.btnStartTest)
        btnRepeatTest = view.findViewById(R.id.btnRepeatTest)
        btnStartTest.setOnClickListener {
            btnStartTest.visibility = View.GONE
            testResultContainer.visibility = View.VISIBLE
            presenter.startTest()
        }
        btnRepeatTest.setOnClickListener {
            btnRepeatTest.visibility = View.GONE
            speedContainer.visibility = View.GONE
            presenter.startTest()
        }
        return view
    }

    override fun onPingSuccess(timeMS: Int) {
        pingProgressIndicator.hide()
        pingValue.text = "$timeMS ms"
        pingValue.visibility = View.VISIBLE
        speedContainer.visibility =  View.VISIBLE
    }

    override fun onPingError() {
        pingProgressIndicator.hide()
        pingProgressIndicator.visibility = View.GONE
        pingValue.text = "Error"
        btnRepeatTest.visibility = View.VISIBLE
    }

    override fun onStartCheckingPing() {
        pingValue.visibility = View.GONE
        pingProgressIndicator.show()
    }

    override fun onStartDownload() {
        downloadContainer.visibility = View.VISIBLE
    }

    override fun onDownloadComplete() {
//        activity?.runOnUiThread {
//
//        }
    }

    override fun onStartUpload() {
        activity?.runOnUiThread {
            uploadContainer.visibility = View.VISIBLE
        }
    }

    override fun onUploadUpdate(progress: Float) {
        Handler(Looper.getMainLooper()).postDelayed({
            progressView.progress = progress
            uploadValue.text = "${progressView.progress}"
        }, 16)
    }

    override fun onUploadComplete() {
        btnRepeatTest.visibility = View.VISIBLE
    }


    override fun onDownloadUpdate(progress: Float) {
        Handler(Looper.getMainLooper()).postDelayed({
            progressView.progress = progress
            downloadValue.text = "${progressView.progress}"
        }, 16)
    }

    override fun onCheckLocation() {
        activity?.runOnUiThread {
            locationValue.visibility = View.GONE
            locationProgressIndicator.show()
        }
    }

    override fun onLocation(city: String) {
        activity?.runOnUiThread {
            locationProgressIndicator.hide()
            locationValue.text = city
            locationValue.visibility = View.VISIBLE
        }
    }

    override fun onCheckServer() {

    }

    override fun onServerReady(host: String) {
        activity?.runOnUiThread {
            hostValue.text = host
            hostValue.visibility = View.VISIBLE
        }
    }
}
