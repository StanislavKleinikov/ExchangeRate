package stanislav.kleinikov.exchangerate.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import stanislav.kleinikov.exchangerate.domain.PreferenceRepository
import stanislav.kleinikov.exchangerate.domain.Resource
import stanislav.kleinikov.exchangerate.presentation.AppViewModel
import stanislav.kleinikov.exchangerate.presentation.adapter.BaseListItem

class SettingsViewModel(
    preferenceRepository: PreferenceRepository,
    private val settingsMapper: SettingsMapper
) : AppViewModel() {

    private val _preferences: MutableLiveData<Resource<List<BaseListItem>>> = MutableLiveData()

    val preferences: LiveData<Resource<List<BaseListItem>>> = _preferences

    init {
        preferenceRepository.getPreferences()
            .map { settingsMapper.getUiModel(it) }
            .subscribeBy(
                onSuccess = { _preferences.postValue(Resource.Success(it)) },
                onError = { _preferences.postValue(Resource.Error(it)) }
            ).addTo(compositeDisposable)
    }

    fun applyChanges() {
        // TODO()
    }
}