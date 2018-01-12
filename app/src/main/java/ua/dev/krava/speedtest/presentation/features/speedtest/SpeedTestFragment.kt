package ua.dev.krava.speedtest.presentation.features.speedtest

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_test.*
import org.krava.speedometer.SpeedometerView
import ua.dev.krava.speedtest.R

/**
 * Created by evheniikravchyna on 01.01.2018.
 */
class SpeedTestFragment: MvpAppCompatFragment(), TestView {
    @InjectPresenter
    lateinit var presenter: SpeedTestPresenter
    private lateinit var btnStartTest: View
    private lateinit var btnRepeatTest: View
    private lateinit var progressView: SpeedometerView
    private var flickerAnimation: FlickerAnimation? = null


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
            uploadContainer.visibility = View.GONE
            uploadValue.text = "0.0"
            downloadValue.text = "0.0"
            pingValue.text = "--"
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
        flickerAnimation?.cancel()
        flickerAnimation = FlickerAnimation(downloadSpeedTitle)
        downloadContainer.visibility = View.VISIBLE
        flickerAnimation?.start()
    }

    override fun onDownloadComplete() {
        flickerAnimation?.cancel()
        downloadValue.animate().scaleX(1.5f).scaleY(1.5f).setListener(object: Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                downloadValue.animate().scaleX(1.0f).scaleY(1.0f).setListener(null).start()
            }
            override fun onAnimationRepeat(p0: Animator?) { }
            override fun onAnimationStart(p0: Animator?) { }
            override fun onAnimationCancel(p0: Animator?) { }
        }).start()
    }

    override fun onStartUpload() {
        flickerAnimation = FlickerAnimation(uploadSpeedTitle)
        uploadContainer.visibility = View.VISIBLE
        flickerAnimation?.start()
    }

    override fun onUploadUpdate(progress: Float) {
        progressView.postDelayed({
            progressView.setProgress(progress)
            uploadValue.text = "$progress"
        }, 16)
    }

    override fun onUploadComplete() {
        flickerAnimation?.cancel()
        uploadValue.animate().scaleX(1.5f).scaleY(1.5f).setListener(object: Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                uploadValue.animate().scaleX(1.0f).scaleY(1.0f).setListener(null).start()
            }
            override fun onAnimationRepeat(p0: Animator?) { }
            override fun onAnimationStart(p0: Animator?) { }
            override fun onAnimationCancel(p0: Animator?) { }
        }).start()
        btnRepeatTest.visibility = View.VISIBLE
    }


    override fun onDownloadUpdate(progress: Float) {
        progressView.postDelayed({
            progressView.setProgress(progress)
            downloadValue.text = "$progress"
        }, 16)
    }

    override fun onCheckLocation() {
        locationValue.visibility = View.GONE
        hostValue.visibility = View.GONE
        hostProgressIndicator.show()
        locationProgressIndicator.show()
    }

    override fun onLocation(city: String) {
        locationProgressIndicator.hide()
        locationValue.text = city
        locationValue.visibility = View.VISIBLE
    }

    override fun onCheckServer() {

    }

    override fun onServerReady(host: String) {
        hostProgressIndicator.hide()
        hostValue.text = host
        hostValue.visibility = View.VISIBLE
    }

    companion object {
        val TAG = "speed_test"
    }
}
