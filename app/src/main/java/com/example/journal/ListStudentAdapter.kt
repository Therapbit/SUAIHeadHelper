package com.example.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ListStudentAdapter(private var students: List<Student>) : RecyclerView.Adapter<ListStudentAdapter.StudentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_no_button, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: com.example.journal.ListStudentAdapter.StudentViewHolder, position: Int) {
        val student = students[position]
        holder.bind(position + 1, student)


    }

    override fun getItemCount(): Int = students.size

    fun updateList(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewIndex: TextView = itemView.findViewById(R.id.textViewIndex)
        private val textViewStudent: TextView = itemView.findViewById(R.id.textViewStudent)

        fun bind(index: Int, student: Student) {
            textViewIndex.text = index.toString()
            textViewStudent.text = ". ${student.SURNAME} ${student.NAME} ${student.PATRONYMIC}"
        }
    }

}