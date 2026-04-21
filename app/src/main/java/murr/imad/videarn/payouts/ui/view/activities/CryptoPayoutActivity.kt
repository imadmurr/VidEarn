package murr.imad.videarn.payouts.ui.view.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import murr.imad.videarn.R
import murr.imad.videarn.databinding.ActivityCryptoPayoutBinding
import murr.imad.videarn.payouts.ui.view.dialogs.CryptoAddressVerificationDialog
import murr.imad.videarn.payouts.ui.viewmodel.CryptoPayoutViewModel
import murr.imad.videarn.shared.ui.view.BaseActivity

/**
 * Activity for handling crypto payouts, allowing users to select a coin, network,
 * and enter the payout details before confirming.
 */
@Suppress("DEPRECATION")
class CryptoPayoutActivity : BaseActivity() {

    private lateinit var binding: ActivityCryptoPayoutBinding
    private val viewModel: CryptoPayoutViewModel = CryptoPayoutViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCryptoPayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupObservers()
        setupCoinSpinner()
        setupAddPayoutButton()
    }

    /**
     * Sets up the action bar with a back button and title.
     */
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCryptoPayoutActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = "Crypto Payout"
        }
        binding.toolbarCryptoPayoutActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Sets up the observers to update UI elements based on LiveData changes.
     */
    private fun setupObservers() {
        viewModel.userDetails.observe(this) { user ->
            binding.cryptoPymntAvailableBalance.text = user?.let {
                "Your balance: %,d points".format(
                    it.points)
            }
        }

        viewModel.cryptoPayoutStatus.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    onUserUpdatedSuccess()
                } else {
                    showErrorSnackBar("Failed to add payout")
                }
            } ?: showErrorSnackBar("An unexpected error occurred")
        }
    }

    /**
     * Sets up the coin spinner with available options and handles network loading on selection.
     */
    private fun setupCoinSpinner() {
        val coins = listOf("USDT", "BTC")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, coins).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.coinsSpinner.adapter = adapter
        binding.coinsSpinner.setSelection(0)
        viewModel.loadNetworksForCoin(coins[0])

        binding.coinsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.loadNetworksForCoin(coins[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        viewModel.networksList.observe(this) { networks ->
            val networkNames = networks.map { it.networkName }
            val networksAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, networkNames).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.networksSpinner.adapter = networksAdapter
        }
    }

    /**
     * Sets up the button to open the verification dialog when adding a payment.
     */
    private fun setupAddPayoutButton() {
        binding.btnAddPayment.setOnClickListener {
            val amount = binding.etPaymentAmount.text.toString()
            val address = binding.etPaymentAddress.text.toString()
            val coin = binding.coinsSpinner.selectedItem.toString()
            val network = binding.networksSpinner.selectedItem.toString()

            viewModel.userDetails.value?.let { it1 ->
                if (amount.isNotEmpty() && address .isNotEmpty()) {
                    CryptoAddressVerificationDialog.newInstance(amount, address, coin, network, it1)
                        .show(supportFragmentManager, "CryptoAddressVerificationDialog")
                } else {
                    showErrorSnackBar("Please add payout details")
                }
            }
        }
    }

    /**
     * Handles the successful update of the user's data after a payout.
     */
    private fun onUserUpdatedSuccess() {
        Toast.makeText(this, "Payout Added", Toast.LENGTH_LONG).show()
        finish()
    }
}
