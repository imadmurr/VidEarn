package murr.imad.videarn.payouts.ui.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import murr.imad.videarn.R
import murr.imad.videarn.databinding.ItemGiftCardPaymentBinding
import murr.imad.videarn.payouts.data.model.GiftCardPayout

class GiftCardsPayoutsAdapter(
    private val context: Context,
    private var list: List<GiftCardPayout>
) : RecyclerView.Adapter<GiftCardsPayoutsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(position: Int, model: GiftCardPayout)
    }

    // Define ViewHolder with ViewBinding
    class MyViewHolder(val binding: ItemGiftCardPaymentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemGiftCardPaymentBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        with(holder.binding) {
            tvCardType.text = model.cardType
            tvCardEmail.text = "Email: ${model.email}"
            tvCardPrice.text = "Price: ${model.price}"

            if (model.paid) {
                tvGiftcardStatus.setBackgroundResource(R.drawable.shape_badge_green_background)
                tvGiftcardStatus.text = context.getString(R.string.paid_out)
            } else {
                tvGiftcardStatus.setBackgroundResource(R.drawable.shape_badge_red_background)
                tvGiftcardStatus.text = context.getString(R.string.pending)
            }

            when (model.cardType) {
                "Steam Wallet" -> ivGiftCardPaymentImage.setImageResource(R.drawable.steam)
                "Amazon Card" -> ivGiftCardPaymentImage.setImageResource(R.drawable.amazon)
                "Netflix Card" -> ivGiftCardPaymentImage.setImageResource(R.drawable.netflix)
                "Google Play" -> ivGiftCardPaymentImage.setImageResource(R.drawable.googleplay)
            }

            root.setOnClickListener {
                onClickListener?.onClick(position, model)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}
