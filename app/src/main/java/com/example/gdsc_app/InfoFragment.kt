package com.example.gdsc_app

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate

class InfoFragment : Fragment() {

    companion object {
        fun newInstance() = InfoFragment()
    }

    private lateinit var viewModel: InfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.info_fragment, container, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // back button
        view.findViewById<Button>(R.id.infoBackButton).setOnClickListener {
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(IndexFragment(), false)
        }

        // website button
        view.findViewById<ImageButton>(R.id.websiteBtn).setOnClickListener {
            val url = "https://gdsc.community.dev/technion-israel-institute-of-technology-haifa/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        // youtube button
        view.findViewById<ImageButton>(R.id.youtubeBtn).setOnClickListener {
            val url = "https://youtube.com/channel/UCDOdo6vJAuxZjQ7xI3WJOFg"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        // facebook button
        view.findViewById<ImageButton>(R.id.facebookBtn).setOnClickListener {
            val url = "https://www.facebook.com/groups/354960796301121"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        // users button
        view.findViewById<ImageButton>(R.id.activeUsersBtn).setOnClickListener {
            val activeUsersBtn = activity as FragmentNavigation
            activeUsersBtn.navigateFrag(ActiveUsersFragment(), true)
        }

        // telegram button
        view.findViewById<ImageButton>(R.id.telegramBtn).setOnClickListener {
            val url =
                "https://t.me/gdscTechnion"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        // instagram button
        view.findViewById<ImageButton>(R.id.instagramBtn).setOnClickListener {
            val url =
                "https://www.instagram.com/p/CTcg2lPsmZc/?utm_medium=share_sheet"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        // linkedin button
        view.findViewById<ImageButton>(R.id.linkedinBtn).setOnClickListener {
            val url = "https://www.linkedin.com/groups/12559597"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}