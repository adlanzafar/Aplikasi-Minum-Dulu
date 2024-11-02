package com.example.minumdulu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView as AndroidRecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView as AdapterRecyclerView

class ActivityListActivity : AppCompatActivity() {

    private lateinit var recyclerViewActivities: AndroidRecyclerView
    private lateinit var activityListAdapter: ActivityListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity_list)

        recyclerViewActivities = findViewById(R.id.recyclerViewActivities)
        recyclerViewActivities.layoutManager = LinearLayoutManager(this)

        val activities = getSortedActivitiesFromSharedPreferences()

        activityListAdapter = ActivityListAdapter(activities)
        recyclerViewActivities.adapter = activityListAdapter
    }

    private fun getSortedActivitiesFromSharedPreferences(): List<String> {
        val sharedPreferences: SharedPreferences = getSharedPreferences(
            MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE
        )

        val activitySet = sharedPreferences.getStringSet(
            MainActivity.KEY_ACTIVITY_LIST, emptySet()
        ) ?: emptySet()

        return activitySet.sortedBy {
            it.substringBefore(":").toInt()
        }.toList()
    }

    class ActivityListAdapter(private val activityList: List<String>) :
        AdapterRecyclerView.Adapter<ActivityListAdapter.ActivityViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_activity, parent, false)
            return ActivityViewHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
            val activity = activityList[position]
            holder.bind(activity)
        }

        override fun getItemCount(): Int {
            return activityList.size
        }

        class ActivityViewHolder(itemView: View) : AdapterRecyclerView.ViewHolder(itemView) {
            private val textViewActivityItem: TextView = itemView.findViewById(R.id.textViewActivityItem)

            fun bind(activity: String) {
                val formattedActivity = "$activity\n-------------------------------------"
                textViewActivityItem.text = formattedActivity
            }
        }
    }
}
