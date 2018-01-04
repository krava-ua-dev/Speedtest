package ua.dev.krava.speedtest.presentation.features.speedtest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    private lateinit var statusLabel: TextView
    private lateinit var btnStartTest: View
    private lateinit var progressView: SpeedometerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test, container, false)
        statusLabel = view.findViewById(R.id.title_view)
        progressView = view.findViewById(R.id.progressView)
        btnStartTest = view.findViewById(R.id.btnStartTest)
        btnStartTest.setOnClickListener {
            btnStartTest.visibility = View.GONE
            presenter.startTest()
        }
        return view
    }

    override fun onPingSuccess(timeMS: Int) {
        activity?.runOnUiThread {
            testResultContainer.visibility = View.VISIBLE
            pingContainer.visibility = View.VISIBLE
            pingValue.text = "$timeMS ms"
        }
    }

    override fun onPingError() {
        activity?.runOnUiThread {
            statusLabel.text = "Error"
            btnStartTest.visibility = View.VISIBLE
        }
    }

    override fun onStartCheckingPing() {
        activity?.runOnUiThread {
            statusLabel.text = "Checking ping..."
        }
    }

    override fun onStartDownload() {
        activity?.runOnUiThread {
            statusLabel.text = "Downloading..."
            speed_container.visibility = View.VISIBLE
        }
    }

    override fun onDownloadComplete() {
        activity?.runOnUiThread {
            downloadValue.text = tvSpeedValue.text
            speed_container.visibility = View.GONE
            downloadContainer.visibility = View.VISIBLE
        }
    }

    override fun onStartUpload() {
        activity?.runOnUiThread {
            statusLabel.text = "Uploading..."
            speed_container.visibility = View.VISIBLE
        }
    }

    override fun onUploadUpdate(progress: Float) {
        Handler(Looper.getMainLooper()).postDelayed({
            progressView.progress = progress
            tvSpeedValue.text = "${progressView.progress}"
        }, 16)
    }

    override fun onUploadComplete() {
        activity?.runOnUiThread {
            statusLabel.text = "Completed"
            uploadValue.text = tvSpeedValue.text
            speed_container.visibility = View.GONE
            uploadContainer.visibility = View.VISIBLE
        }
    }


    override fun onDownloadUpdate(progress: Float) {
        Handler(Looper.getMainLooper()).postDelayed({
            progressView.progress = progress
            tvSpeedValue.text = "${progressView.progress}"
        }, 16)
    }
}
