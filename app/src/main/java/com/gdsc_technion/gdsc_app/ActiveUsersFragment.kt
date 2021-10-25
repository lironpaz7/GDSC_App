package com.gdsc_technion.gdsc_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [ActiveUsersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActiveUsersFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val docs = ArrayList<User>()
        val textActiveUsers = view.findViewById<TextView>(R.id.activeUsersText)
        val textTotalActiveUsers = view.findViewById<TextView>(R.id.totalActiveUsersText)

        val db = FirebaseFirestore.getInstance()
        db.collection("users").get().addOnSuccessListener { documents ->
            for (doc in documents) {
                docs.add(doc.toObject(User::class.java))
            }
            val textTmp = StringBuilder()
            var counter = 0

            for (doc in docs) {
                if (doc.shareMyInfo != null && doc.shareMyInfo == "yes") {
                    if (doc.name != null) {
                        counter++
                        textTmp.append("#$counter:\n")
                        textTmp.append("\tName: ${doc.name}\n")
                        if (doc.age != null) {
                            textTmp.append("\tAge: ${doc.age}\n")
                            if (doc.degree != null && doc.degree != "") {
                                textTmp.append("\tDegree: ${doc.degree}\n")
                                if (doc.degreeYear != null && doc.degreeYear != "") {
                                    textTmp.append("\tYear: ${doc.degreeYear}")
                                }
                                textTmp.append("\n")
                            }
                            if (doc.interests != null) {
                                textTmp.append("\tInterests: ${doc.interests}\n\n")
                            }
                        }
                    }
                }

            }
            val total = "Total active users: $counter"
            textTotalActiveUsers?.text = total
            textActiveUsers?.text = textTmp.toString()
        }
    }
}