package com.example.journal

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(
    private var groupedSchedules: List<Pair<String, List<ScheduleWithDisciplineName>>>
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cardview, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val (date, schedules) = groupedSchedules[position]
        holder.bind(date, schedules)
    }

    override fun getItemCount(): Int = groupedSchedules.size

    fun updateList(newGroupedSchedules: List<Pair<String, List<ScheduleWithDisciplineName>>>) {
        groupedSchedules = newGroupedSchedules
        notifyDataSetChanged()
    }

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)

        fun bind(date: String, schedules: List<ScheduleWithDisciplineName>) {
            textViewDate.text = date
            val adapter = ItemScheduleAdapter(schedules)
            recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter

            adapter.setOnItemClickListener(object : ItemScheduleAdapter.OnItemClickListener {
                override fun onDeleteClick(position: Int) {
                    val scheduleToDelete = adapter.getItem(position).schedule
                    // Удаление элемента из базы данных
                    Thread {
                        MainDb.getDb(itemView.context).getDao().deleteSchedule(scheduleToDelete)
                        // Удаление элемента из адаптера
                        (itemView.context as AppCompatActivity).runOnUiThread {
                            adapter.deleteSchedule(position)
                        }
                    }.start()
                }
            })

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, attendanceActivity::class.java).apply {
                    putExtra("DATE", date)
                }
                context.startActivity(intent)
            }
        }
    }
}
