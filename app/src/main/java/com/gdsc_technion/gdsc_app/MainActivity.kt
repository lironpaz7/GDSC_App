package com.gdsc_technion.gdsc_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.gdsc_technion.gdsc_app.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), FragmentNavigation {
    private lateinit var fAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

//        fAuth = FirebaseAuth.getInstance()
//
//        val currentUser = fAuth.currentUser
//        if (currentUser != null) {
//            supportFragmentManager
//                .beginTransaction()
//                .add(R.id.container, IndexFragment()).addToBackStack(null)
//        } else {
//            supportFragmentManager.beginTransaction().add(R.id.container, LoginFragment()).commit()
//        }

        supportFragmentManager.beginTransaction().add(R.id.container, LoginFragment()).commit()


    }

    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
        val transaction =
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)

        if (addToStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}