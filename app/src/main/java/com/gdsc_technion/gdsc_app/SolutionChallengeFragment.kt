package com.gdsc_technion.gdsc_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 * Use the [SolutionChallengeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SolutionChallengeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_solution_challenge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // buttons assignments
        val overviewBtn = view.findViewById<Button>(R.id.solution_challenge_overview_btn)
        val prizesBtn = view.findViewById<Button>(R.id.solution_challenge_prizes_btn)
        val judgingBtn = view.findViewById<Button>(R.id.solution_challenge_judging_btn)
        val timelineBtn = view.findViewById<Button>(R.id.solution_challenge_timeline_btn)
        val videosBtn = view.findViewById<Button>(R.id.solution_challenge_videos_btn)
        val resourcesBtn = view.findViewById<Button>(R.id.solution_challenge_resources_btn)
        val faqsBtn = view.findViewById<Button>(R.id.solution_challenge_faqs_btn)
        val lookbackBtn = view.findViewById<Button>(R.id.solution_challenge_lookback_btn)


        overviewBtn.setOnClickListener {
            val url =
                "https://docs.google.com/presentation/d/1_hQ-Z7nx8Bjnb-n4DPXs3VzjsFKy7bVr/edit#slide=id.p3"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        prizesBtn.setOnClickListener {
            val url =
                "https://docs.google.com/presentation/d/1_hQ-Z7nx8Bjnb-n4DPXs3VzjsFKy7bVr/edit#slide=id.p5"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        judgingBtn.setOnClickListener {
            val url =
                "https://docs.google.com/presentation/d/1_hQ-Z7nx8Bjnb-n4DPXs3VzjsFKy7bVr/edit#slide=id.p6"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        timelineBtn.setOnClickListener {
            val url =
                "https://docs.google.com/presentation/d/1_hQ-Z7nx8Bjnb-n4DPXs3VzjsFKy7bVr/edit#slide=id.p7"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        videosBtn.setOnClickListener {
            val url =
                "https://docs.google.com/presentation/d/1_hQ-Z7nx8Bjnb-n4DPXs3VzjsFKy7bVr/edit#slide=id.p8"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        resourcesBtn.setOnClickListener {
            val url =
                "https://docs.google.com/presentation/d/1_hQ-Z7nx8Bjnb-n4DPXs3VzjsFKy7bVr/edit#slide=id.p9"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        faqsBtn.setOnClickListener {
            val url =
                "https://docs.google.com/presentation/d/1_hQ-Z7nx8Bjnb-n4DPXs3VzjsFKy7bVr/edit#slide=id.p13"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        lookbackBtn.setOnClickListener {
            val url =
                "https://docs.google.com/presentation/d/1_hQ-Z7nx8Bjnb-n4DPXs3VzjsFKy7bVr/edit#slide=id.p14"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }


    }
}