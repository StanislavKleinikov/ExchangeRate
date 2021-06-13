package stanislav.kleinikov.exchangerate.data

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import io.reactivex.Completable
import io.reactivex.Single
import stanislav.kleinikov.exchangerate.R
import stanislav.kleinikov.exchangerate.domain.Preference
import stanislav.kleinikov.exchangerate.domain.PreferenceRepository

private const val PREFERENCES_NAME = "rates_preferences"

class PreferenceRepositoryImpl(context: Context) : PreferenceRepository {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    init {
        PreferenceManager.setDefaultValues(
            context,
            PREFERENCES_NAME,
            Context.MODE_PRIVATE,
            R.xml.currency_preferences,
            false
        )
    }

    override fun getPreferences(): Single<List<Preference>> {
        return Single.fromCallable {
            preferences.all.map { (key, value) ->
                Preference(charCode = key, isActive = value as Boolean)
            }
        }
    }

    override fun setPreferences(preferences: List<Preference>): Completable {
        return Completable.fromCallable {
            this.preferences.edit {
                preferences.forEach { preference ->
                    putBoolean(preference.charCode, preference.isActive)
                }
            }
        }
    }
}