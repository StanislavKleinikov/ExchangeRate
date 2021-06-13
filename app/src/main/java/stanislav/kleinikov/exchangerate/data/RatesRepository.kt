package stanislav.kleinikov.exchangerate.data

import io.reactivex.Single
import stanislav.kleinikov.exchangerate.domain.Rate
import stanislav.kleinikov.exchangerate.domain.RatesRepository
import java.util.*


class RatesRepositoryImpl : RatesRepository {

    override fun getRates(date: Calendar): Single<List<Rate>> {
        return NetworkService.getNbrbRates(date).map { rates ->
            rates.map { rate ->
                rate.mapToDomain()
            }
        }
    }

    private fun RateBean.mapToDomain(): Rate {
        return Rate(
            id = id,
            date = date,
            charCode = charCode,
            scale = scale,
            name = name,
            rate = rate
        )
    }
}