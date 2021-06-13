package stanislav.kleinikov.exchangerate.data.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

import java.math.BigDecimal

class BigDecimalAdapter {

    @FromJson
    fun fromJson(string: String): BigDecimal = BigDecimal(string)

    @ToJson
    fun toJson(value: BigDecimal): String = value.toString()
}
