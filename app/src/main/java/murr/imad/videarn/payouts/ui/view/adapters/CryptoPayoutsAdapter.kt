package murr.imad.videarn.payouts.ui.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import murr.imad.videarn.R
import murr.imad.videarn.databinding.ItemCryptoPaymentBinding
import murr.imad.videarn.payouts.data.model.CryptoPayout

class CryptoPayoutsAdapter(
    private val context: Context,
    private var list: List<CryptoPayout>
) : RecyclerView.Adapter<CryptoPayoutsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    // Define the ViewHolder to use ViewBinding
    class MyViewHolder(val binding: ItemCryptoPaymentBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnClickListener {
        fun onClick(position: Int, model: CryptoPayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCryptoPaymentBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        with(holder.binding) {
            tvAmount.text = "Amount: %.2f$".format(model.amount / 10000.0)
            tvCoinAndNetwork.text = "Coin: ${model.coin} - ${model.network}"
            tvAddress.text = "Address: ${model.address}"

            if (model.paid) {
                tvCryptoStatus.setBackgroundResource(R.drawable.shape_badge_green_background)
                tvCryptoStatus.text = context.getString(R.string.paid_out)
            } else {
                tvCryptoStatus.setBackgroundResource(R.drawable.shape_badge_red_background)
                tvCryptoStatus.text = context.getString(R.string.pending)
            }

            when (model.coin) {
                "USDT" -> ivPaymentImage.setImageResource(R.drawable.tether)
                "BTC" -> ivPaymentImage.setImageResource(R.drawable.bitcoin2)
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
