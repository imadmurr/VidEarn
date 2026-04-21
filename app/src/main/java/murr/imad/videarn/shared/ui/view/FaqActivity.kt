package murr.imad.videarn.shared.ui.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import murr.imad.videarn.R
import murr.imad.videarn.databinding.ActivityFaqBinding

class FaqActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaqBinding

    private val questions = listOf(
        "What is VidEarn?", "How do I earn?", "What rewards do you offer?",
        "How much can I earn?", "How can I earn more?", "Why is internet sharing slow?",
        "How do I use offerwalls?", "How do I contact you?"
    )

    private val answers = listOf(
        "VidEarn is an innovative mobile app that allows users to earn rewards through internet sharing, surveys, offerwalls, and more. It’s designed to give you the freedom to earn on your terms with minimal effort.",
        "Simply create a free account, and you’ll unlock multiple earning opportunities, like watching videos, completing surveys, sharing internet, and more. Pick an option that works for you and start earning points!",
        "VidEarn offers a wide variety of rewards, including popular cryptocurrencies like Bitcoin and USDT, as well as gift cards to your favorite stores. More reward options will be added soon!",
        "Earnings depend on your activity, but most users can expect to earn anywhere between \$10 and \$100+ per month. Keep in mind, the more active you are, the more you’ll earn.",
        "You can boost your earnings by referring friends and completing high-paying offerwalls. Internet sharing and completing surveys are some of the best ways to maximize your rewards.",
        "VidEarn’s internet sharing feature works best in countries with high-speed internet like the US, UK, and parts of Europe. It might be slower in regions with lower internet speeds due to network limitations.",
        "Choose an offer that interests you and carefully follow the instructions. Completing offers correctly ensures that you’ll receive your points automatically after the task is done.",
        "If you run into any issues or have questions, feel free to reach out via email at videarn@outlook.com. We’re here to help!"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = FAQAdapter(questions, answers)

        // Contact us button
        binding.contactUsButton.setOnClickListener {
            sendEmail()
        }
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("videarn@outlook.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            putExtra(Intent.EXTRA_TEXT, "Hi, I need help with...")
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarFaqActivity)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = getString(R.string.faq)
        }

        binding.toolbarFaqActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}
