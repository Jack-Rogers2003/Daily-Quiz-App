package com.example.coursework_1

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry


class ProfileFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {

            val view = inflater.inflate(R.layout.fragment_profile, container, false)

            val topicSpinner = view.findViewById<Spinner>(R.id.userProfileSelectTopic)
            val listForSpinner = resources.getStringArray(R.array.topics).toMutableList()
            listForSpinner.add(0, "Overall")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,
                listForSpinner)
            topicSpinner.adapter = adapter

            val refreshButton = view.findViewById<ImageButton>(R.id.refreshButtonProfileFragment)
            refreshButton.setOnClickListener{refreshButtonClicked(view)}
            refreshButtonClicked(view)

            val previousQuestionsButton = view.findViewById<Button>(R.id.previousAnswersButton)
            previousQuestionsButton.setOnClickListener{previousQuestionsButtonClicked()}

            return view
        }

    private fun setPieChart(view: View) {
        val currentTopic = requireContext().getSharedPreferences("topicSelection", MODE_PRIVATE)
            .getString("topic", "General Knowledge")

        UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
            val snapshot = result.result
            if (currentTopic != "Overall") {
                val correctAnswersNum = snapshot.child("topicAnswers/$currentTopic/correctAnswers")
                    .getValue(String::class.java)?.toInt()
                if (correctAnswersNum != null) {
                    val wrongAnswersNum = snapshot.child("topicAnswers/$currentTopic/wrongAnswers")
                        .getValue(String::class.java)!!.toInt()
                    val rightProportion =
                        correctAnswersNum.toFloat() / (correctAnswersNum + wrongAnswersNum).toFloat()
                    val wrongProportion =
                        wrongAnswersNum.toFloat() / (correctAnswersNum + wrongAnswersNum).toFloat()

                    createPieChart(view, rightProportion, wrongProportion)
                }
            } else {
                val topicSnapshot = snapshot.child("topicAnswers")
                var correctAnswersTotal = 0
                var wrongAnswerTotal = 0
                for (topic in topicSnapshot.children) {
                    val correctNum = (topic.child("correctAnswers").getValue(String::class.java))?.toInt()
                    if (correctNum != null) {
                        correctAnswersTotal += correctNum
                    }
                    val wrongNum = (topic.child("wrongAnswers").getValue(String::class.java))?.toInt()
                    if (wrongNum != null) {
                        wrongAnswerTotal += wrongNum
                    }
                }
                val rightProportion =
                    correctAnswersTotal.toFloat() / (correctAnswersTotal + wrongAnswerTotal).toFloat()
                val wrongProportion =
                    wrongAnswerTotal.toFloat() / (correctAnswersTotal + wrongAnswerTotal).toFloat()

                createPieChart(view, rightProportion, wrongProportion)
            }
        }
    }

    private fun createPieChart(view: View, correctNum: Float, wrongNum: Float) {
        val pieChart = view.findViewById<PieChart>(R.id.pieChart)

        val pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(correctNum, "Correct"))
        pieEntries.add(PieEntry(wrongNum, "Incorrect"))

        val dataSet = PieDataSet(pieEntries, "Categories")
        dataSet.colors = listOf(
            resources.getColor(R.color.navy_blue),
            resources.getColor(R.color.black)
        )

        val data = PieData(dataSet)
        pieChart.setDrawEntryLabels(false)
        pieChart.setUsePercentValues(true)
        dataSet.sliceSpace = 0f
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 12f
        pieChart.description.isEnabled = false
        pieChart.data = data
        pieChart.invalidate()
    }

    private fun refreshButtonClicked(view: View) {
        val topicSpinner = view.findViewById<Spinner>(R.id.userProfileSelectTopic)
        val sharedPreferences = requireContext().getSharedPreferences("topicSelection", MODE_PRIVATE)
        sharedPreferences.edit().putString("topic", topicSpinner.selectedItem.toString()).apply()

        setTFMCRatio(view)
        setPieChart(view)
    }

    private fun setTFMCRatio(view: View) {
        val topic: String? = requireContext().getSharedPreferences("topicSelection", MODE_PRIVATE)
            .getString("topic", "Overall")
        view.findViewById<TextView>(R.id.whatTopicCount).text = buildString {
            append(topic)
            append(" True or False and Multiple Choice Play Count")
        }
        val mcText = view.findViewById<TextView>(R.id.multipleChoiceCount)
        val tofText = view.findViewById<TextView>(R.id.trueOrFalsePercentage)
        UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
            var mcCount: Long = 0
            var tofCount: Long = 0

            if (topic != "Overall") {
                mcCount = topic?.let {
                    result.result.child("topicAnswers/$it").child("MC").childrenCount }!!
                tofCount = topic.let {
                    result.result.child("topicAnswers/$it").child("TOF").childrenCount
                }
            } else {
                for (currentChild in result.result.child("topicAnswers/").children) {
                    System.out.println("I am here")
                    mcCount += currentChild.child("MC").childrenCount
                    tofCount += currentChild.child("TOF").childrenCount
                }
            }
            mcText.text = buildString {
                append("Multiple Choice Plays:")
                append(mcCount)
            }
            tofText.text = buildString {
                append("True or False Play Count:")
                append(tofCount)
            }
        }
    }

    private fun previousQuestionsButtonClicked() {
        startActivity(Intent(requireContext(), PreviousQuestionsActivity::class.java))    }
}