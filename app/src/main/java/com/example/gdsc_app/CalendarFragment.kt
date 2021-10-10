package com.example.gdsc_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import events.EventFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

data class EventsData(
    var date: String? = null,
    var content: String? = null,
    var dateFormat: Date? = null
)

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var currentDateSelected: String? = null
    private lateinit var cal: Calendar
    private lateinit var currentDateContent: TextView
    private lateinit var calenderUpcomingEvents: TextView
    private var allEventsTable: HashMap<String, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calender, container, false)

        // initialize buttons, textviews and events list
        val dateParser = SimpleDateFormat("dd/MM/yyyy")
        cal = Calendar.getInstance()
        val calender = view.findViewById<CalendarView>(R.id.calendar)
        val backBtn = view.findViewById<Button>(R.id.calendar_back_btn)
        val selectBtn = view.findViewById<Button>(R.id.calendar_select_btn)

        val upcomingEventsTable = ArrayList<EventsData>()

        val currentDateTitle = view.findViewById<TextView>(R.id.calendar_current_title)
        currentDateContent = view.findViewById(R.id.calendar_current_content)
        calenderUpcomingEvents = view.findViewById(R.id.calendar_upcoming_content)


        // updates current date view title with current date
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        var datePresentationFormat = "$day/${month + 1}/$year"
        currentDateTitle.text = datePresentationFormat
        val dateToday = dateParser.parse(datePresentationFormat)

        //extract events from firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("events").get().addOnSuccessListener { documents ->
            for (doc in documents) {
                val data = doc.toObject(EventsData::class.java)
                val dateFormat = doc.id.replace(".", "/")
                val currentDate = dateParser.parse(dateFormat)
                allEventsTable[dateFormat] = data.content.toString()
                if (currentDate.after(dateToday)) {
                    data.date = dateFormat
                    data.dateFormat = currentDate
                    upcomingEventsTable.add(data)
                }
//                Log.d("dateFormat", dateFormat)
//                Log.d("content", data.content.toString())
//                Toast.makeText(context, doc.id, Toast.LENGTH_SHORT).show()

            }

            checkAndUpdateEvent(datePresentationFormat)

            // sorts the upcoming events list
            upcomingEventsTable.sortBy {
                it.dateFormat
            }

            //update upcoming events
            val textTmp = StringBuilder()
            for (event in upcomingEventsTable) {
                textTmp.append("${event.date}: ${event.content}\n")
            }
            calenderUpcomingEvents.text = textTmp.toString()

        }

        // when choosing a date on calendar
        calender.setOnDateChangeListener { _, year, month, day ->
            datePresentationFormat = "$day/${month + 1}/$year"
            currentDateSelected = datePresentationFormat
            currentDateTitle.text = datePresentationFormat
            checkAndUpdateEvent(datePresentationFormat)
        }

        // back button
        backBtn.setOnClickListener {
            val navCalender = activity as FragmentNavigation
            navCalender.navigateFrag(IndexFragment(), false)
        }

        // select button
        selectBtn.setOnClickListener {
            if (currentDateSelected != null) {
                if (currentDateSelected in allEventsTable) {
                    val navEvent = activity as FragmentNavigation
                    navEvent.navigateFrag(EventFragment(currentDateSelected), true)
                } else {
                    Toast.makeText(context, "No events found on this date", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(context, "Please select a date first", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }

    private fun checkAndUpdateEvent(date: String) {
        if (date in allEventsTable) {
            currentDateContent.text = allEventsTable[date]
        } else {
            currentDateContent.text = "No events"
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CalenderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}