package ua.dev.krava.speedtest.presentation.features.history

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_history.*
import ua.dev.krava.speedtest.R
import ua.dev.krava.speedtest.presentation.model.TestEntry

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
class HistoryFragment: MvpAppCompatFragment(), HistoryView {
    @InjectPresenter
    lateinit var presenter: HistoryPresenter
    private lateinit var adapter: HistoryAdapter
    private lateinit var listView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        listView = view.findViewById(R.id.historyListView)
        listView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        this.adapter = HistoryAdapter()
        listView.adapter = this.adapter

        this.presenter.loadHistory()

        return view
    }

    override fun onHistoryLoaded(history: List<TestEntry>) {
        this.adapter.setHistory(history)
    }

    override fun onHistoryEmpty() {
        emptyHistoryContainer.visibility = View.VISIBLE
    }

    override fun onLoadingError() {

    }
}