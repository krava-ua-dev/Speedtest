package ua.dev.krava.speedtest.presentation.features.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_main.*
import ua.dev.krava.speedtest.R
import ua.dev.krava.speedtest.domain.preferences.ValueSettingsImpl
import ua.dev.krava.speedtest.presentation.features.fetch.servers.FetchServersFragment
import ua.dev.krava.speedtest.presentation.features.fetch.servers.IReloadingServers
import ua.dev.krava.speedtest.presentation.features.history.HistoryFragment
import ua.dev.krava.speedtest.presentation.features.speedtest.SpeedTestFragment

class MainActivity: MvpAppCompatActivity(), MainView, IReloadingServers, IFastStartTest, BottomNavigationView.OnNavigationItemSelectedListener {
    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener(this)
        presenter.onCreate(ValueSettingsImpl(this))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.tab_test -> {
                showFragment(SpeedTestFragment.instance(), SpeedTestFragment.TAG)
            }
            R.id.tab_history -> {
                showFragment(HistoryFragment(), HistoryFragment.TAG)
            }
        }
        return true
    }

    private fun showFragment(fragment: Fragment, tag:String, force: Boolean = false) {
        if (supportFragmentManager.findFragmentByTag(tag) == null || force) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .commitNow()
        }
    }

    override fun onReloadServers() {
        this.presenter.loadServers()
    }


    override fun onServersLoadingError() {
        showFragment(FetchServersFragment.getInstance(true), FetchServersFragment.TAG, true)
    }


    override fun onStartLoadingServers() {
        showFragment(FetchServersFragment.getInstance(false), FetchServersFragment.TAG, true)
    }

    override fun onServersLoaded(autoStart: Boolean) {
        showFragment(SpeedTestFragment.instance(autoStart), SpeedTestFragment.TAG)
        bottom_navigation.animate().translationY(0.0f)
    }

    override fun onFastStart() {
        bottom_navigation.setOnNavigationItemSelectedListener(null)
        bottom_navigation.selectedItemId = R.id.tab_test
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        presenter.onFastStartSpeedtest()
    }
}
