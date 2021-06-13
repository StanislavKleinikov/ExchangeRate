package stanislav.kleinikov.exchangerate.presentation.adapter

import android.view.View
import stanislav.kleinikov.exchangerate.R
import stanislav.kleinikov.exchangerate.databinding.ListItemRateHeaderBinding

class RateHeaderListItem(
    private val firstDate: String, private val secondDate: String
) : BaseListItem {

    override fun getLayoutId(): Int = R.layout.list_item_rate_header

    override fun bind(view: View) {
        with(ListItemRateHeaderBinding.bind(view)) {
            firstDateTv.text = firstDate
            secondDateTv.text = secondDate
        }
    }
}