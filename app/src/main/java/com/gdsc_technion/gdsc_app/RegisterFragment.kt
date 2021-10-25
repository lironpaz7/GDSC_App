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
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var fAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        username = view.findViewById(R.id.reg_username)
        password = view.findViewById(R.id.reg_password)
        confirmPassword = view.findViewById(R.id.reg_confirm_password)
        fAuth = FirebaseAuth.getInstance()

        view.findViewById<Button>(R.id.register_back_btn).setOnClickListener {
            findNavController().navigate(R.id.action_global_loginFragment)
        }

        view.findViewById<Button>(R.id.register_reg_btn).setOnClickListener {
            validateEmptyForm()
        }

        view.findViewById<Button>(R.id.register_clear_btn).setOnClickListener {
            username.setText("")
            password.setText("")
            confirmPassword.setText("")
        }
    }

    private fun fireBaseSignUp() {
        val btn = view?.findViewById<Button>(R.id.register_reg_btn)
        if (btn != null) {
            btn.isEnabled = false
            btn.alpha = ALPHA_OFF
        }

        fAuth.createUserWithEmailAndPassword(username.text.toString(), password.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = fAuth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Verification email has been sent",
                                Toast.LENGTH_SHORT
                            ).show()
//                            val navLogin = activity as FragmentNavigation

                            val userEmail = username.text.toString()
                            // collects data
                            val userInfo = hashMapOf(
                                "id" to userEmail
                            )

                            // collects data and store on Firestore -> doc name = user email.
                            val db = FirebaseFirestore.getInstance()
                            val userRef = db.collection("users")
                            userRef.document(userEmail).set(userInfo)

                            // login and move to LoginFragment
//                            navLogin.navigateFrag(LoginFragment(), true)
                            findNavController().navigate(R.id.action_global_loginFragment)
                        } else {
                            Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    if (btn != null) {
                        btn.isEnabled = true
                        btn.alpha = ALPHA_ON
                    }
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateEmptyForm() {
        val errorIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.error)
        errorIcon?.setBounds(0, 0, 70, 70)
        when {
            TextUtils.isEmpty(username.text.toString().trim()) -> {
                username.setError("Please Enter Username", errorIcon)
            }
            TextUtils.isEmpty(password.text.toString().trim()) -> {
                password.setError("Please Enter Password", errorIcon)
            }
            TextUtils.isEmpty(confirmPassword.text.toString().trim()) -> {
                confirmPassword.setError("Please Enter Password Again", errorIcon)
            }
        }

        if (username.text.toString().isNotEmpty() && password.text.toString()
                .isNotEmpty() && confirmPassword.text.toString().isNotEmpty()
        ) {
            val user = username.text.toString()
            val endMail = user.substring(user.indexOf("@") + 1)
            if (user.matches(Regex(MAIL_PATTERN)) || endMail.equals(
                    TECHNION_MAIL
                )
            ) {
                if (password.text.toString().length >= 5) {
                    if (password.text.toString() == confirmPassword.text.toString()) {
                        fireBaseSignUp()
//                        Toast.makeText(context, "Register Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        confirmPassword.setError("Password Didn't Match", errorIcon)
                    }
                } else {
                    password.setError("Please Enter At Lease 5 Characters", errorIcon)
                }
            } else {
                username.setError("Please Enter Valid Email Id", errorIcon)
            }
        }
    }
}