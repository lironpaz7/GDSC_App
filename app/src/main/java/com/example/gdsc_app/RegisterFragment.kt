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
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        username = view.findViewById(R.id.reg_username)
        password = view.findViewById(R.id.reg_password)
        confirmPassword = view.findViewById(R.id.reg_confirm_password)
        fAuth = FirebaseAuth.getInstance()

        view.findViewById<Button>(R.id.btn_login_reg).setOnClickListener {
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(LoginFragment(), false)
        }

        view.findViewById<Button>(R.id.btn_register_reg).setOnClickListener {
            validateEmptyForm()
        }

        view.findViewById<Button>(R.id.btn_clear_reg).setOnClickListener {
            username.setText("")
            password.setText("")
            confirmPassword.setText("")
        }
        return view
    }

    private fun fireBaseSignUp() {
        val btn = view?.findViewById<Button>(R.id.btn_register_reg)
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
                            val navLogin = activity as FragmentNavigation

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
                            navLogin.navigateFrag(LoginFragment(), true)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}