package stanislav.kleinikov.exchangerate.presentation.settings

import stanislav.kleinikov.exchangerate.domain.Preference
import stanislav.kleinikov.exchangerate.presentation.adapter.BaseListItem
import stanislav.kleinikov.exchangerate.presentation.adapter.PreferenceListItem

interface SettingsMapper {
    fun getUiModel(preferences: List<Preference>): List<BaseListItem>
}

class SettingsMapperImpl : SettingsMapper {

    override fun getUiModel(preferences: List<Preference>): List<BaseListItem> {
        return preferences.map { PreferenceListItem(it) }
    }
}