package com.gdsc_technion.gdsc_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.gdsc_technion.gdsc_app.databinding.FragmentEventAdminBinding


/**
 * A simple [Fragment] subclass.
 * Use the [EventAdminFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventAdminFragment : DialogFragment() {

    private lateinit var binding: FragmentEventAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentEventAdminBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.adminCreateBtn.setOnClickListener {
            AddEventFragment().show(requireActivity().supportFragmentManager, null)
            dismiss()
        }

        binding.adminDeleteBtn.setOnClickListener {
            DeleteEventFragment().show(requireActivity().supportFragmentManager, null)
            dismiss()
        }
        binding.adminEditBtn.alpha = ALPHA_OFF
        binding.adminEditBtn.setOnClickListener {
            Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

}