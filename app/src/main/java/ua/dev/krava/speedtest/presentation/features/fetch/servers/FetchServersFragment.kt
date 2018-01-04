package ua.dev.krava.speedtest.presentation.features.fetch.servers

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_load_servers.*
import ua.dev.krava.speedtest.R

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
class FetchServersFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_load_servers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingServersIndicator.smoothToShow()
    }

    override fun onDestroyView() {
        loadingServersIndicator.hide()
        super.onDestroyView()
    }
}