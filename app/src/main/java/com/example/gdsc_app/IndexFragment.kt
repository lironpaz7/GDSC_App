package com.example.gdsc_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IndexFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IndexFragment : Fragment() {

    private lateinit var displayUser: String
    lateinit var fAuth: FirebaseAuth

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val appID = "APP_ID"
    private val region = "REGION"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(
                ARG_PARAM2
            )

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view = inflater.inflate(R.layout.fragment_index, container, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // extract user name to display on screen
        fAuth = FirebaseAuth.getInstance()
        var userName = fAuth.currentUser?.email.toString()
        userName = userName.substring(0, userName.indexOf("@"))
        displayUser = "Welcome " + userName
        view.findViewById<TextView>(R.id.userNameTitle).setText(displayUser)


        //profile button
        view.findViewById<ImageButton>(R.id.profileButton).setOnClickListener {
//            Toast.makeText(context, "Contact will be available soon...", Toast.LENGTH_SHORT).show()
            var navIndex = activity as FragmentNavigation
            navIndex.navigateFrag(ProfileFragment(), true)
        }
        // info button (fragment)
        view.findViewById<ImageButton>(R.id.infoButton).setOnClickListener {
            var navIndex = activity as FragmentNavigation
            navIndex.navigateFrag(InfoFragment(), true)
        }

        // events button
        view.findViewById<ImageButton>(R.id.eventsButton).setOnClickListener {
//            Toast.makeText(context, "Events will be available soon...", Toast.LENGTH_SHORT).show()
            var navEvents = activity as FragmentNavigation
            navEvents.navigateFrag(CalendarFragment(), true)


//            val intent = Intent(requireActivity(), EventsActivity::class.java)
//            startActivity(intent)

        }

        // logout button
        view.findViewById<Button>(R.id.btn_logout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            var navLogin = activity as FragmentNavigation
            Toast.makeText(context, "Log out successful", Toast.LENGTH_SHORT).show()
            navLogin.navigateFrag(LoginFragment(), false)
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IndexFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IndexFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}