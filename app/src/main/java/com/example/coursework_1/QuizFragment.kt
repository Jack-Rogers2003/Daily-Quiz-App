package com.example.coursework_1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class QuizFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)

        UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
            val snapshot = result.result

            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val adapter = TopicAdapter(
                resources.getStringArray(R.array.topics).toList(),
                snapshot.child("topic").getValue(String::class.java), true
            )
            recyclerView.adapter = adapter

            val numOfQuestionsSpinner =
                view.findViewById<Spinner>(R.id.selectNumOfQuestionsQuizFragment)
            val typeOfQuestionsSpinner = view.findViewById<Spinner>(R.id.selectQuestionType)


            val numAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item,
                (1..15).toList()
            )
            numOfQuestionsSpinner.adapter = numAdapter

            val typeAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.types_of_questions)
            )
            typeOfQuestionsSpinner.adapter = typeAdapter
            numOfQuestionsSpinner.setSelection(
                resources.getStringArray(R.array.topics).toList()
                    .indexOf(snapshot.child("numQuestions").getValue(String::class.java))
            )

            val btnStartQuiz = view.findViewById<Button>(R.id.startQuizButton)
            btnStartQuiz.setOnClickListener { startQuiz(view) }
        }
        return view
    }


    private fun startQuiz(view: View) {
        UserInformation.getFromDatabase()?.addOnCompleteListener { task ->
            if (!task.result.child("playedToday").getValue(String::class.java).toBoolean()) {
                UserInformation.setDatabase(
                    "type",
                    view.findViewById<Spinner>(R.id.selectQuestionType).selectedItem.toString()
                )
                UserInformation.setDatabase(
                    "numQuestions",
                    view.findViewById<Spinner>(R.id.selectNumOfQuestionsQuizFragment).selectedItem.toString()
                )
                val dateNow = LocalDate.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
                UserInformation.setDatabase("lastPlayed", dateNow)
                startActivity(Intent(requireContext(), QuizActivity::class.java))
            }
        }

    }
}
