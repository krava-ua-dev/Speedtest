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
    private var isError: Boolean = false
    private var reloadInterface: IReloadingServers? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (activity is IReloadingServers) {
            reloadInterface = activity as IReloadingServers
        }
        arguments?.let {
            isError = it.getBoolean("is_error", false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_load_servers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isError) {
            loadingContainer.visibility = View.VISIBLE
            loadingServersIndicator.smoothToShow()
        } else {
            errorContainer.visibility = View.VISIBLE
            btnTryAgain.setOnClickListener {
                reloadInterface?.onReloadServers()
            }
        }
    }

    override fun onDestroyView() {
        loadingServersIndicator.hide()
        super.onDestroyView()
    }


    companion object {
        val TAG = "fetch_servers"

        fun getInstance(isError: Boolean): FetchServersFragment {
            val args = Bundle()
            args.putBoolean("is_error", isError)
            val fragment = FetchServersFragment()
            fragment.arguments = args

            return fragment
        }
    }
}