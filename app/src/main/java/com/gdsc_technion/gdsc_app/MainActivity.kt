package com.gdsc_technion.gdsc_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
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

//        supportFragmentManager.beginTransaction().add(R.id.container, LoginFragment()).commit()
        setupActionBarWithNavController(findNavController(R.id.mainFragment))

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.mainFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}