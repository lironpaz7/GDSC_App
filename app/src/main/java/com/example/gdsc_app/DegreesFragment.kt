package com.example.gdsc_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.google.android.gms.dynamic.SupportFragmentWrapper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DegreesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DegreesFragment() : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_degrees, container, false)
        val navProfile = activity as FragmentNavigation


        // declare buttons
        val c1 = view.findViewById<Button>(R.id.degrees_c1)
        val c2 = view.findViewById<Button>(R.id.degrees_c2)
        val c3 = view.findViewById<Button>(R.id.degrees_c3)
        val c4 = view.findViewById<Button>(R.id.degrees_c4)
        val c5 = view.findViewById<Button>(R.id.degrees_c5)
        val c6 = view.findViewById<Button>(R.id.degrees_c6)
        val c7 = view.findViewById<Button>(R.id.degrees_c7)
        val c8 = view.findViewById<Button>(R.id.degrees_c8)
        val c9 = view.findViewById<Button>(R.id.degrees_c9)

        // set buttons text

        c1.text = "Data Science"
        c2.text = "Computer Science"
        c3.text = "Electircal Engineering"
        c4.text = "Information Systems"
        c5.text = "Industrial Engineering"
        c6.text = "Biology"
        c7.text = "Mathematics"
        c8.text = "Physics"
        c9.text = "Other"

        // set buttons
        c1.setOnClickListener {
            navProfile.navigateFrag(ProfileFragment(c1.text.toString()), false)
        }
        c2.setOnClickListener {
            navProfile.navigateFrag(ProfileFragment(c2.text.toString()), false)
        }
        c3.setOnClickListener {
            navProfile.navigateFrag(ProfileFragment(c3.text.toString()), false)
        }
        c4.setOnClickListener {
            navProfile.navigateFrag(ProfileFragment(c4.text.toString()), false)
        }
        c5.setOnClickListener {
            navProfile.navigateFrag(ProfileFragment(c5.text.toString()), false)
        }
        c6.setOnClickListener {
            navProfile.navigateFrag(ProfileFragment(c6.text.toString()), false)
        }
        c7.setOnClickListener {
            navProfile.navigateFrag(ProfileFragment(c7.text.toString()), false)
        }
        c8.setOnClickListener {
            navProfile.navigateFrag(ProfileFragment(c8.text.toString()), false)
        }
        c9.setOnClickListener {
            navProfile.navigateFrag(ProfileFragment(c9.text.toString()), false)
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
         * @return A new instance of fragment degreesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DegreesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}