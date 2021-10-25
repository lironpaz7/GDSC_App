package com.gdsc_technion.gdsc_app

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotFragment : Fragment() {
    private lateinit var username: EditText
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        username = view.findViewById(R.id.forgot_username_text)
        fAuth = FirebaseAuth.getInstance()

        // reset password
        view.findViewById<Button>(R.id.forgot_reset_btn).setOnClickListener {
            validateForm()
        }

        // back button
        view.findViewById<Button>(R.id.forgot_back_btn).setOnClickListener {
            findNavController().navigate(R.id.action_global_loginFragment)
        }
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
//                val navIndex = activity as FragmentNavigation
                Toast.makeText(
                    context,
                    "an email with a reset link has been sent to you",
                    Toast.LENGTH_SHORT
                ).show()
//                navIndex.navigateFrag(LoginFragment(), false)
                findNavController().navigate(R.id.action_global_loginFragment)
            } else {
                Toast.makeText(
                    context,
                    "try again! something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}