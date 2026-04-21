package murr.imad.videarn.payouts.ui.view.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import murr.imad.videarn.R
import murr.imad.videarn.databinding.CustomDialogBinding
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.payouts.ui.viewmodel.CryptoPayoutViewModel

/**
 * DialogFragment for verifying crypto address and confirming the payout.
 */
@Suppress("DEPRECATION")
class CryptoAddressVerificationDialog : DialogFragment() {

    private var _binding: CustomDialogBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel with the parent Activity
    private lateinit var viewModel: CryptoPayoutViewModel

    // Arguments passed to the dialog
    private var amount: String? = null
    private var address: String? = null
    private var coin: String? = null
    private var network: String? = null
    private var user: User? = null

    /**
     * This is where you can access the activity's ViewModel safely.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Initialize the ViewModel
        viewModel = activityViewModels<CryptoPayoutViewModel>().value
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrieveArguments()
    }

    /**
     * Inflates the dialog's layout and sets the background drawable.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CustomDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        return binding.root
    }

    /**
     * Adjusts the dialog's size when it starts.
     */
    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Sets up the dialog's UI components and their click listeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvWarningAddress.text = address

        binding.btnWarningPayment.setOnClickListener {
            confirmPayout()
            dialog?.cancel()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    /**
     * Cleans up the binding when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Retrieves the arguments passed to the dialog and stores them in member variables.
     */
    private fun retrieveArguments() {
        arguments?.let {
            amount = it.getString(ARG_AMOUNT)
            address = it.getString(ARG_ADDRESS)
            coin = it.getString(ARG_COIN)
            network = it.getString(ARG_NETWORK)
            user = it.getParcelable(ARG_USER)
        }
    }

    /**
     * Confirms the payout by calling the ViewModel's method with the provided details.
     */
    private fun confirmPayout() {
        amount?.let { amt ->
            address?.let { addrs ->
                coin?.let { c ->
                    network?.let { net ->
                        viewModel.addCryptoPayout(amt, addrs, c, net)
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_AMOUNT = "amount"
        private const val ARG_ADDRESS = "address"
        private const val ARG_COIN = "coin"
        private const val ARG_NETWORK = "network"
        private const val ARG_USER = "user"

        /**
         * Creates a new instance of the dialog with the given payout details.
         *
         * @param amount The amount to be paid out.
         * @param address The crypto address to send the payout.
         * @param coin The type of cryptocurrency.
         * @param network The network to use for the payout.
         * @param user The user requesting the payout.
         * @return A new instance of CryptoAddressVerificationDialog.
         */
        @JvmStatic
        fun newInstance(amount: String, address: String, coin: String, network: String, user: User) =
            CryptoAddressVerificationDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_AMOUNT, amount)
                    putString(ARG_ADDRESS, address)
                    putString(ARG_COIN, coin)
                    putString(ARG_NETWORK, network)
                    putParcelable(ARG_USER, user)
                }
            }
    }
}
