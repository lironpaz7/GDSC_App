package com.gdsc_technion.gdsc_app

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [IndexFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IndexFragment : Fragment() {

    private lateinit var displayUser: String
    lateinit var fAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_index, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // buttons

        val profileButton = view.findViewById<ImageView>(R.id.index_profileButton)
        val infoButton = view.findViewById<ImageButton>(R.id.infoButton)
        val eventsButton = view.findViewById<ImageButton>(R.id.eventsButton)
        val solutionChallengeButton = view.findViewById<ImageView>(R.id.solution_challenge_button)

        // extract user name to display on screen
        fAuth = FirebaseAuth.getInstance()
        var userName = fAuth.currentUser?.email.toString()
        val storageReference = FirebaseStorage.getInstance().reference
        userName = userName.substring(0, userName.indexOf("@"))
        displayUser = "Welcome $userName"
        view.findViewById<TextView>(R.id.index_username_welcome).text = displayUser

        // change profile picture
        userName = fAuth.currentUser?.email.toString()
        val userNameFromMail = userName.substring(0, userName.indexOf("@"))
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userName).get().addOnSuccessListener { docSnap ->
            //get data class object for easy data assignment
            val doc = docSnap.toObject(User::class.java)
            if (doc != null) {
                if (doc.imagePath != null && doc.imagePath != "null") {
                    val sRef = storageReference.child(doc.imagePath.toString())
                    val localFile = File.createTempFile("tempImage", "jpg")
                    sRef.getFile(localFile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        profileButton.setImageBitmap(bitmap)
                    }.addOnFailureListener {
                    }
                } else {
                    profileButton.setImageResource(R.drawable.profile)
                }
            }
        }

        //profile button
        profileButton.setOnClickListener {
            findNavController()
                .navigate(R.id.action_indexFragment_to_profileFragment)
        }
        // info button (fragment)
        infoButton.setOnClickListener {
            findNavController()
                .navigate(R.id.action_indexFragment_to_infoFragment)
        }

        // events button
        eventsButton.setOnClickListener {
            findNavController()
                .navigate(R.id.action_indexFragment_to_calendarFragment)
        }

        // solution challenge button
        solutionChallengeButton.setOnClickListener {
            findNavController().navigate(R.id.action_indexFragment_to_solutionChallengeFragment)
        }

        // logout button
        view.findViewById<Button>(R.id.btn_logout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Log out successful", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_indexFragment_to_loginFragment)
        }
    }
}