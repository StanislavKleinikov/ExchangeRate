package stanislav.kleinikov.exchangerate.domain

import io.reactivex.Scheduler
import io.reactivex.Single
import stanislav.kleinikov.exchangerate.util.copy
import stanislav.kleinikov.exchangerate.util.modified
import java.util.*

interface GetRatesUseCase {
    fun getRates(): Single<List<Rate>>
}

class GetRatesUseCaseImpl(
    private val ratesRepository: RatesRepository,
    private val preferenceRepository: PreferenceRepository,
    private val subscribeScheduler: Scheduler,
    private val observeScheduler: Scheduler,
) : GetRatesUseCase {

    override fun getRates(): Single<List<Rate>> {
        val date = Calendar.getInstance()
        return preferenceRepository.getPreferences()
            .flatMap { preferences ->
                Single.zip(
                    ratesRepository.getRates(date)
                        .map { rates -> filterRates(rates, preferences) },
                    ratesRepository.getRates(date.copy().modified(Calendar.DAY_OF_YEAR, 1))
                        .onErrorResumeNext {
                            ratesRepository.getRates(date.copy().modified(Calendar.DAY_OF_YEAR, -1))
                        }
                        .map { rates -> filterRates(rates, preferences) },
                ) { dailyRates1, dailyRates2 ->
                    dailyRates1 + dailyRates2
                }.flatMap { rates ->
                    updatePreferences(preferences, rates)
                }
            }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
    }

    private fun filterRates(rates: List<Rate>, preferences: List<Preference>): List<Rate> {
        return rates.filter { rate ->
            preferences.find { it.charCode == rate.charCode }?.isActive ?: true
        }
    }

    private fun updatePreferences(old: List<Preference>, data: List<Rate>): Single<List<Rate>> {
        val new = data.filter { rate ->
            data.any { it.charCode == rate.charCode }
        }.map { rate ->
            Preference(charCode = rate.charCode, isActive = true)
        }

        return if (new.isNotEmpty()) {
            return preferenceRepository.setPreferences(old + new)
                .toSingle { data }
        } else {
            Single.just(data)
        }
    }
}