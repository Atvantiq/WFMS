package com.atvantiq.wfms.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.atvantiq.wfms.R
import com.atvantiq.wfms.databinding.CalendarViewBinding
import com.atvantiq.wfms.models.calendar.AttendanceDay
import com.atvantiq.wfms.models.calendar.AttendanceStatus
import com.atvantiq.wfms.ui.screens.adapters.CalendarAdapter
import com.atvantiq.wfms.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarView : LinearLayoutCompat {

    private lateinit var binding: CalendarViewBinding
    private var eventHandler: CustomCalendarEventHandler? = null
    private lateinit var calendarAdapter: CalendarAdapter
    private val calendar: Calendar = Calendar.getInstance()
    private val calendarToday = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initControl(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initControl(context, attrs)
    }


    /**
     * Load control xml layout
     */
    private fun initControl(context: Context, attrs: AttributeSet?) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.calendar_view,
            this,
            true
        )
        handleUI(context)
        calendarToday.time = Date()
        assignClickHandlers(context)
        updateCalendar(context)
    }

    private fun handleUI(context: Context) {
        calendarAdapter = CalendarAdapter(context){
            position, day ->
            Log.e("jaspal", "Selected Position:$position")
            Log.e("jaspal", "Selected Position:${day.date}")
            if(eventHandler!=null){
                eventHandler?.onDayClickListener(position,day)
            }
        }
        binding.calendarGrid.layoutManager = GridLayoutManager(context, 7)
        binding.calendarGrid.addItemDecoration(GridSpacingItemDecoration(7, Utils.dpToPx(4f,context), true))
        binding.calendarGrid.adapter = calendarAdapter
    }

    private fun isInvalidDate(): Boolean {
        val month = calendar[Calendar.MONTH] + 1
        val year = calendar[Calendar.YEAR]

        val currentMonth = calendarToday[Calendar.MONTH] + 1
        val currentYear = calendarToday[Calendar.YEAR]

        return month >= currentMonth || year >= currentYear
    }

    private fun assignClickHandlers(context: Context) {

        binding.btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            if (eventHandler != null) {
                updateCalendar(context)
                eventHandler?.onNextMonthClickListener(calendar)
            }
        }

        binding.btnPreviousMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1) // Move to the next month
            if (eventHandler != null) {
                updateCalendar(context)
                eventHandler?.onPrevMonthClickListener(calendar)
            }
        }
    }

    /**
     * Assign event handler to be passed needed events
     */
    fun setCustomCalendarEventHandler(eventHandler: CustomCalendarEventHandler?) {
        this.eventHandler = eventHandler
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    interface CustomCalendarEventHandler {
        fun onDayClickListener(position:Int,day:AttendanceDay)

        fun onPrevMonthClickListener(calendar: Calendar?)

        fun onNextMonthClickListener(calendar: Calendar?)
    }

    private fun updateCalendar(context: Context) {
        val monthDays = getMonthDays(calendar)
        calendarAdapter.addDays(monthDays)
        binding.tvMonthYear.text = dateFormat.format(calendar.time) // Set the month and year title
    }

/*
    private fun getMonthDays(calendar: Calendar): List<AttendanceDay> {
        val attendanceData = mutableListOf<AttendanceDay>()

        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the month

        // Get the day of the week for the first day of the month
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)

        // Calculate padding days before the first day of the month
        val paddingDays = if (firstDayOfWeek == Calendar.SUNDAY) 0 else firstDayOfWeek - 1

        // Add empty placeholders for padding
        for (i in 1..paddingDays) {
            attendanceData.add(AttendanceDay("", AttendanceStatus.NONE)) // Empty days
        }

        // Get the total number of days in the current month
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Populate the actual days with attendance data
        for (i in 1..daysInMonth) {
            val dayCalendar = tempCalendar.clone() as Calendar
            dayCalendar.set(Calendar.DAY_OF_MONTH, i)

            // Example: Alternate between PRESENT and ABSENT for demo
            val status = if (i % 2 == 0) AttendanceStatus.PRESENT else AttendanceStatus.ABSENT

            // Format date as "YYYY-MM-DD"
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(dayCalendar.time)

            attendanceData.add(AttendanceDay(dateString, status))
        }

        return attendanceData
    }
*/

    private fun getMonthDays(calendar: Calendar): List<AttendanceDay> {
        val attendanceData = mutableListOf<AttendanceDay>()

        // Clone calendar to manipulate dates without affecting the original instance
        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the month

        // Get the day of the week for the first day of the current month
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)

        // Calculate padding days before the first day of the month
        val paddingDays = if (firstDayOfWeek == Calendar.SUNDAY) 0 else firstDayOfWeek - 1

        // Add previous month's days for padding
        if (paddingDays > 0) {
            val previousMonthCalendar = tempCalendar.clone() as Calendar
            previousMonthCalendar.add(Calendar.MONTH, -1) // Move to the previous month
            val lastDayOfPreviousMonth = previousMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            // Add days from the previous month
            for (i in (lastDayOfPreviousMonth - paddingDays + 1)..lastDayOfPreviousMonth) {
                attendanceData.add(
                    AttendanceDay(
                        date = "",
                        status = AttendanceStatus.NONE // Gray color for these dates
                    )
                )
            }
        }

        // Get the total number of days in the current month
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Populate the actual days with attendance data
        for (i in 1..daysInMonth) {
            val dayCalendar = tempCalendar.clone() as Calendar
            dayCalendar.set(Calendar.DAY_OF_MONTH, i)

            val status = when {
                i % 2 == 0 -> AttendanceStatus.PRESENT
                i % 3 == 0 ->  AttendanceStatus.ABSENT
                else -> AttendanceStatus.IDLE
            }

            // Format date as "YYYY-MM-DD"
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(dayCalendar.time)

            attendanceData.add(AttendanceDay(dateString, status))
        }

        // Add next month's dates to fill the calendar grid
        val totalCells = 42 // 6 rows * 7 columns
        val remainingDays = totalCells - attendanceData.size
        if (remainingDays > 0) {
            for (i in 1..remainingDays) {
                attendanceData.add(
                    AttendanceDay(
                        date = "",
                        status = AttendanceStatus.NONE // Gray color for these dates
                    )
                )
            }
        }

        return attendanceData
    }


}