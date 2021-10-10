package com.example.gdsc_app

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var username: EditText
    private lateinit var fAuth: FirebaseAuth

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
        val view = inflater.inflate(R.layout.fragment_forgot, container, false)

        username = view.findViewById(R.id.log_username_forgot)
        fAuth = FirebaseAuth.getInstance()

        // reset password
        view.findViewById<Button>(R.id.btn_reset_forgot).setOnClickListener {
            validateForm()
        }

        // back button
        view.findViewById<Button>(R.id.btn_back_forgot).setOnClickListener {
            val navLogin = activity as FragmentNavigation
            navLogin.navigateFrag(LoginFragment(), false)
        }
        return view
    }

    private fun validateForm() {
        val errorIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.error)
        errorIcon?.setBounds(0, 0, 70, 70)
        when {
            TextUtils.isEmpty(username.text.toString().trim()) -> {
                username.setError("Please Enter Username", errorIcon)
            }
        }

        if (username.text.toString().isNotEmpty()) {
            val email = username.text.toString()
            val endMail = email.substring(email.indexOf("@") + 1)
            if (email
                    .matches(Regex(MAIL_PATTERN)) || endMail.equals(
                    TECHNION_MAIL
                )
            ) {
                resetPassword(email)
            } else {
                username.setError("Please Enter Valid Email Id", errorIcon)
            }
        }
    }

    private fun resetPassword(email: String) {
        fAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val navIndex = activity as FragmentNavigation
                Toast.makeText(
                    context,
                    "an email with a reset link has been sent to you",
                    Toast.LENGTH_SHORT
                ).show()
                navIndex.navigateFrag(LoginFragment(), false)

            } else {
                Toast.makeText(
                    context,
                    "try again! something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment forgotFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ForgotFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}