package stanislav.kleinikov.exchangerate.data.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import stanislav.kleinikov.exchangerate.util.toCalendar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws


class CalendarAdapter {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())

    @FromJson
    @Synchronized
    @Throws(ParseException::class)
    fun fromJson(string: String): Calendar = dateFormatter.parse(string)!!.toCalendar()

    @ToJson
    @Synchronized
    fun toJson(date: Calendar): String = dateFormatter.format(date.time)
}
