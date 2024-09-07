package com.example.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DisciplineAdapter(private var disciplines: List<Discipline>) : RecyclerView.Adapter<DisciplineAdapter.DisciplineViewHolder>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onDeleteClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisciplineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_discipline, parent, false)
        return DisciplineViewHolder(view)
    }

    override fun onBindViewHolder(holder: DisciplineViewHolder, position: Int) {
        val discipline = disciplines[position]
        holder.bind(position + 1, discipline)

        // Обработчик клика на кнопку удаления
        holder.itemView.findViewById<View>(R.id.deleteItemDiscipline).setOnClickListener {
            listener.onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = disciplines.size

    fun updateList(newDisciplines: List<Discipline>) {
        disciplines = newDisciplines
        notifyDataSetChanged()
    }

    fun deleteDiscipline(position: Int) {
        disciplines = disciplines.filterIndexed { index, _ -> index != position }
        notifyDataSetChanged()
    }

    class DisciplineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewIndex: TextView = itemView.findViewById(R.id.textViewIndex)
        private val textViewDiscipline: TextView = itemView.findViewById(R.id.textViewDiscipline)

        fun bind(index: Int, discipline: Discipline) {
            textViewIndex.text = index.toString()
            textViewDiscipline.text = ". ${discipline.NAME}"
        }
    }

    fun getItem(position: Int): Discipline {
        return disciplines[position]
    }

}