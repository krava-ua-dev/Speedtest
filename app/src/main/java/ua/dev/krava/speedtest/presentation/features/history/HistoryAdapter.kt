package ua.dev.krava.speedtest.presentation.features.history

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ua.dev.krava.speedtest.R
import ua.dev.krava.speedtest.presentation.model.TestEntry
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by evheniikravchyna on 04.01.2018.
 */
class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private val items = ArrayList<TestEntry>()
    private var sortBy = SortedTab.DATE
    private var isDesc = true

    fun setHistory(items: List<TestEntry>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun sort(by: SortedTab, isDesc: Boolean) {
        this.sortBy = by
        this.isDesc = isDesc
        when(sortBy) {
            SortedTab.DATE -> if (isDesc) {
                items.sortByDescending { it.date }
            } else {
                items.sortBy { it.date }
            }
            SortedTab.DOWNLOAD -> if (isDesc) {
                items.sortByDescending { it.downloadSpeed }
            } else {
                items.sortBy { it.downloadSpeed }
            }
            SortedTab.UPLOAD -> if (isDesc) {
                items.sortByDescending { it.uploadSpeed }
            } else {
                items.sortBy { it.uploadSpeed }
            }
            SortedTab.PING -> if (isDesc) {
                items.sortByDescending { it.ping }
            } else {
                items.sortBy { it.ping }
            }
        }
        notifyDataSetChanged()
    }

    fun getCurrentSort() = sortBy

    fun isDesc() = isDesc

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.history_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = this.items.size



    class HistoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val dateView = view.findViewById<TextView>(R.id.test_date)
        private val downloadSpeedView = view.findViewById<TextView>(R.id.test_download_speed)
        private val uploadSpeedView = view.findViewById<TextView>(R.id.test_upload_speed)
        private val pingView = view.findViewById<TextView>(R.id.test_ping)

        fun bind(test: TestEntry) {
            dateView.text = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(Date(test.date))
            pingView.text = test.ping.toString()
            downloadSpeedView.text = String.format("%.02f", test.downloadSpeed).replace(',', '.')
            uploadSpeedView.text = String.format("%.02f", test.uploadSpeed).replace(',', '.')

            if (adapterPosition % 2 == 0) {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            } else {
                itemView.setBackgroundColor(Color.argb(21, 0, 0, 0))
            }
        }
    }


    enum class SortedTab {
        DATE,
        DOWNLOAD,
        UPLOAD,
        PING;
    }
}