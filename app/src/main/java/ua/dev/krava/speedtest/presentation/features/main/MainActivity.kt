package ua.dev.krava.speedtest.presentation.features.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_main.*
import ua.dev.krava.speedtest.R
import ua.dev.krava.speedtest.domain.preferences.ValueSettingsImpl
import ua.dev.krava.speedtest.presentation.features.fetch.servers.FetchServersFragment
import ua.dev.krava.speedtest.presentation.features.history.HistoryFragment
import ua.dev.krava.speedtest.presentation.features.settings.SettingsFragment
import ua.dev.krava.speedtest.presentation.features.speedtest.SpeedTestFragment

class MainActivity: MvpAppCompatActivity(), MainView {
    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.tab_test -> {
                    showFragment(SpeedTestFragment())
                }
                R.id.tab_history -> {
                    showFragment(HistoryFragment())
                }
                R.id.tab_settings -> {
                    showFragment(SettingsFragment())
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        presenter.onCreate(ValueSettingsImpl(this))
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitNow()
    }


    override fun onStartLoadingServers() {
        showFragment(FetchServersFragment())
    }

    override fun onServersLoaded() {
        showFragment(SpeedTestFragment())
        bottom_navigation.animate().translationY(0.0f)
    }
}
