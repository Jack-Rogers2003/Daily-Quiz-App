package com.example.coursework_1

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class AchievementsActivity: AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievements_layout)
        val layout = findViewById<LinearLayout>(R.id.achievementsLinearLayout)
        var dbHelper = DBHelper(this)

        if(dbHelper.isTableEmpty()) {
            for (topic in resources.getStringArray(R.array.types_of_questions)) {
                dbHelper.addNewTopic(topic)
            }
        }
    }
}