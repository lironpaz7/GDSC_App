package com.gdsc_technion.gdsc_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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

        val rulesBtn = view.findViewById<Button>(R.id.solution_challenge_rules_btn)
        rulesBtn.alpha = ALPHA_OFF
        val challengesBtn = view.findViewById<Button>(R.id.solution_challenge_challenges_btn)
        challengesBtn.alpha = ALPHA_OFF

        rulesBtn.setOnClickListener {
            Toast.makeText(context, "Rules will be updated soon...", Toast.LENGTH_SHORT).show()
        }

        challengesBtn.setOnClickListener {
            Toast.makeText(context, "Challenges will be updated soon...", Toast.LENGTH_SHORT).show()
        }

    }
}