package com.example.gdsc_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [ActiveUsersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActiveUsersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_active_users, container, false)

        val docs = ArrayList<User>()
        val textActiveUsers = view?.findViewById<TextView>(R.id.activeUsersText)
        val textTotalActiveUsers = view?.findViewById<TextView>(R.id.totalActiveUsersText)

        val db = FirebaseFirestore.getInstance()
        db.collection("users").get().addOnSuccessListener { documents ->
            for (doc in documents) {
                docs.add(doc.toObject(User::class.java))
//                Log.d("DOC TAG@@@@@@", doc.id)
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
                                textTmp.append("\tDegree: ${doc.degree}")
                                if (doc.degreeYear != null && doc.degreeYear != "") {
                                    textTmp.append(", Year: ${doc.degreeYear}")
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


//        Log.d("TESSSSSSSST", textTmp.toString())

        return view

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ActiveUsersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ActiveUsersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}