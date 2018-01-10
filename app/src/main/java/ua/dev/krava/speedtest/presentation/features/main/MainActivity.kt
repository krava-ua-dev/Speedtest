package ua.dev.krava.speedtest.presentation.features.main

import android.os.Bundle
import android.support.v4.app.Fragment
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_main.*
import ua.dev.krava.speedtest.R
import ua.dev.krava.speedtest.domain.preferences.ValueSettingsImpl
import ua.dev.krava.speedtest.presentation.features.fetch.servers.FetchServersFragment
import ua.dev.krava.speedtest.presentation.features.fetch.servers.IReloadingServers
import ua.dev.krava.speedtest.presentation.features.history.HistoryFragment
import ua.dev.krava.speedtest.presentation.features.settings.SettingsFragment
import ua.dev.krava.speedtest.presentation.features.speedtest.SpeedTestFragment

class MainActivity: MvpAppCompatActivity(), MainView, IReloadingServers {
    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.tab_test -> {
                    showFragment(SpeedTestFragment(), SpeedTestFragment.TAG)
                }
                R.id.tab_history -> {
                    showFragment(HistoryFragment(), HistoryFragment.TAG)
                }
                R.id.tab_settings -> {
                    showFragment(SettingsFragment(), SettingsFragment.TAG)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        presenter.onCreate(ValueSettingsImpl(this))
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
        showFragment(FetchServersFragment.getInstance(false), FetchServersFragment.TAG, true)
    }


    override fun onStartLoadingServers() {
        showFragment(FetchServersFragment.getInstance(false), FetchServersFragment.TAG, true)
    }

    override fun onServersLoaded() {
        showFragment(SpeedTestFragment(), SpeedTestFragment.TAG)
        bottom_navigation.animate().translationY(0.0f)
    }
}
