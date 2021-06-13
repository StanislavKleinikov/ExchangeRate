package stanislav.kleinikov.exchangerate.domain

import java.math.BigDecimal
import java.util.*

class Rate(
    val id: Int,
    val date: Calendar,
    val charCode: String,
    val scale: Int,
    val name: String,
    val rate: BigDecimal
)