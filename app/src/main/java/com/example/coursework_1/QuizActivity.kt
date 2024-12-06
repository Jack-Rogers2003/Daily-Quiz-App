package com.example.coursework_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import com.koushikdutta.ion.Ion
import org.json.JSONObject
import kotlin.random.Random
import android.text.Html
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class QuizActivity: AppCompatActivity() {
    private var url = ""
    private var currentQuestion = 0
    private var savedQuestions: String = ""
    private var correctAnswers = 0
    private var wrongAnswers = 0
    private var quizType: String? = ""
    private var quizTopic: String? = ""
    private var correctAnswerText: String = ""
    private var allAnswers: MutableList<String> = mutableListOf()
    private lateinit var tts: TextToSpeech
    private var questionTTS = ""
    private var userAnswer = ""


    private fun getCategoryNum(topic: String?): Int {
        when (topic) {
            "General Knowledge" -> return 9
            "Entertainment: Books" -> return 10
            "Entertainment: Film" -> return 11
            "Entertainment: Music" -> return 12
            "Entertainment: Musicals and Theatres" -> return 13
            "Entertainment: Television" -> return 14
            "Entertainment: Video Games" -> return 15
            "Entertainment: Board Games" -> return 16
            "Entertainment: Science & Nature" -> return 17
            "Science: Computers" -> return 18
            "Science: Mathematics" -> return 19
            "Mythology" -> return 20
            "Sports" -> return 21
            "Geography" -> return 22
            "History" -> return 23
            "Politics" -> return 24
            "Art" -> return 25
            "Celebrities" -> return 26
            "Animals" -> return 27
            "Vehicles" -> return 28
            "Entertainment Comics" -> return 29
            "Science: Gadgets" -> return 30
            "Entertainment: Japanese Anime & Manga" -> return 31
            "Entertainment: Cartoon & Animation" -> return 32
        }
        return 9
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
            val snapshot = result.result
            quizType = snapshot.child("type").getValue(String::class.java)
            quizTopic = snapshot.child("topic").getValue(String::class.java)
            url = "https://opentdb.com/api.php?amount=" + snapshot.child("numQuestions").getValue(String::class.java) +
                    "&category=" + getCategoryNum(quizTopic)

            if (quizType == "True or False") {
                setContentView(R.layout.quiz_play_true_false_activity)
                val backButton = findViewById<Button>(R.id.trueOrFalseBack)
                backButton.setOnClickListener { backButtonOnClick() }
                val ttsButton = findViewById<ImageView>(R.id.trueOrFalseTTS)
                ttsButton.setOnClickListener { ttsTrueOrFalse() }
                url = "$url&type=boolean"
            } else {
                setContentView(R.layout.quiz_play_multiple_choice_activity)
                val backButton = findViewById<Button>(R.id.quizMultipleChoiceBackButton)
                backButton.setOnClickListener { backButtonOnClick() }
                val ttsButton = findViewById<ImageView>(R.id.multipleChoiceTTS)
                ttsButton.setOnClickListener { ttsMultipleChoice() }
                url = "$url&type=multiple"
            }
            getQuestions()
        }
    }

    private fun ttsMultipleChoice() {
        tts = TextToSpeech(this) { success ->
            if(success == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.UK)
                val text = questionTTS + ".The possible answers from the bottom down are. " +
                        allAnswers[0] + "." + allAnswers[1] + "." + allAnswers[2] + "." +
                        allAnswers[3]
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            }
        }
    }

    private fun ttsTrueOrFalse() {
        tts = TextToSpeech(this) { success ->
            if(success == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.UK)
                val text = "$questionTTS.Left for false and right for true."
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            }
        }
    }

    private fun getQuestions() {
        savedQuestions = Ion.with(this)
            .load("Get", url)
            .asString().get()
        process()
    }

    private fun process() {
        val questions = JSONObject(savedQuestions).getJSONArray("results")
        if (currentQuestion != questions.length()) {
            if (quizType == "True or False") {
                processQuestionTrueOrFalse(questions.getJSONObject(currentQuestion))
            } else {
                processQuestionMultipleChoice(questions.getJSONObject(currentQuestion))
            }
        } else {
            currentQuestion = -1
            buildAlertBox("Finish", "Correct Answers: " + correctAnswers
                    + "\nIncorrect Answers: " + wrongAnswers)
        }
    }

    private fun processQuestionMultipleChoice(json: JSONObject) {
        val answerButton1 = findViewById<Button>(R.id.answerButton1)
        val answerButton2 = findViewById<Button>(R.id.answerButton2)
        val answerButton3 = findViewById<Button>(R.id.answerButton3)
        val answerButton4 = findViewById<Button>(R.id.answerButton4)
        val correctAnswer = json.getString("correct_answer")
        val listOfAnswers = json.getJSONArray("incorrect_answers").put(correctAnswer)

        val questionText = findViewById<TextView>(R.id.quizQuestionMultipleChoice)
        questionTTS = Html.fromHtml(json.getString("question"), Html.FROM_HTML_MODE_LEGACY).toString()
        questionText.text = questionTTS
        val listOfButtons = mutableListOf(answerButton1, answerButton2, answerButton3, answerButton4)
        while (listOfButtons.isNotEmpty()) {
            var answerIndex = 0
            if (listOfAnswers.length() > 1) {
                answerIndex = Random.nextInt(listOfAnswers.length() - 1)
            }
            val randomAnswer = listOfAnswers.getString(answerIndex)
            listOfAnswers.remove(answerIndex)
            val randomButton = listOfButtons[0]
            listOfButtons.remove(randomButton)
            randomButton.text = Html.fromHtml(randomAnswer, Html.FROM_HTML_MODE_LEGACY).toString()
            if (randomAnswer == correctAnswer) {
                correctAnswerText = correctAnswer
                randomButton.setOnClickListener{ view ->
                    val button = view as Button
                    userAnswer = button.text.toString()
                    correctAnswer()}
            } else {
                randomButton.setOnClickListener{ view ->
                    val button = view as Button
                    userAnswer = button.text.toString()
                    wrongAnswer()}
            }
            allAnswers.add(randomAnswer)
        }
    }

    private fun processQuestionTrueOrFalse(json: JSONObject) {
        val trueButton = findViewById<Button>(R.id.trueButton)
        val falseButton = findViewById<Button>(R.id.falseButton)
        val questionText = findViewById<TextView>(R.id.quizQuestionTrueOrFalse)
        questionTTS = Html.fromHtml(json.getString("question"), Html.FROM_HTML_MODE_LEGACY).toString()
        questionText.text = questionTTS
        if (json.getString("correct_answer") == "True") {
            correctAnswerText = "True"
            trueButton.setOnClickListener{
                trueButton.text.toString()
                correctAnswer()}
            falseButton.setOnClickListener{
                falseButton.text.toString()
                wrongAnswer()}
        } else {
            correctAnswerText = "False"
            trueButton.setOnClickListener { view ->
                val button = view as Button
                userAnswer = button.text.toString()
            }
            falseButton.setOnClickListener{ view ->
                val button = view as Button
                userAnswer = button.text.toString()
                correctAnswer()}
        }

    }


    private fun correctAnswer() {
        correctAnswers++
        buildAlertBox("Correct", "You are Correct!")
    }

    private fun wrongAnswer() {
        wrongAnswers++
        buildAlertBox("Incorrect", "You are Wrong!\nCorrect answer is: $correctAnswerText")
    }

    private fun buildAlertBox(title: String, text: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(text)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setOnDismissListener {
            if (currentQuestion != -1) {
                currentQuestion++
                process()
            } else {
                UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
                    val snapshot = result.result
                    val currentNumCorrect =
                        (quizTopic?.let { it1 ->
                            snapshot.child("topicAnswers/$it1/correctAnswers").value as? String
                        })?.toInt()
                    val currentNumWrong =
                        (quizTopic?.let { it1 ->
                            snapshot.child("topicAnswers/$it1/wrongAnswers").value as? String
                        })?.toInt()
                    if (currentNumCorrect != null) {
                        correctAnswers += currentNumCorrect
                    }
                    if (currentNumWrong != null) {
                        wrongAnswers += currentNumWrong
                    }



                    val route = "topicAnswers/$quizTopic"
                    UserInformation.setDatabase("$route/correctAnswers", correctAnswers.toString())
                    UserInformation.setDatabase("$route/wrongAnswers", wrongAnswers.toString())
                    val questionFinal = questionTTS.replace("[.#$\\[\\]]", "").replace(Regex("\\.$"), "")
                    val userAnswerFinal = userAnswer.replace("[.#$\\[\\]]", "").replace(Regex("\\.$"), "")
                    System.out.println(userAnswer)
                    if (quizType == "True or False") {
                        UserInformation.setDatabase("$route/TOF/$questionFinal",
                            "$correctAnswerText;$userAnswerFinal"
                        )
                    } else {
                        val wrongAnswerText: MutableList<String> = mutableListOf()

                        for (answer in allAnswers) {
                            if (answer != correctAnswerText) {
                                wrongAnswerText.add(answer)
                            }
                        }
                        UserInformation.setDatabase("$route/MC/$questionFinal", wrongAnswerText[0] + ";" +
                                wrongAnswerText[1] + ";" + wrongAnswerText[2] + ";" + correctAnswerText + ";" + userAnswerFinal)
                    }
                    UserInformation.setDatabase("playedToday", "true")
                    UserInformation.setDatabase("lastPlayed", LocalDate.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    UserInformation.setDatabase("playedToday", "true")
                    backButtonOnClick()
                }
            }
        }
        builder.create().show()
    }

    private fun backButtonOnClick() {
        startActivity(Intent(this, MainScreen::class.java))
    }
}