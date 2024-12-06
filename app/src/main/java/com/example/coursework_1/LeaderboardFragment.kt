package com.example.coursework_1


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LeaderboardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)!!

        val topicSpinner = view.findViewById<Spinner>(R.id.leaderBoardTopicSpinner)
        val listForSpinner = resources.getStringArray(R.array.topics).toMutableList()
        listForSpinner.add(0, "Overall")
        val topicAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, listForSpinner)
        topicSpinner.adapter = topicAdapter

        val questionTypeSpinner = view.findViewById<Spinner>(R.id.leaderboardQuestionTypeSpinner)
        val listForTypeSpinner = resources.getStringArray(R.array.types_of_questions).toMutableList()
        listForTypeSpinner.add(0, "All")
        val typeAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, listForTypeSpinner)
        questionTypeSpinner.adapter = typeAdapter

        getUsers(view)

        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.refreshLeaderboard)
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.setOnRefreshListener {
            // Call your refresh function here
            refreshData(view)
        }

        return view
    }

    private fun getUsers(view: View) {
        //Get whole database and iterate through it
        val database = UserInformation.getWholeDatabase()
        var usersAndScore: MutableList<String> = mutableListOf()
        val topic = view.findViewById<Spinner>(R.id.leaderBoardTopicSpinner)?.selectedItem
        val type = view.findViewById<Spinner>(R.id.leaderboardQuestionTypeSpinner)?.selectedItem

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (currentUser in snapshot.children) {
                        val username = currentUser.child("username").value
                        var score = 0
                        if (type != "All") {
                            if (type == "True or False") {
                                if (topic != "Overall") {
                                    for (question in currentUser.child("topicAnswers/$topic/TOF").children) {
                                        val value = question.value.toString().split(";")
                                        if (value[0] == value[1]) {
                                            score += 1
                                        }
                                    }
                                    usersAndScore.add("$username,$score")
                                } else {
                                    for (question in currentUser.child("topicAnswers").children) {
                                        for (tof in question.child("TOF").children) {
                                            val value = tof.value.toString().split(";")
                                            if (value[0] == value[1]) {
                                                score += 1
                                            }
                                        }
                                    }
                                    usersAndScore.add("$username,$score")
                                }
                            } else {
                                if (topic != "Overall") {
                                    for (question in currentUser.child("topicAnswers/$topic/MC").children) {
                                        val value = question.value.toString().split(";")
                                        if (value[3] == value[4]) {
                                            score += 1
                                        }
                                    }
                                    usersAndScore.add("$username,$score")
                                } else {
                                    for (question in currentUser.child("topicAnswers").children) {
                                        for (tof in question.child("MC").children) {
                                            val value = tof.value.toString().split(";")
                                            if (value[3] == value[4]) {
                                                score += 1
                                            }
                                        }
                                    }
                                    for (question in currentUser.child("topicAnswers/$topic/TOF").children) {
                                        val value = question.value.toString().split(";")
                                        if (value[0] == value[1]) {
                                            score += 1
                                        }
                                    }
                                    usersAndScore.add("$username,$score")
                                }
                            }
                        } else {
                            if (topic == "Overall") {
                                for (question in currentUser.child("topicAnswers").children) {
                                    for (tof in question.child("MC").children) {
                                        val value = tof.value.toString().split(";")
                                        if (value[3] == value[4]) {
                                            score += 1
                                        }
                                    }
                                    for (tof in question.child("TOF").children) {
                                        val value = tof.value.toString().split(";")
                                        if (value[0] == value[1]) {
                                            score += 1
                                        }
                                    }
                                }
                                usersAndScore.add("$username,$score")
                            } else {
                                for (question in currentUser.child("topicAnswers/$topic/MC").children) {
                                    val value = question.value.toString().split(";")
                                    if (value[3] == value[4]) {
                                        score += 1
                                    }
                                }
                                usersAndScore.add("$username,$score")
                            }
                        }
                    }
                }
                usersAndScore = usersAndScore.sortedByDescending {
                    it.split(",")[1].toInt()
                }.toMutableList()
                var rank = 1
                val addToRecyclerView: MutableList<String> = mutableListOf()
                for (entry in usersAndScore) {
                    val user = entry.split(",")[0]
                    val score = entry.split(",")[1]
                    addToRecyclerView.add("Rank: #" + rank +
                    "\nUser: " + user + "      Score: " + score)
                    rank++
                }
                val recyclerView = view.findViewById<RecyclerView>(R.id.leaderboardRecyclerView)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                val adapter = TopicAdapter(addToRecyclerView, null, false)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun refreshData(view: View) {
        val refresh = view.findViewById<SwipeRefreshLayout >(R.id.refreshLeaderboard)
        refresh.isRefreshing = true
        getUsers(view)
        refresh.isRefreshing = false

    }
}