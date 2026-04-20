package murr.imad.myapplication.offerwalls.ui

import ai.bitlabs.sdk.BitLabs
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.auth.User
import murr.imad.myapplication.R
import murr.imad.myapplication.auth.data.repository.UserRepository
import murr.imad.myapplication.databinding.ActivityBitLabsBinding
import murr.imad.myapplication.databinding.ActivitySignInBinding

class BitLabsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBitLabsBinding
    private val userid = UserRepository().getCurrentUserID()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBitLabsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()


        this.let { BitLabs.init(it, "BITLABS_API_KEY", userid) }
        this.let { it1 -> BitLabs.launchOfferWall(it1) }
    }

    /**
     * Set up the action bar with a back button.
     */
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarBitlabsActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        binding.toolbarBitlabsActivity.setNavigationOnClickListener { onBackPressed() }
    }
}