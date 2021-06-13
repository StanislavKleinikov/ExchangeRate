package stanislav.kleinikov.exchangerate.domain

import io.reactivex.Completable
import io.reactivex.Single

interface PreferenceRepository {
    fun getPreferences(): Single<List<Preference>>
    fun setPreferences(preferences: List<Preference>): Completable
}