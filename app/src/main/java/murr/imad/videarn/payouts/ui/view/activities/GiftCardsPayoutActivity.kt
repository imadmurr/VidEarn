package murr.imad.videarn.payouts.ui.view.activities

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import murr.imad.videarn.R
import murr.imad.videarn.shared.ui.view.BaseActivity
import murr.imad.videarn.shared.ui.view.MainActivity
import murr.imad.videarn.databinding.ActivityGiftCardsPayoutBinding
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.payouts.ui.viewmodel.GiftCardsPayoutViewModel
import murr.imad.videarn.payouts.ui.viewmodel.GiftCardsPayoutViewModelFactory

/**
 * Activity for managing the gift card payout process.
 */
class GiftCardsPayoutActivity : BaseActivity() {

    private lateinit var userDetails: User
    private lateinit var giftCardType: String
    private lateinit var binding: ActivityGiftCardsPayoutBinding
    private lateinit var viewModel: GiftCardsPayoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiftCardsPayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        retrieveIntentData()

        // Initialize ViewModel with factory
        val factory = GiftCardsPayoutViewModelFactory(userDetails, giftCardType)
        viewModel = ViewModelProvider(this, factory)[GiftCardsPayoutViewModel::class.java]

        // Set gift card type to display
        binding.etGiftCardType.text = giftCardType.toEditable()

        setupObservers()
        setupListeners()
    }

    /**
     * Retrieves user and gift card type data from the intent bundle.
     */
    private fun retrieveIntentData() {
        val bundle = intent.getBundleExtra(GiftCardsActivity.BUNDLE_CODE)
        userDetails = bundle?.getParcelable(MainActivity.USER_DATA_MODEL)!!
        giftCardType = bundle.getString(GiftCardsActivity.GIFT_CARD_CLICKED).toString()
    }

    /**
     * Sets up observers for LiveData properties in the ViewModel.
     */
    private fun setupObservers() {
        viewModel.giftCardPriceOptions.observe(this) { prices ->
            val adapter = ArrayAdapter(this, R.layout.network_spinner_item, prices)
            binding.giftCardPriceSpinner.adapter = adapter
        }

        viewModel.userDetails.observe(this) { user ->
            binding.giftCardPymntAvailableBalance.text = user?.let {
                "Your balance: %,d points".format(
                    it.points)
            }
        }

        viewModel.showError.observe(this) { message ->
            message?.let {
                showErrorSnackBar(it)
                viewModel.onErrorHandled()
            }
        }

        viewModel.navigateToPayoutsActivity.observe(this) {
            it?.let {
                Toast.makeText(this, "Payout Added", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    /**
     * Sets up listeners for UI interactions.
     */
    private fun setupListeners() {
        binding.giftCardPriceSpinner.setOnItemSelectedListener { _, _, position, _ ->
            viewModel.onPriceSelected(position)
        }

        binding.btnAddGiftCardPayment.setOnClickListener {
            val email = binding.etGiftCardEmail.text.toString().trim()
            viewModel.addGiftCardPayment(email)
        }
    }

    /**
     * Configures the action bar for this activity.
     */
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarGiftCardsPayoutActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = getString(R.string.redeem_gift_card)
        }
        binding.toolbarGiftCardsPayoutActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Extension function to set an item selected listener on a Spinner.
     */
    private fun AdapterView<*>.setOnItemSelectedListener(listener: (parent: AdapterView<*>, view: View, position: Int, id: Long) -> Unit) {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                listener(parent, view, position, id)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No-op
            }
        }
    }

    /**
     * Extension function to convert a String to an Editable.
     */
    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
