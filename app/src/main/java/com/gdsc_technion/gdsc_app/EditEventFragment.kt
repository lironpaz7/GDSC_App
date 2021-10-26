package com.gdsc_technion.gdsc_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.gdsc_technion.gdsc_app.databinding.FragmentEditEventBinding

/**
 * A simple [Fragment] subclass.
 * Use the [EditEventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditEventFragment : DialogFragment() {

    private lateinit var binding: FragmentEditEventBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentEditEventBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }


}