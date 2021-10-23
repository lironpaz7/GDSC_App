package com.gdsc_technion.gdsc_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

const val ALPHA_ON = 1.0f
const val ALPHA_OFF = 0.2f
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)

        activityResultLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                onActivityResult(1, result)
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.findViewById<TextView>(R.id.versionNumber).text = BuildConfig.VERSION_NAME

        username = view.findViewById(R.id.log_username)
        password = view.findViewById(R.id.log_password)
        fAuth = FirebaseAuth.getInstance()

        view.findViewById<TextView>(R.id.login_btn_register).setOnClickListener {
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(), true)
        }

        // forgot password
        view.findViewById<TextView>(R.id.login_forgotPassword).setOnClickListener {
            val navForgot = activity as FragmentNavigation
            navForgot.navigateFrag(ForgotFragment(), true)
        }

        // login button
        view.findViewById<Button>(R.id.login_btn).setOnClickListener {
            validateForm()
        }

        // sign in with google button
        view.findViewById<ImageButton>(R.id.login_google_btn).setOnClickListener {
            val intent = googleSignInClient.signInIntent
            activityResultLaunch.launch(intent)
        }
        return view
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
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

        val btn = view?.findViewById<ImageButton>(R.id.login_google_btn)
        if (btn != null) {
            btn.isEnabled = false
            btn.alpha = ALPHA_OFF
        }

        fAuth.signInWithCredential(credential).addOnSuccessListener { task ->
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
        val btn = view?.findViewById<Button>(R.id.login_btn)
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

}