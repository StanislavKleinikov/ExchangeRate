package stanislav.kleinikov.exchangerate.presentation.adapter

import android.view.View
import stanislav.kleinikov.exchangerate.R
import stanislav.kleinikov.exchangerate.databinding.ListItemPreferenceBinding
import stanislav.kleinikov.exchangerate.domain.Preference
import stanislav.kleinikov.exchangerate.presentation.settings.OnStartDragListener

class PreferenceListItem(private val preference: Preference) : DraggableListItem {

    override fun getLayoutId(): Int = R.layout.list_item_preference

    override fun getAnchorId(): Int = R.id.anchor_iv

    override fun bind(view: View) {
        with(ListItemPreferenceBinding.bind(view)) {
            charCodeTv.text = preference.charCode
            visibilitySc.isChecked = preference.isActive
        }
    }
}