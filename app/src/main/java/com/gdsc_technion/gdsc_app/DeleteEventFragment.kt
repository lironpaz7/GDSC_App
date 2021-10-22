package com.gdsc_technion.gdsc_app

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.gdsc_technion.gdsc_app.databinding.FragmentDeleteEventBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [DeleteEventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteEventFragment : DialogFragment() {

    private lateinit var binding: FragmentDeleteEventBinding

    private var lastSelectedView: View? = null
    private var selectedEventData: EventData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentDeleteEventBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = DeleteEventItemAdapter(this::selectView)
        binding.deleteEventEvents.adapter = adapter
        val db = FirebaseFirestore.getInstance()
        db.collection("events").get().addOnSuccessListener { documents ->
            val events = documents.toObjects(EventData::class.java)
            adapter.submitList(events)
        }

        binding.deleteEventDeleteBtn.setOnClickListener {
            selectedEventData?.let { eventData ->
                db.collection("events")
                    .document("${eventData.date} ${eventData.time}")
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Document deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismiss()
                    }
            }
        }
    }

    private fun selectView(view: View, eventData: EventData) {
        clearLastSelectedItem()

        view.setBackgroundColor(Color.RED)
        lastSelectedView = view
        selectedEventData = eventData
    }

    private fun clearLastSelectedItem() {
        lastSelectedView?.setBackgroundColor(Color.TRANSPARENT)
    }
}