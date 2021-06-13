package stanislav.kleinikov.exchangerate.presentation.adapter

import android.view.View
import stanislav.kleinikov.exchangerate.R
import stanislav.kleinikov.exchangerate.databinding.ListItemRateBinding
import java.math.BigDecimal
import java.util.*

class RateListItem(
    private val charCode: String,
    private val name: String,
    private val scale: String,
    private val firstRate: String,
    private val secondRate: String
) : BaseListItem {

    override fun getLayoutId(): Int = R.layout.list_item_rate

    override fun bind(view: View) {
        val context = view.context
        with(ListItemRateBinding.bind(view)) {
            charCodeTv.text = charCode
            scaleTv.text = context.getString(R.string.format_scale, scale, name)
            firstRateTv.text = firstRate
            secondRateTv.text = secondRate
        }
    }
}