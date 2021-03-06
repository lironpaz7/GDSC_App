package com.gdsc_technion.gdsc_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
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
import kotlinx.parcelize.Parcelize
import org.naishadhparmar.zcustomcalendar.CustomCalendar
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener
import org.naishadhparmar.zcustomcalendar.Property
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

@Parcelize
data class EventData(
    var date: String? = null,
    var title: String? = null,
    var content: String? = null,
    var dateFormat: Date? = null,
    var time: String? = null,
    var timeObj: Date? = null,
    var location: String? = null,
    var url: String? = null
) : Parcelable

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarFragment : Fragment(), OnNavigationButtonClickedListener {
    private lateinit var binding: FragmentCalenderBinding
    private var currentDateSelected: String? = null

    // calendar
    private lateinit var calendar: Calendar
    private lateinit var dateParser: SimpleDateFormat
    private lateinit var descHashMap: HashMap<Any, Property>
    private lateinit var dateHashMap: HashMap<Int, Any>
    private lateinit var customCalendar: CustomCalendar
    private lateinit var upcomingEventsTable: ArrayList<EventData>
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
        dateParser = SimpleDateFormat("dd/MM/yyyy")
        customCalendar = binding.calendar
        val backBtn = binding.calendarBackBtn
        val todayBtn = binding.calendarTodayBtn
        val selectBtn = binding.calendarSelectBtn
        val adminButton = binding.calendarEventAdminBtn
        addToCalendarBtn = binding.calendarCurrentAddToCalendarBtn
        descHashMap = HashMap()
        dateHashMap = HashMap()
        upcomingEventsTable = ArrayList()
        calendar = Calendar.getInstance()

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

        val currentDateTitle = view.findViewById<TextView>(R.id.calendar_current_title)
        currentDateContent = view.findViewById(R.id.calendar_current_content)
        calenderUpcomingEvents = view.findViewById(R.id.calendar_upcoming_content)


        // updates current date view title with current date
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
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
                data.timeObj = sdf.parse(doc.id)

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
            initializeCalendar()
        }


        // CustomCalendar setup
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);
        customCalendar.setOnDateSelectedListener { view, selectedDate, desc ->
            val day = selectedDate.get(Calendar.DAY_OF_MONTH)
            val month = selectedDate.get(Calendar.MONTH) + 1
            val year = selectedDate.get(Calendar.YEAR)

            val sDate = "$day/$month/$year"
            currentDateSelected = sDate
            currentDateTitle.text = sDate
            checkAndUpdateEvent(sDate)

//            Toast.makeText(context, sDate, Toast.LENGTH_SHORT).show()
        }

        // today button
        todayBtn.setOnClickListener {
            dateHashMap.putAll(updateCalenderMonth(calendar.get(Calendar.MONTH)))
            customCalendar.setDate(calendar, dateHashMap)
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

    private fun initializeCalendar() {
        val defaultProperty = Property()
        defaultProperty.layoutResource = R.layout.default_view
        defaultProperty.dateTextViewResource = R.id.calendar_textview
        descHashMap["default"] = defaultProperty

        val currentProperty = Property()
        currentProperty.layoutResource = R.layout.current_view
        currentProperty.dateTextViewResource = R.id.calendar_textview
        descHashMap["current"] = currentProperty

        val eventProperty = Property()
        eventProperty.layoutResource = R.layout.event_view
        eventProperty.dateTextViewResource = R.id.calendar_textview
        descHashMap["event"] = eventProperty

//        val blankProperty = Property()
//        blankProperty.layoutResource = R.layout.blank_view
//        blankProperty.dateTextViewResource = R.id.calendar_textview
//        descHashMap["blank"] = blankProperty

        customCalendar.setMapDescToProp(descHashMap)
        dateHashMap[calendar.get(Calendar.DAY_OF_MONTH)] = "current"

        // set date
        dateHashMap.putAll(updateCalenderMonth(calendar.get(Calendar.MONTH)))
        customCalendar.setDate(calendar, dateHashMap)

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

    override fun onNavigationButtonClicked(
        whichButton: Int,
        newMonth: Calendar?
    ): Array<MutableMap<Int, Any>?> {
        val arr: Array<MutableMap<Int, Any>?> = arrayOfNulls(2)
        when (newMonth!![Calendar.MONTH]) {
            Calendar.JANUARY -> {
                arr[0] = updateCalenderMonth(Calendar.JANUARY)
            }
            Calendar.FEBRUARY -> {
                arr[0] = updateCalenderMonth(Calendar.FEBRUARY)
            }
            Calendar.MARCH -> {
                arr[0] = updateCalenderMonth(Calendar.MARCH)
            }
            Calendar.APRIL -> {
                arr[0] = updateCalenderMonth(Calendar.APRIL)
            }
            Calendar.MAY -> {
                arr[0] = updateCalenderMonth(Calendar.MAY)
            }
            Calendar.JUNE -> {
                arr[0] = updateCalenderMonth(Calendar.JUNE)
            }
            Calendar.JULY -> {
                arr[0] = updateCalenderMonth(Calendar.JULY)
            }
            Calendar.AUGUST -> {
                arr[0] = updateCalenderMonth(Calendar.AUGUST)
            }
            Calendar.SEPTEMBER -> {
                arr[0] = updateCalenderMonth(Calendar.SEPTEMBER)
            }
            Calendar.OCTOBER -> {
                arr[0] = updateCalenderMonth(Calendar.OCTOBER)
            }
            Calendar.NOVEMBER -> {
                arr[0] = updateCalenderMonth(Calendar.NOVEMBER)

            }
            Calendar.DECEMBER -> {
                arr[0] = updateCalenderMonth(Calendar.DECEMBER)
            }
        }
        return arr
    }

    private fun updateCalenderMonth(month: Int): HashMap<Int, Any> {
        val map = HashMap<Int, Any>()
        for (event in upcomingEventsTable) {
            val dateSplitted = event.date?.split("/")
            if (dateSplitted != null) {
                val dDay = dateSplitted[0].toInt()
                val dMonth = dateSplitted[1].toInt()
                val dYear = dateSplitted[2].toInt()

                val timeInMillis = customCalendar.selectedDate.timeInMillis
                val currentYear = dateParser.format(timeInMillis).split("/")[2].toInt()

//                Log.d("year: ", dYear.toString())

                if (month + 1 == dMonth && currentYear == dYear) {
                    map[dDay] = "event"
                }
            }
        }
        return map
    }
}