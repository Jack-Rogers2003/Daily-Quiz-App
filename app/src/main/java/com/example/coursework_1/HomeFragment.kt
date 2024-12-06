package com.example.coursework_1
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit


class HomeFragment : Fragment() {

    private val handler = Handler()
    private val updateInterval: Long = 6000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)!!
        val btnLogOut = view.findViewById<Button>(R.id.logOutButton)
        btnLogOut.setOnClickListener{logOutButtonClicked()}
        val btnQuickStart = view.findViewById<Button>(R.id.quickStartButton)
        btnQuickStart.setOnClickListener{quickStartButtonClicked()}
        UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
            val username = result.result.child("username").value
            view.findViewById<TextView>(R.id.welcomeTextHomeScreen).text =
            buildString {
                append("Welcome ")
                    append(username)
                    append("!")
                }
            }

        return view
    }

    override fun onStart() {
        super.onStart()
        view?.let {
            updateTime(it)
        }
    }

    private fun updateTime(view: View) {
        UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
            val snapshot = result.result
            val dateCheck = dateCheck(snapshot.child("lastPlayed").getValue(String::class.java))
            val timeCheck = checkTimes(snapshot.child("resetTime").getValue(String::class.java))

            if ((timeCheck && dateCheck) || !snapshot.child("playedToday").getValue(String::class.java).toBoolean()
                || twoOrMoreDays(snapshot.child("lastPlayed").getValue(String::class.java))) {
                UserInformation.setDatabase("playedToday", "false")
                "Ready to play!".also {
                    view.findViewById<TextView>(R.id.timeRemainingHomeScreen).text = it
                }
            } else {
                view.findViewById<TextView>(R.id.timeRemainingHomeScreen).text =
                    buildString {
                        append("You have ")
                        append(timeLeft(snapshot.child("resetTime").getValue(String::class.java), dateCheck, timeCheck))
                    }
            }
            handler.postDelayed({
                updateTime(view)
            }, updateInterval)
        }
    }

    private fun twoOrMoreDays(lastPlayed: String?) : Boolean {
        val currentDate = LocalDate.now()
        val lastPlayedParsed = LocalDate.parse(lastPlayed)
        return ChronoUnit.DAYS.between(lastPlayedParsed, currentDate) > 2
    }

    private fun timeLeft(resetTime: String?, dateCheck: Boolean, timeCheck: Boolean): String {
        if (dateCheck) {
            val duration = Duration.between(LocalTime.now(), LocalTime.parse(resetTime))
            return "" + duration.toHours() + " hours and " + (duration.toMinutes() % 60 + 60) % 60 + " minutes left"
        } else {
            val time: Duration? = if(timeCheck) {
                Duration.between(LocalTime.now(), LocalTime.now().withHour(23).withMinute(59)) +
                        Duration.between(
                            LocalTime.now().withHour(0).withMinute(0),
                            LocalTime.parse(resetTime)
                        )
            } else {
                Duration.between(LocalTime.now(), LocalTime.parse(resetTime))
            }

            return "" + time?.toHours() + " hours and " + (time?.toMinutes()?.rem(60)) + " minutes left"
        }
    }

    private fun checkTimes(resetTime: String?): Boolean {
        val currentTime = LocalTime.now()
        val reset = LocalTime.parse(resetTime)
        return currentTime.isAfter(reset)
    }

    private fun dateCheck(lastDate: String?): Boolean {
        val currentDate = LocalDate.now()
        val lastPlayed = LocalDate.parse(lastDate)
        return currentDate.isAfter(lastPlayed)
    }

    private fun logOutButtonClicked() {
        UserInformation.logOut()
        DailyResetNotification.cancelAlarm(requireContext())
        startActivity(Intent(requireActivity(), LoginOrSignUp::class.java))
    }

    private fun quickStartButtonClicked() {
        UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
            val snapshot = result.result
            if (!snapshot.child("playedToday").getValue(String::class.java).toBoolean()) {
                startActivity(Intent(requireContext(), QuizActivity::class.java))
            }
        }
    }
}