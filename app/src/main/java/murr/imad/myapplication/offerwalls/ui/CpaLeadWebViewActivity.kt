package murr.imad.myapplication.offerwalls.ui

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import android.webkit.WebViewClient
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import murr.imad.myapplication.R
import murr.imad.myapplication.databinding.ActivityCpaLeadWebViewBinding
import murr.imad.myapplication.shared.ui.view.BaseActivity

class CpaLeadWebViewActivity : BaseActivity() {

    private lateinit var binding: ActivityCpaLeadWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCpaLeadWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("URL") ?: return

        setupActionBar()

        binding.webView.apply {

            showProgressDialog("Loading..")
            // Enable JavaScript
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                // Called when the page is finished loading
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // Here, you can perform actions when the page has fully loaded
                    hideProgressDialog()
                }
            }

            loadUrl(url)

        }
    }

    /**
     * Sets up the action bar with a title and back navigation.
     */
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCpaLeadActivity)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = getString(R.string.cpalead)
        }

        binding.toolbarCpaLeadActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}