package murr.imad.myapplication.payouts.ui.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import murr.imad.myapplication.R
import murr.imad.myapplication.shared.ui.view.MainActivity
import murr.imad.myapplication.auth.data.model.User
import murr.imad.myapplication.payouts.data.model.GiftCard
import murr.imad.myapplication.payouts.ui.view.adapters.GiftCardsAdapter
import murr.imad.myapplication.payouts.ui.viewmodel.GiftCardsViewModel
import murr.imad.myapplication.databinding.ActivityGiftCardsBinding

/**
 * Activity for displaying a list of gift cards and handling user interactions with them.
 */
class GiftCardsActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var binding: ActivityGiftCardsBinding

    private val viewModel: GiftCardsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiftCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        user = intent.getParcelableExtra(MainActivity.USER_DATA_MODEL)
            ?: throw IllegalArgumentException("User data missing")

        initializeViewModel()

        // Load gift cards data
        viewModel.loadGiftCards()
    }

    /**
     * Initializes the ViewModel and sets up observers and click listeners.
     */
    private fun initializeViewModel() {

        viewModel.giftCards.observe(this) { giftCards ->
            binding.idGRV.adapter = GiftCardsAdapter(giftCards, this)
        }

        viewModel.selectedGiftCard.observe(this) { giftCard ->
            giftCard?.let {
                startGiftCardPayoutActivity(it)
                viewModel.clearSelectedGiftCard()
            }
        }

        binding.idGRV.setOnItemClickListener { _, _, position, _ ->
            viewModel.giftCards.value?.get(position)?.let { viewModel.selectGiftCard(it) }
        }
    }

    /**
     * Configures the action bar with a back button and title.
     */
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarGiftCardsActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = getString(R.string.gift_cards)
        }
        binding.toolbarGiftCardsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * Starts the GiftCardsPayoutActivity with the selected gift card.
     *
     * @param giftCard The selected gift card to pass to the next activity.
     */
    private fun startGiftCardPayoutActivity(giftCard: GiftCard) {
        val bundle = Bundle().apply {
            putString(GIFT_CARD_CLICKED, giftCard.giftCardName)
            putParcelable(MainActivity.USER_DATA_MODEL, user)
        }
        startActivity(Intent(this, GiftCardsPayoutActivity::class.java).putExtra(BUNDLE_CODE, bundle))
        finish()
    }

    companion object {
        const val GIFT_CARD_CLICKED = "clicked_gift_card"
        const val BUNDLE_CODE = "bundle_code"
    }
}
