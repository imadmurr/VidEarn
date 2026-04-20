package murr.imad.myapplication.payouts.ui.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import murr.imad.myapplication.R
import murr.imad.myapplication.shared.ui.view.MainActivity
import murr.imad.myapplication.databinding.ActivityPayoutsBinding
import murr.imad.myapplication.auth.data.model.User
import murr.imad.myapplication.payouts.ui.viewmodel.PayoutsViewModel
import murr.imad.myapplication.shared.ui.view.MainActivity.Companion.USER_DATA_MODEL

/**
 * [PayoutsActivity] is responsible for displaying payout options and handling
 * navigation to different payout-related activities.
 *
 * It sets up the UI, including the action bar and click listeners, and uses the [PayoutsViewModel]
 * to manage and observe user details.
 */
class PayoutsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayoutsBinding
    private val viewModel: PayoutsViewModel by viewModels()
    private lateinit var user: User

    /**
     * Called when the activity is first created.
     *
     * This method inflates the layout, initializes the [PayoutsViewModel], and sets up the UI.
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayoutsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.extras?.getParcelable(USER_DATA_MODEL) ?: return

        setupActionBar()
        setupClickListeners()
    }

    /**
     * Sets up the action bar with a title and back navigation.
     */
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarPayoutsActivity)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = getString(R.string.payouts)
        }

        binding.toolbarPayoutsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Sets up click listeners for the CardViews to navigate to the respective activities.
     */
    private fun setupClickListeners() {
        binding.myPayoutsCard.setOnClickListener {
            launchActivity(MyPayoutsActivity::class.java)
        }

        binding.cryptoPayoutCard.setOnClickListener {
            launchActivity(CryptoPayoutActivity::class.java)
        }

        binding.giftCardPayoutCard.setOnClickListener {
            launchActivity(GiftCardsActivity::class.java)
        }
    }

    /**
     * Launches the specified activity and passes the user details as an intent extra.
     *
     * @param activityClass The activity class to launch.
     */
    private fun launchActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass).apply {
            putExtra(USER_DATA_MODEL, user)
        }
        startActivity(intent)
    }
}
