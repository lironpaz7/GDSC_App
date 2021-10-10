package com.example.gdsc_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
const val ALPHA_ON = 1.0f
const val ALPHA_OFF = 0.3f
const val TECHNION_MAIL = "campus.technion.ac.il"
const val MAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var fAuth: FirebaseAuth
    private lateinit var activityResultLaunch: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)

        activityResultLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(RC_SIGN_IN, result)
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        username = view.findViewById(R.id.log_username)
        password = view.findViewById(R.id.log_password)
        fAuth = FirebaseAuth.getInstance()

        view.findViewById<TextView>(R.id.btn_register).setOnClickListener {
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), true)
        }

        // forgot password
        view.findViewById<TextView>(R.id.forgotPassword).setOnClickListener {
            val navForgot = activity as FragmentNavigation
            navForgot.navigateFrag(ForgotFragment(), true)
        }

        // login button
        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            validateForm()
        }

        // sign in with google button
        view.findViewById<ImageButton>(R.id.btn_login_google).setOnClickListener {
            val intent = googleSignInClient.signInIntent
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                onActivityResult(RC_SIGN_IN, result)
//            }
            activityResultLaunch.launch(intent)
//            startActivityForResult(intent, RC_SIGN_IN)
        }
        return view
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = accountTask.getResult(ApiException::class.java)
                    fireBaseAuthWithGoogleAccount(account)
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to sign in with google", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun fireBaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)

        val btn = view?.findViewById<ImageButton>(R.id.btn_login_google)
        if (btn != null) {
            btn.isEnabled = false
            btn.alpha = ALPHA_OFF
        }

        fAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userEmail = fAuth.currentUser?.email.toString()
                val userName = userEmail.substring(0, userEmail.indexOf("@"))
                val navIndex = activity as FragmentNavigation
                Toast.makeText(
                    context,
                    "Login Successful as $userName",
                    Toast.LENGTH_SHORT
                ).show()

                // collects data
                val userInfo = hashMapOf(
                    "id" to userName
                )

                // collects data and store on Firestore -> doc name = user email.
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("users")
                userRef.document(userEmail).get().addOnSuccessListener {
                    if (!it.exists()) {
                        userRef.document(userEmail).set(userInfo)
                    }
                }

                navIndex.navigateFrag(IndexFragment(), true)
            } else {
                if (btn != null) {
                    btn.isEnabled = true
                    btn.alpha = ALPHA_ON
                }
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun validateForm() {
        val errorIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.error)
        errorIcon?.setBounds(0, 0, 70, 70)
        when {
            TextUtils.isEmpty(username.text.toString().trim()) -> {
                username.setError("Please Enter Username", errorIcon)
            }
            TextUtils.isEmpty(password.text.toString().trim()) -> {
                password.setError("Please Enter Password", errorIcon)
            }
        }

        if (username.text.toString().isNotEmpty() && password.text.toString()
                .isNotEmpty()
        ) {
            val user = username.text.toString()
            val endMail = user.substring(user.indexOf("@") + 1)
            if (username.text.toString()
                    .matches(Regex(MAIL_PATTERN)) || endMail.equals(
                    TECHNION_MAIL
                )
            ) {
                if (password.text.toString().length >= 5) {
                    fireBaseSignIn()
                } else {
                    password.setError("Please Enter At Lease 5 Characters", errorIcon)
                }
            } else {
                username.setError("Please Enter Valid Email Id", errorIcon)
            }
        }
    }

    private fun fireBaseSignIn() {
        val btn = view?.findViewById<Button>(R.id.btn_login)
        if (btn != null) {
            btn.isEnabled = false
            btn.alpha = ALPHA_OFF
        }

        fAuth.signInWithEmailAndPassword(username.text.toString(), password.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // check verified email
                    val verified = fAuth.currentUser?.isEmailVerified
                    if (verified == false) {
                        Toast.makeText(
                            context, "Please verify your account", Toast.LENGTH_SHORT
                        ).show()
                        if (btn != null) {
                            btn.isEnabled = true
                            btn.alpha = ALPHA_ON
                        }
                    } else {
                        var userName = fAuth.currentUser?.email.toString()
                        userName = userName.substring(0, userName.indexOf("@"))
                        val navIndex = activity as FragmentNavigation
                        Toast.makeText(
                            context,
                            "Login Successful as $userName",
                            Toast.LENGTH_SHORT
                        ).show()
                        navIndex.navigateFrag(IndexFragment(), true)
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

    companion object {

        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}