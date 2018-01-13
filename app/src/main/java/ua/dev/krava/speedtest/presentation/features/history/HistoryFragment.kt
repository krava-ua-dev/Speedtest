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
import ua.dev.krava.speedtest.presentation.features.main.IFastStartTest
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sortByDate.isSelected = true
        sortByDate.setOnClickListener { onTabClick(R.id.sortByDate) }
        sortByDownload.setOnClickListener { onTabClick(R.id.sortByDownload) }
        sortByUpload.setOnClickListener { onTabClick(R.id.sortByUpload) }
        sortByPing.setOnClickListener { onTabClick(R.id.sortByPing) }
    }

    private fun onTabClick(viewId: Int) {
        sortByDate.isSelected = false
        sortByDownload.isSelected = false
        sortByUpload.isSelected = false
        sortByPing.isSelected = false
        sortByDateIcon.visibility = View.INVISIBLE
        sortByDownloadIcon.visibility = View.INVISIBLE
        sortByUploadIcon.visibility = View.INVISIBLE
        sortByPingIcon.visibility = View.INVISIBLE

        var newSort = HistoryAdapter.SortedTab.DATE
        var isDesc = true
        when (viewId) {
            R.id.sortByDate -> {
                sortByDate.isSelected = true
                newSort = HistoryAdapter.SortedTab.DATE
                isDesc = if (adapter.getCurrentSort() == newSort) {
                    !adapter.isDesc()
                } else {
                    true
                }
                sortByDateIcon.setImageResource(if (isDesc) R.drawable.sort_desc else R.drawable.sort_asc)
                sortByDateIcon.visibility = View.VISIBLE
            }
            R.id.sortByDownload -> {
                sortByDownload.isSelected = true
                newSort = HistoryAdapter.SortedTab.DOWNLOAD
                isDesc = if (adapter.getCurrentSort() == newSort) {
                    !adapter.isDesc()
                } else {
                    true
                }
                sortByDownloadIcon.setImageResource(if (isDesc) R.drawable.sort_desc else R.drawable.sort_asc)
                sortByDownloadIcon.visibility = View.VISIBLE
            }
            R.id.sortByUpload -> {
                sortByUpload.isSelected = true
                newSort = HistoryAdapter.SortedTab.UPLOAD
                isDesc = if (adapter.getCurrentSort() == newSort) {
                    !adapter.isDesc()
                } else {
                    true
                }
                sortByUploadIcon.setImageResource(if (isDesc) R.drawable.sort_desc else R.drawable.sort_asc)
                sortByUploadIcon.visibility = View.VISIBLE
            }
            R.id.sortByPing -> {
                sortByPing.isSelected = true
                newSort = HistoryAdapter.SortedTab.PING
                isDesc = if (adapter.getCurrentSort() == newSort) {
                    !adapter.isDesc()
                } else {
                    true
                }
                sortByPingIcon.setImageResource(if (isDesc) R.drawable.sort_desc else R.drawable.sort_asc)
                sortByPingIcon.visibility = View.VISIBLE
            }
        }
        adapter.sort(newSort, isDesc)
    }

    override fun onHistoryLoaded(history: List<TestEntry>) {
        this.adapter.setHistory(history)
    }

    override fun onHistoryEmpty() {
        emptyHistoryContainer.visibility = View.VISIBLE
        btnStartTest.setOnClickListener {
            if (activity is IFastStartTest) {
                (activity as IFastStartTest).onFastStart()
            }
        }
    }

    override fun onLoadingError() {

    }

    companion object {
        val TAG = "history"
    }
}