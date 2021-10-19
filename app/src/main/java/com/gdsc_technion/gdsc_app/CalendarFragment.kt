package com.gdsc_technion.gdsc_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import com.gdsc_technion.gdsc_app.R
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
    var title: String? = null,
    var content: String? = null,
    var dateFormat: Date? = null,
    var url: String? = null
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
    private var allEventsDateTable: HashMap<String, String> = HashMap()
    private var allEventsTable: HashMap<String, EventsData> = HashMap()

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
        val todayBtn = view.findViewById<Button>(R.id.calendar_today_btn)
        val selectBtn = view.findViewById<Button>(R.id.calendar_select_btn)

        val upcomingEventsTable = ArrayList<EventsData>()

        val currentDateTitle = view.findViewById<TextView>(R.id.calendar_current_title)
        currentDateContent = view.findViewById(R.id.calendar_current_content)
        calenderUpcomingEvents = view.findViewById(R.id.calendar_upcoming_content)


        // updates current date view title with current date
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        var todayPresentationFormat = "$day/${month + 1}/$year"
        currentDateTitle.text = todayPresentationFormat
        val dateToday = dateParser.parse(todayPresentationFormat)

        //extract events from firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("events").get().addOnSuccessListener { documents ->
            for (doc in documents) {
                val data = doc.toObject(EventsData::class.java)
//                Log.d("@@@@@@@@", data.content.toString())
                val dateFormat = doc.id.replace(".", "/")
                val currentDate = dateParser.parse(dateFormat)
                allEventsDateTable[dateFormat] = data.title.toString()
                allEventsTable[dateFormat] = data
                data.date = dateFormat
                data.dateFormat = currentDate
                if (currentDate.after(dateToday)) {
                    upcomingEventsTable.add(data)
                }
            }

            checkAndUpdateEvent(todayPresentationFormat)

            if (upcomingEventsTable.isEmpty()) {
                calenderUpcomingEvents.text = "No upcoming events"
            } else {
                // sorts the upcoming events list
                upcomingEventsTable.sortBy {
                    it.dateFormat
                }

                //update upcoming events
                val textTmp = StringBuilder()
                for (event in upcomingEventsTable) {
                    textTmp.append("${event.date}: ${event.title}\n")
                }
                calenderUpcomingEvents.text = textTmp.toString()
            }

        }

        // when choosing a date on calendar
        calender.setOnDateChangeListener { _, year, month, day ->
            val presentationFormat = "$day/${month + 1}/$year"
            currentDateSelected = presentationFormat
            currentDateTitle.text = presentationFormat
            checkAndUpdateEvent(presentationFormat)
        }

        // today button
        todayBtn.setOnClickListener {
            calender.setDate(Calendar.getInstance().timeInMillis, true, false)
            currentDateSelected = todayPresentationFormat
            currentDateTitle.text = todayPresentationFormat
            checkAndUpdateEvent(todayPresentationFormat)
        }

        // back button
        backBtn.setOnClickListener {
            val navCalender = activity as FragmentNavigation
            navCalender.navigateFrag(IndexFragment(), false)
        }

        // select button
        selectBtn.setOnClickListener {
            if (currentDateSelected != null) {
                if (currentDateSelected in allEventsDateTable) {
                    if (allEventsTable[currentDateSelected]?.url != null) {
                        val url = allEventsTable[currentDateSelected]?.url
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                    } else {
                        val navEvent = activity as FragmentNavigation
                        navEvent.navigateFrag(EventFragment(currentDateSelected), true)
                    }
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
        if (date in allEventsDateTable) {
            currentDateContent.text = allEventsDateTable[date]
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