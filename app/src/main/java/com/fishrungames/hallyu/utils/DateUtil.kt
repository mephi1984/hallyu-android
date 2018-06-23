package com.fishrungames.hallyu.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    private val MONDAY_SHORT = "пн"
    private val TUESDAY_SHORT = "вт"
    private val WEDNESDAY_SHORT = "ср"
    private val THURSDAY_SHORT = "чт"
    private val FRIDAY_SHORT = "пт"
    private val SATURDAY_SHORT = "сб"
    private val SUNDAY_SHORT = "вс"

    val DAYS_OF_WEEK_REDUCED = arrayOf(SUNDAY_SHORT, MONDAY_SHORT, TUESDAY_SHORT, WEDNESDAY_SHORT, THURSDAY_SHORT, FRIDAY_SHORT, SATURDAY_SHORT)

    private val yesterday: Calendar
        get() {
            val yesterday = Calendar.getInstance()
            yesterday.time = Date()
            yesterday.add(Calendar.DATE, -1)
            return yesterday
        }

    fun getFormattedDate(dateAsString: String): String {
        val date = tryFormat(dateAsString)
        return getFormattedDate(date)
    }

    private fun tryFormat(dateAsString: String): Date {
        val dateFormat = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
        try {
            return dateFormat.parse(dateAsString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return Date()
    }

    fun getFormattedDate(date: Date): String {
        val stringBuilder = StringBuilder()
        val hourMinutesFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val hourMinutes = hourMinutesFormat.format(date)
        val callCalendar = Calendar.getInstance()
        callCalendar.time = date
        stringBuilder.append(defineDate(date))
        stringBuilder.append(',')
        stringBuilder.append(' ')
        stringBuilder.append(defineDayOfWeek(callCalendar))
        stringBuilder.append(' ')
        stringBuilder.append(hourMinutes)
        return stringBuilder.toString()
    }

    private fun defineDayOfWeek(callCalendar: Calendar): String {
        val dayOfWeekId = callCalendar.get(Calendar.DAY_OF_WEEK)
        return getShortDayOfWeek(dayOfWeekId)
    }

    private fun getShortDayOfWeek(id: Int): String {
        return DAYS_OF_WEEK_REDUCED[id - 1]
    }

    private fun defineDate(callDate: Date): String {
        val dayMonthYearFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val callCalendar = getCalendar(callDate)

        val dayMonthYear: String
        if (isCallDateIsToday(callCalendar))
            dayMonthYear = "Сегодня"
        else if (isCallDateIsYesterday(callCalendar))
            dayMonthYear = "Вчера"
        else
            dayMonthYear = dayMonthYearFormat.format(callDate)

        return dayMonthYear
    }

    private fun getCalendar(date: Date): Calendar {
        val callCalendar = Calendar.getInstance()
        callCalendar.time = date
        return callCalendar
    }

    private fun isCallDateIsToday(callCalendar: Calendar): Boolean {
        val today = Calendar.getInstance()
        today.time = Date()
        return today.get(Calendar.YEAR) == callCalendar.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == callCalendar.get(Calendar.DAY_OF_YEAR)
    }

    private fun isCallDateIsYesterday(callCalendar: Calendar): Boolean {
        val yesterday = yesterday
        return yesterday.get(Calendar.YEAR) == callCalendar.get(Calendar.YEAR) && yesterday.get(Calendar.DAY_OF_YEAR) == callCalendar.get(Calendar.DAY_OF_YEAR)
    }
}