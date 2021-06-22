package com.yatharth.covid19tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var stateAdapter: StateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.addHeaderView(LayoutInflater.from(this).inflate(R.layout.item_header, list, false))
        fetchResults()
    }

    private fun fetchResults() {
        GlobalScope.launch {
            val response = withContext(Dispatchers.IO){ Client.api.clone().execute() }
            if(response.isSuccessful){
                val data = Gson().fromJson(response.body?.string(), Response::class.java)
                launch(Dispatchers.Main) {
                    bindCombinedData(data.statewise[0])
                    bindStatewiseData(data.statewise.subList(0, data.statewise.size))
                }
            }
        }
    }

    private fun bindStatewiseData(subList: List<StatewiseItem>) {
        stateAdapter = StateAdapter(subList)
        list.adapter = stateAdapter
    }

    private fun bindCombinedData(statewiseItem: StatewiseItem) {
        val lastUpdated = statewiseItem.lastupdatedtime!!
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        lastUpdatedTv.text = "Last Updated\n${getTimeAgo(sdf.parse(lastUpdated)!!)}"

        confirmedTv.text = statewiseItem.confirmed
        recoveredTv.text = statewiseItem.recovered
        activeTv.text = statewiseItem.active
        deceasedTv.text = statewiseItem.deaths
    }

    private fun getTimeAgo(past: Date): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)

        return when {
            seconds < 60 -> {
                "Few Seconds ago"
            }
            minutes < 60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hours ${minutes % 60} minutes ago"
            }
            else -> {
                SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(past)
            }
        }
    }
}