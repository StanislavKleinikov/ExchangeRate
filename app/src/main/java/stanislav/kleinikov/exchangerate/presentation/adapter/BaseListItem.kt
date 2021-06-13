package stanislav.kleinikov.exchangerate.presentation.adapter

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import stanislav.kleinikov.exchangerate.presentation.settings.OnStartDragListener

interface BaseListItem {
    @LayoutRes
    fun getLayoutId(): Int
    fun bind(view: View)
}

interface DraggableListItem : BaseListItem {
    @IdRes
    fun getAnchorId(): Int
}