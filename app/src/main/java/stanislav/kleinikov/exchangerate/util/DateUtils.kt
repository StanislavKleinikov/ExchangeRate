package stanislav.kleinikov.exchangerate.util

import java.util.*

fun Date.toCalendar(): Calendar {
    return Calendar.getInstance().apply { time = this@toCalendar }
}

fun Calendar.copy(): Calendar {
    return clone() as Calendar
}

fun Calendar.modified(field: Int, amount: Int): Calendar {
    return apply { add(field, amount) }
}