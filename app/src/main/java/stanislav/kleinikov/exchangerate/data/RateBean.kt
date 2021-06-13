package stanislav.kleinikov.exchangerate.data

import com.squareup.moshi.Json
import java.math.BigDecimal
import java.util.*

data class RateBean(
    @Json(name = "Cur_ID")
    val id: Int,
    @Json(name = "Date")
    val date: Calendar,
    @Json(name = "Cur_Abbreviation")
    val charCode: String,
    @Json(name = "Cur_Scale")
    val scale: Int,
    @Json(name = "Cur_Name")
    val name: String,
    @Json(name = "Cur_OfficialRate")
    val rate: BigDecimal
)
