package murr.imad.myapplication.payouts.ui.view.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import murr.imad.myapplication.R
import murr.imad.myapplication.shared.ui.view.BaseActivity
import murr.imad.myapplication.shared.ui.view.MainActivity.Companion.USER_DATA_MODEL
import murr.imad.myapplication.databinding.ActivityMyPayoutsBinding
import murr.imad.myapplication.auth.data.model.User
import murr.imad.myapplication.payouts.data.model.CryptoPayout
import murr.imad.myapplication.payouts.data.model.GiftCardPayout
import murr.imad.myapplication.payouts.ui.view.adapters.CryptoPayoutsAdapter
import murr.imad.myapplication.payouts.ui.view.adapters.GiftCardsPayoutsAdapter
import murr.imad.myapplication.payouts.ui.viewmodel.MyPayoutsViewModel
import murr.imad.myapplication.utils.Constants

/**
 * Activity that displays the list of payouts requested by the user.
 * Allows users to view and cancel their cryptocurrency or gift card payouts.
 */
@Suppress("DEPRECATION")
class MyPayoutsActivity : BaseActivity() {

    private val viewModel: MyPayoutsViewModel by viewModels()
    private lateinit var binding: ActivityMyPayoutsBinding
    private lateinit var user: User

    /**
     * Called when the activity is starting. Initializes the activity and sets up the UI components.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied in [onSaveInstanceState].
     * Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPayoutsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        user = intent.extras?.getParcelable(USER_DATA_MODEL) ?: return

        setupSpinner()
        observePayouts()
        observeCancelResult()
    }

    /**
     * Sets up the action bar with a custom title and a back button.
     */
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMyPayoutsActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = getString(R.string.my_payouts)
        }
        binding.toolbarMyPayoutsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Sets up the dropdown spinner for selecting the type of payouts to display
     * (cryptocurrency payouts or gift card payouts).
     */
    private fun setupSpinner() {
        val options = resources.getStringArray(R.array.My_payouts_array)
        val adapter = ArrayAdapter(this, R.layout.network_spinner_item, options)
        binding.myPayoutsSpinner.adapter = adapter

        binding.myPayoutsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = options[position]
                fetchPayoutsList(selectedOption)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action required
            }
        }
    }

    /**
     * Fetches the list of payouts based on the selected type from the spinner.
     *
     * @param option The type of payouts selected ("Cryptocurrency Payouts" or "Gift-Cards Payouts").
     */
    private fun fetchPayoutsList(option: String) {
        showProgressDialog("Fetching Payouts..")
        viewModel.fetchPayouts(option, user)
    }

    /**
     * Observes changes in the cryptocurrency and gift card payout lists from the ViewModel
     * and updates the UI accordingly.
     */
    private fun observePayouts() {
        viewModel.cryptoPayouts.observe(this) { payoutsList ->
            hideProgressDialog()
            populatePayoutsList(Constants.CRYPTO_PAYMENTS, payoutsList)
        }
        viewModel.giftCardPayouts.observe(this) { payoutsList ->
            hideProgressDialog()
            populatePayoutsList(Constants.GIFT_CARDS_PAYMENTS, payoutsList)
        }
    }

    /**
     * Observes the result of the payout cancellation operation and handles the success or failure.
     */
    private fun observeCancelResult() {
        viewModel.cancelResult.observe(this) { result ->
            hideProgressDialog()
            result.onSuccess { onPaymentCancelled() }
                .onFailure { showErrorSnackBar("Error cancelling payout") }
        }
    }

    /**
     * Populates the RecyclerView with the list of payouts depending on the selected type.
     *
     * @param option The type of payouts (Cryptocurrency or Gift Cards).
     * @param payoutsList The list of payouts to display.
     */
    private fun populatePayoutsList(option: String, payoutsList: List<*>) {
        with(binding.rvPayoutsList) {
            layoutManager = LinearLayoutManager(this@MyPayoutsActivity)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(this@MyPayoutsActivity, RecyclerView.VERTICAL))
        }

        when (option) {
            Constants.CRYPTO_PAYMENTS -> setupCryptoPayoutsAdapter(payoutsList as List<CryptoPayout>)
            Constants.GIFT_CARDS_PAYMENTS -> setupGiftCardPayoutsAdapter(payoutsList as List<GiftCardPayout>)
        }
    }

    /**
     * Sets up the adapter for the cryptocurrency payouts and handles item click events.
     *
     * @param payoutsList The list of cryptocurrency payouts to display.
     */
    private fun setupCryptoPayoutsAdapter(payoutsList: List<CryptoPayout>) {
        if (payoutsList.isNotEmpty()) {
            binding.rvPayoutsList.visibility = View.VISIBLE
            binding.tvNoPayouts.visibility = View.GONE

            val adapter = CryptoPayoutsAdapter(this, payoutsList)
            binding.rvPayoutsList.adapter = adapter

            adapter.setOnClickListener(object : CryptoPayoutsAdapter.OnClickListener {
                override fun onClick(position: Int, model: CryptoPayout) {
                    if (!model.paid) {
                        showDeletePayoutDialog(model, true)
                    } else {
                        showErrorSnackBar("This payout is paid and cannot be refunded!")
                    }
                }
            })
        } else {
            binding.rvPayoutsList.visibility = View.GONE
            binding.tvNoPayouts.visibility = View.VISIBLE
        }
    }

    /**
     * Sets up the adapter for the gift card payouts and handles item click events.
     *
     * @param payoutsList The list of gift card payouts to display.
     */
    private fun setupGiftCardPayoutsAdapter(payoutsList: List<GiftCardPayout>) {
        if (payoutsList.isNotEmpty()) {
            binding.rvPayoutsList.visibility = View.VISIBLE
            binding.tvNoPayouts.visibility = View.GONE

            val adapter = GiftCardsPayoutsAdapter(this, payoutsList)
            binding.rvPayoutsList.adapter = adapter

            adapter.setOnClickListener(object : GiftCardsPayoutsAdapter.OnClickListener {
                override fun onClick(position: Int, model: GiftCardPayout) {
                    if (!model.paid) {
                        showDeletePayoutDialog(model, false)
                    } else {
                        showErrorSnackBar("This payout is paid and cannot be refunded!")
                    }
                }
            })
        } else {
            binding.rvPayoutsList.visibility = View.GONE
            binding.tvNoPayouts.visibility = View.VISIBLE
        }
    }

    /**
     * Shows a confirmation dialog to the user before canceling the payout request.
     *
     * @param model The payout to be canceled, either a `CryptoPayout` or `GiftCardPayout`.
     * @param isCrypto Boolean indicating if the payout is a cryptocurrency payout.
     */
    private fun showDeletePayoutDialog(model: Any, isCrypto: Boolean) {
        AlertDialog.Builder(this).apply {
            setTitle("Cancel Payout Request")
            setMessage("Are you sure you want to cancel this payout request? There is a 500 points penalty")
            setIcon(R.drawable.ic_baseline_warning_24)
            setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                showProgressDialog("Canceling payout request...")
                viewModel.cancelPayout(model, user, isCrypto)
            }
            setNegativeButton("No") { dialogInterface, _ -> dialogInterface.dismiss() }
            setCancelable(false)
        }.create().show()
    }

    /**
     * Handles the successful cancellation of a payout by showing a message to the user.
     */
    private fun onPaymentCancelled() {
        hideProgressDialog()
        showErrorSnackBar("Payout cancelled and points refunded.")
    }
}
