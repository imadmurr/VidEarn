package murr.imad.myapplication.payouts.ui.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import murr.imad.myapplication.R
import murr.imad.myapplication.payouts.data.model.GiftCard

/**
 * Adapter for displaying a list of [GiftCard] items in a GridView.
 *
 * @property giftCardList The list of [GiftCard] items to display.
 * @property context The context used to access system resources and layout inflaters.
 */
internal class GiftCardsAdapter(
    private val giftCardList: List<GiftCard>,
    private val context: Context
) : BaseAdapter() {

    private val inflater: LayoutInflater by lazy {
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int = giftCardList.size

    override fun getItem(position: Int): Any? {
        return giftCardList.getOrNull(position)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    /**
     * Returns a view for the item at the specified position.
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A view corresponding to the data at the specified position.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.gridview_item, parent, false)

        val giftCardImageView: ImageView = view.findViewById(R.id.gift_card_img)
        val giftCardTextView: TextView = view.findViewById(R.id.gift_card_text)

        val giftCard = getItem(position) as? GiftCard
        giftCard?.let {
            giftCardImageView.setImageResource(it.giftCardImg)
            giftCardTextView.text = it.giftCardName
        }

        return view
    }
}
