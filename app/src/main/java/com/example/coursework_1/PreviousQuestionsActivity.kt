package com.example.coursework_1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreviousQuestionsActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.previous_questions_layout)

        val topicSpinner = findViewById<Spinner>(R.id.previousQuestionsTopic)
        val listForSpinner = resources.getStringArray(R.array.topics).toMutableList()
        listForSpinner.add(0, "Overall")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listForSpinner)
        topicSpinner.adapter = adapter

        val typeOfQuestionsSpinner = findViewById<Spinner>(R.id.previousQuestionsType)
        val typeAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.types_of_questions)
        )
        typeOfQuestionsSpinner.adapter = typeAdapter

        val exitButton = findViewById<Button>(R.id.exitPreviousQuestionsButton)
        exitButton.setOnClickListener{exitButtonClicked()}

        val searchButton = findViewById<Button>(R.id.searchForPreviousQuestionsButton)
        searchButton.setOnClickListener{searchButtonClicked()}

        val type = typeOfQuestionsSpinner.selectedItem.toString()

        if (type == "True or False") {
            getTOFQuestions(topicSpinner.selectedItem.toString())
        } else {
            getMultipleChoiceQuestions(topicSpinner.selectedItem.toString())
        }
    }

    private fun exitButtonClicked() {
        startActivity(Intent(this, MainScreen::class.java))
    }

    private fun getTOFQuestions(topic: String) {
        UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
            val toAdd: MutableList<String> = mutableListOf()
            val snapshot = result.result
            if (topic != "Overall") {
                for (child in snapshot.child("topicAnswers/$topic/TOF").children) {
                    val value = child.value.toString().split(";")
                    toAdd.add("Question: " + child.key + "\nAnswer: " + value[0]
                    + "\nYou Answered: " + value[1])
                }
            } else {
                for (topic in snapshot.child("topicAnswers").children) {
                    toAdd.add(topic.key.toString() + "\n")
                    for (answers in topic.child("TOF").children) {
                        val value = answers.value.toString().split(";")
                        toAdd.add("Question: " + answers.key + "\nAnswer: " + value[0]
                                + "\nYou Answered: " + value[1])
                    }
                }
            }
            val recyclerView = findViewById<RecyclerView>(R.id.previousQuestionsRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            val adapter = TopicAdapter(toAdd, null, false)
            recyclerView.adapter = adapter
        }
    }

    private fun getMultipleChoiceQuestions(topic: String) {
        UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
            val toAdd: MutableList<String> = mutableListOf()
            val snapshot = result.result
            if (topic != "Overall") {
                for (child in snapshot.child("topicAnswers/$topic/MC").children) {
                    val value = child.value.toString().split(";")
                    if (value.size == 5) {
                        toAdd.add(
                            "Question: " + child.key + "\nAnswer: " + value[3]
                                    + "\nOther Possible Answers: " + "\n" + value[0] + "\n" + value[1] + "\n" + value[2]
                                    + "\nYour Answer: " + value[4]
                        )
                    }
                }
            } else {
                for (topic in snapshot.child("topicAnswers").children) {
                    toAdd.add(topic.key.toString() + "\n")
                    for (answers in topic.child("MC").children) {
                        val value = answers.value.toString().split(";")
                        if (value.size == 5) {
                            toAdd.add(
                                "Question: " + answers.key + "\nAnswer: " + value[0]
                                        + "\nYou Answered: " + value[1]
                            )
                        }
                    }
                }
            }
            val recyclerView = findViewById<RecyclerView>(R.id.previousQuestionsRecyclerView)
            val adapter = TopicAdapter(toAdd, null, false)
            recyclerView.adapter = adapter
        }
    }

    private fun searchButtonClicked() {
        val topic = findViewById<Spinner>(R.id.previousQuestionsTopic).selectedItem.toString()
        val type = findViewById<Spinner>(R.id.previousQuestionsType).selectedItem.toString()
        if (type == "True or False") {
            getTOFQuestions(topic)
        } else {
            getMultipleChoiceQuestions(topic)
        }
    }
}