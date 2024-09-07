package com.example.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout

class ItemScheduleAdapter(private var schedules: List<ScheduleWithDisciplineName>) : RecyclerView.Adapter<ItemScheduleAdapter.ScheduleViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onDeleteClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val scheduleWithDisciplineName = schedules[position]
        holder.bind(position + 1, scheduleWithDisciplineName)

        // Обработчик клика на кнопку удаления
        holder.itemView.findViewById<View>(R.id.deleteItemSchedule).setOnClickListener {
            listener.onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = schedules.size

    fun updateList(newSchedules: List<ScheduleWithDisciplineName>) {
        schedules = newSchedules
        notifyDataSetChanged()
    }

    fun deleteSchedule(position: Int) {
        schedules = schedules.toMutableList().apply { removeAt(position) }
        notifyItemRemoved(position)
    }

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewIndex: TextView = itemView.findViewById(R.id.textViewIndex)
        private val textViewDiscipline: TextView = itemView.findViewById(R.id.textViewDiscipline)
        private val textViewType: TextView = itemView.findViewById(R.id.textViewType)

        fun bind(index: Int, scheduleWithDisciplineName: ScheduleWithDisciplineName) {
            textViewIndex.text = "${scheduleWithDisciplineName.schedule.TIME_PAIR}. "
            textViewDiscipline.text = scheduleWithDisciplineName.disciplineName
            textViewType.text = scheduleWithDisciplineName.schedule.TYPE
        }
    }

    fun getItem(position: Int): ScheduleWithDisciplineName {
        return schedules[position]
    }
}
