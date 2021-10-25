package com.gdsc_technion.gdsc_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdsc_technion.gdsc_app.databinding.FragmentCalenderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


data class EventData(
    var date: String? = null,
    var title: String? = null,
    var content: String? = null,
    var dateFormat: Date? = null,
    var time: String? = null,
    var timeObj: Date? = null,
    var location: String? = null,
    var url: String? = null
)

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalenderBinding
    private var currentDateSelected: String? = null
    private lateinit var cal: Calendar
    private lateinit var currentDateContent: TextView
    private lateinit var calenderUpcomingEvents: TextView
    private lateinit var addToCalendarBtn: ImageView
    private var allEventsDateTable: HashMap<String, String> = HashMap()
    private var allEventTable: HashMap<String, EventData> = HashMap()
    lateinit var fAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCalenderBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // initialize buttons, textviews and events list
        val dateParser = SimpleDateFormat("dd/MM/yyyy")
        cal = Calendar.getInstance()
        val calender = binding.calendar
        val backBtn = binding.calendarBackBtn
        val todayBtn = binding.calendarTodayBtn
        val selectBtn = binding.calendarSelectBtn
        val adminButton = binding.calendarEventAdminBtn
        addToCalendarBtn = binding.calendarCurrentAddToCalendarBtn

        fAuth = FirebaseAuth.getInstance()
        val userName = fAuth.currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userName).get().addOnSuccessListener {
            val data = it.toObject(User::class.java)
            if (data?.admin != null && data.admin == "yes") {
                adminButton.isVisible = true
                adminButton.isEnabled = true
            }
        }

        // adminButton button
        adminButton.setOnClickListener {
            EventAdminFragment().show(requireActivity().supportFragmentManager, null)
        }

        val upcomingEventsTable = ArrayList<EventData>()

        val currentDateTitle = view.findViewById<TextView>(R.id.calendar_current_title)
        currentDateContent = view.findViewById(R.id.calendar_current_content)
        calenderUpcomingEvents = view.findViewById(R.id.calendar_upcoming_content)


        // updates current date view title with current date
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        val todayPresentationFormat = "$day/${month + 1}/$year"
        currentDateTitle.text = todayPresentationFormat
        val dateToday = dateParser.parse(todayPresentationFormat)

        //extract events from firebase
        db.collection("events").get().addOnSuccessListener { documents ->
            for (doc in documents) {
                val data = doc.toObject(EventData::class.java)
                val docNameSplit = doc.id.split(" ")
                val dateFormat = docNameSplit[0].replace(".", "/")
                val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
                data.timeObj = sdf.parse(doc.id.toString())

                val currentDate = dateParser.parse(dateFormat)
                allEventsDateTable[dateFormat] = data.title.toString()
                allEventTable[dateFormat] = data
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
                    textTmp.append("${event.date} ${event.time}: ${event.title}\n")
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

        // add to calendar button
        addToCalendarBtn.setOnClickListener {
            val obj = allEventTable[currentDateSelected]
            val eventStart = obj?.timeObj?.time
            val insertCalendarIntent =
                Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, obj?.title)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventStart)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, obj?.location)
            startActivity(insertCalendarIntent)
        }

        // back button
        backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_global_indexFragment)
        }

        // select button
        selectBtn.setOnClickListener {
            if (currentDateSelected != null) {
                if (currentDateSelected in allEventsDateTable) {
                    if (allEventTable[currentDateSelected]?.url != null) {
                        val url = allEventTable[currentDateSelected]?.url
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } else {
                        findNavController().navigate(R.id.action_calendarFragment_self)
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
    }

    private fun checkAndUpdateEvent(date: String) {
        if (date in allEventsDateTable) {
            currentDateContent.text = allEventsDateTable[date]
            addToCalendarBtn.visibility = View.VISIBLE
        } else {
            currentDateContent.text = "No events"
            addToCalendarBtn.visibility = View.INVISIBLE
        }
    }
}