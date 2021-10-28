package com.gdsc_technion.gdsc_app

import android.content.Context
import android.content.ContextWrapper
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
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


/**
 * A simple [Fragment] subclass.
 * Use the [IndexFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IndexFragment : Fragment() {

    private lateinit var displayUser: String

    private lateinit var profileButton: ImageView
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

        profileButton = view.findViewById(R.id.index_profileButton)
        val infoButton = view.findViewById<ImageButton>(R.id.infoButton)
        val eventsButton = view.findViewById<ImageButton>(R.id.eventsButton)
        val solutionChallengeButton = view.findViewById<ImageView>(R.id.solution_challenge_button)

        // extract user name to display on screen
        fAuth = FirebaseAuth.getInstance()
        var userName = fAuth.currentUser?.email.toString()
        userName = userName.substring(0, userName.indexOf("@"))
        displayUser = "Welcome $userName"
        view.findViewById<TextView>(R.id.index_username_welcome).text = displayUser

        // load profile picture
        val cw = ContextWrapper(requireContext())
        // path to /data/data/yourapp/app_data/imageDir
        val imagePath = cw.getDir("imageDir", Context.MODE_PRIVATE).absolutePath
        loadImageFromStorage(imagePath)

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
        solutionChallengeButton.alpha = ALPHA_OFF
        solutionChallengeButton.setOnClickListener {
//            findNavController().navigate(R.id.action_indexFragment_to_solutionChallengeFragment)
            Toast.makeText(
                context,
                "Solution Challenge will be available soon...",
                Toast.LENGTH_SHORT
            ).show()
        }

        // logout button
        view.findViewById<Button>(R.id.btn_logout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Log out successful", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_indexFragment_to_loginFragment)
        }
    }

    private fun setDefaultProfilePicture() {
        profileButton.setImageResource(R.drawable.profile)
    }

    private fun loadImageFromStorage(path: String) {
        try {
            val f = File(path, "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            profileButton.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            setDefaultProfilePicture()
        }
    }
}