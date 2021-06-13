package stanislav.kleinikov.exchangerate.domain

import io.reactivex.Single
import java.util.*

interface RatesRepository {
    fun getRates(date: Calendar): Single<List<Rate>>
}