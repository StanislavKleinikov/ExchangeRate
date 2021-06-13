package stanislav.kleinikov.exchangerate.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class BaseRecyclerAdapter(
    private var items: List<BaseListItem> = emptyList(),
    private val adapterType: Type = Type.NORMAL
) : RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder>() {

    private val itemTouchHelper: ItemTouchHelper? =
        if (adapterType == Type.DRAGGABLE) ItemTouchHelper(ItemTouchCallback()) else null

    enum class Type {
        NORMAL, DRAGGABLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (adapterType) {
            Type.NORMAL -> BaseViewHolder(itemView)
            Type.DRAGGABLE -> DraggableViewHolder(itemTouchHelper, itemView)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].getLayoutId()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        itemTouchHelper?.attachToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        itemTouchHelper?.attachToRecyclerView(null)
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }


    open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @CallSuper
        open fun bind(item: BaseListItem) {
            item.bind(itemView)
        }
    }

    class DraggableViewHolder(private val itemTouchHelper: ItemTouchHelper?, itemView: View) :
        BaseViewHolder(itemView) {

        @SuppressLint("ClickableViewAccessibility")
        override fun bind(item: BaseListItem) {
            super.bind(item)
            val anchor = if (item is DraggableListItem) {
                val anchorId = item.getAnchorId()
                if (anchorId != -1) itemView.findViewById(anchorId) else itemView
            } else {
                itemView
            }
            anchor.setOnTouchListener { _: View, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper?.startDrag(this)
                }
                false
            }
        }
    }

    private class ItemTouchCallback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return if (recyclerView.layoutManager is GridLayoutManager) {
                val dragFlags =
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags = 0
                makeMovementFlags(dragFlags, swipeFlags)
            } else {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                makeMovementFlags(dragFlags, swipeFlags)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            viewHolder1: RecyclerView.ViewHolder
        ): Boolean {
            (recyclerView.adapter as BaseRecyclerAdapter).onItemMove(
                viewHolder.bindingAdapterPosition,
                viewHolder1.bindingAdapterPosition
            )
            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
            // not implemented
        }
    }
}