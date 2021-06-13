package stanislav.kleinikov.exchangerate.presentation.main

import android.annotation.SuppressLint
import stanislav.kleinikov.exchangerate.domain.Rate
import stanislav.kleinikov.exchangerate.presentation.adapter.BaseListItem
import stanislav.kleinikov.exchangerate.presentation.adapter.RateHeaderListItem
import stanislav.kleinikov.exchangerate.presentation.adapter.RateListItem
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

interface RatesMapper {
    fun getUiModel(rates: List<Rate>): List<BaseListItem>
}

class RatesMapperImpl : RatesMapper {

    @SuppressLint("ConstantLocale")
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun getUiModel(rates: List<Rate>): List<BaseListItem> {

        val dates = rates.groupBy { it.date }.toSortedMap()
            .map { it.key }
            .run { getOrNull(0) to getOrNull(1) }

        val header = RateHeaderListItem(
            firstDate = formatDate(dates.first?.time),
            secondDate = formatDate(dates.second?.time)
        )

        val items = mutableListOf<BaseListItem>(header)

        rates.distinctBy { it.id }
            .associateWith { rate ->
                rates.filter { it.id == rate.id }
                    .associateBy { it.date }
                    .mapValues { it.value.rate }
            }.map { (rate, rateByDate) ->
                RateListItem(
                    charCode = rate.charCode,
                    name = rate.name,
                    scale = rate.scale.toString(),
                    firstRate = formatRate(rateByDate[dates.first]),
                    secondRate = formatRate(rateByDate[dates.second])
                )
            }.also { items.addAll(it) }

        return items
    }

    private fun formatDate(date: Date?): String {
        return if (date == null) "-" else dateFormatter.format(date)
    }

    private fun formatRate(rate: BigDecimal?): String {
        return if (rate == null) "=" else String.format(Locale.getDefault(), "%.4f", rate)
    }
}
