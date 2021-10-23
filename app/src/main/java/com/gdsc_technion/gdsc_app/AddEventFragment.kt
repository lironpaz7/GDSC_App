package com.gdsc_technion.gdsc_app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 * Use the [AddEventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEventFragment : DialogFragment() {
    lateinit var fAuth: FirebaseAuth
    var cal: Calendar = Calendar.getInstance()
    private lateinit var dateText: TextView
    private lateinit var titleText: EditText
    private lateinit var timeText: TextView
    private lateinit var locationText: EditText
    lateinit var linkText: EditText

    private lateinit var eventsRef: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_event, container, false)
        val db = FirebaseFirestore.getInstance()
        eventsRef = db.collection("events")

        // buttons and textviews
        dateText = view.findViewById(R.id.add_event_date_text)
        titleText = view.findViewById(R.id.add_event_title_text)
        linkText = view.findViewById(R.id.add_event_link_text)
        timeText = view.findViewById(R.id.add_event_time_text)
        locationText = view.findViewById(R.id.add_event_location_text)
        val datePicker = view.findViewById<ImageView>(R.id.add_event_date_btn)
        val timePicker = view.findViewById<ImageView>(R.id.add_event_time_btn)

        val submitBtn = view.findViewById<Button>(R.id.add_event_submit_btn)
        val resetBtn = view.findViewById<Button>(R.id.add_event_reset_btn)

        // Calendar variables
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)


        // reset button
        resetBtn.setOnClickListener {
            titleText.setText("")
            linkText.setText("")
            locationText.setText("")
            dateText.text = ""
            timeText.text = ""
        }

        // submit button
        submitBtn.setOnClickListener {
            validateForm()
        }

        // datePicker button
        datePicker.setOnClickListener {
            DatePickerDialog(
                requireActivity(),
                { _, mYear, mMonth, mDay ->
                    dateText.setText("$mDay/${mMonth + 1}/$mYear")
                },
                year,
                month,
                day
            ).show()
        }

        // time picker
        timePicker.setOnClickListener {
            TimePickerDialog(
                requireActivity(),
                { _, h, m ->
                    var fixedMinute = m.toString()
                    if (m < 10) {
                        fixedMinute = "0$m"
                    }
                    timeText.text = "$h:$fixedMinute"
                }, hour, minute, true
            ).show()
        }

        return view
    }

    private fun validateForm() {
        val errorIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.error)
        errorIcon?.setBounds(0, 0, 70, 70)
        when {
            TextUtils.isEmpty(titleText.text.toString().trim()) -> {
                titleText.setError("Please enter a title", errorIcon)
            }
            TextUtils.isEmpty(dateText.text.toString().trim()) -> {
                Toast.makeText(context, "Please choose a date", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(timeText.text.toString().trim()) -> {
                Toast.makeText(context, "Please choose time", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(locationText.text.toString().trim()) -> {
                locationText.setError("Please enter a location", errorIcon)
            }
            TextUtils.isEmpty(linkText.text.toString().trim()) -> {
                linkText.setError("Please enter a link", errorIcon)
            }
        }

        if (titleText.text.toString().isNotEmpty() && dateText.text.toString()
                .isNotEmpty() && timeText.text.toString().isNotEmpty() && linkText.text.toString()
                .isNotEmpty()
        ) {
            val title = titleText.text.toString()
            val date = dateText.text.toString().replace("/", ".")
            val time = timeText.text.toString()
            var link = linkText.text.toString()
            if (!"^https?://.*$".toRegex().matches(link)) {
                link = "https://$link"
            }
            val location = locationText.text.toString()

            addEventOnFireBase(title, date, time, location, link)

        }
    }

    private fun addEventOnFireBase(
        title: String,
        date: String,
        time: String,
        location: String,
        link: String
    ) {
        val data = HashMap<String, String>()
        data["title"] = title
        data["date"] = date
        data["time"] = time
        data["url"] = link
        data["location"] = location

        val docId = "$date $time"
        eventsRef.document(docId).set(data).addOnSuccessListener {
            Toast.makeText(context, "Event added successfully!", Toast.LENGTH_SHORT).show()
            dismiss()
        }.addOnFailureListener {
            Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_SHORT)
                .show()
        }
    }
}