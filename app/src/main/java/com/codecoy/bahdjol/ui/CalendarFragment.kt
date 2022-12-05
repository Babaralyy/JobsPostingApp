package com.codecoy.bahdjol.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.EventDay
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.HistoryAdapter
import com.codecoy.bahdjol.callback.HistoryCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentCalendarBinding
import com.codecoy.bahdjol.datamodels.BookingHistoryData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment(), HistoryCallback {

    private lateinit var myViewModel: MyViewModel

    private lateinit var bookingHistoryList: MutableList<BookingHistoryData>
    private lateinit var filteredHistoryList: MutableList<BookingHistoryData>
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var date: String

    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentCalendarBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentCalendarBinding.inflate(inflater)

        inIt()

        return mBinding.root

    }

    private fun inIt() {

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel = ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        bookingHistoryList = arrayListOf()
        filteredHistoryList = arrayListOf()


        mBinding.rvBooking.setHasFixedSize(true)
        mBinding.rvBooking.layoutManager = LinearLayoutManager(requireActivity())
        if (activity.isNetworkConnected()){
            getBookingHistory()
        } else {
            Toast.makeText(activity, "Connect to the internet and try again", Toast.LENGTH_SHORT).show()
        }


        mBinding.calendarView.setOnDayClickListener {

            val clickedDayCalendar: Calendar = it.calendar
            Log.i(TAG, "onDayClick: $clickedDayCalendar")


            val format = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
            date = format.format(clickedDayCalendar.time)

            Log.i(TAG, "onDayClick: date:$date")

            bookingAgainstDate(date)
        }

        mBinding.toolBar.setNavigationOnClickListener {
            replaceFragment(ServicesFragment())
        }

    }


    private fun getBookingHistory() {

        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        myViewModel.bookingHistory(ServiceIds.userId!!)

        myViewModel.bookingHistoryLiveData.observe(
            activity
        ) {

            dialog.dismiss()

            Log.i(TAG, "response: outer ${it.data.size}")

            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(TAG, "response: success ${it.data.size}")

                bookingHistoryList = it.data

                setBookingsOnCalendar(bookingHistoryList as ArrayList<BookingHistoryData>)

            } else {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setBookingsOnCalendar(bookingHistoryList: ArrayList<BookingHistoryData>) {

        val bookingDays: MutableList<EventDay> = arrayListOf()
        val selectedDates: MutableList<Calendar> = arrayListOf()

        for (item in bookingHistoryList) {
            Log.i(
                TAG,
                "setEventsOnCalendar: test: date:" + item.date
            )
            val format = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
            var d: Date? = null
            try {
                d = format.parse(item.date.toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val c = Calendar.getInstance()
            if (d != null) {
                c.time = d
            }
            selectedDates.add(c)
        }

        for (calendar in selectedDates) {
            if (Collections.frequency(selectedDates, calendar) == 1) {
                val eventDay2 = EventDay(calendar, R.drawable.one)
                bookingDays.add(eventDay2)
            } else if (Collections.frequency(selectedDates, calendar) == 2) {
                val eventDay2 = EventDay(calendar, R.drawable.two)
                bookingDays.add(eventDay2)
            } else if (Collections.frequency(selectedDates, calendar) == 3) {
                val eventDay2 = EventDay(calendar, R.drawable.three)
                bookingDays.add(eventDay2)
            } else {
                val eventDay2 = EventDay(calendar, R.drawable.multiple)
                bookingDays.add(eventDay2)
            }
        }

        mBinding.calendarView.setEvents(bookingDays)


    }

    private fun bookingAgainstDate(selectedDate: String) {

        this.filteredHistoryList = bookingHistoryList.filter {
            it.date == selectedDate
        } as MutableList<BookingHistoryData>

        if (this.filteredHistoryList.isEmpty()){

            Toast.makeText(activity, "Booking does not exist!", Toast.LENGTH_SHORT).show()
            filteredHistoryList.clear()

            historyAdapter = HistoryAdapter(activity, filteredHistoryList, this)
            mBinding.rvBooking.adapter = historyAdapter


        } else {

            historyAdapter = HistoryAdapter(activity, filteredHistoryList, this)
            mBinding.rvBooking.adapter = historyAdapter

        }

    }

    override fun onHistoryClick(position: Int, bookingHistoryData: BookingHistoryData) {

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLay, fragment)
        fragmentTransaction.commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }


}