package murr.imad.videarn.shared.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.traffmonetizer.sdk.TraffmonetizerSdk
import murr.imad.videarn.R
import murr.imad.videarn.auth.data.model.User
import murr.imad.videarn.dailyearn.ui.view.DailyEarnFragment
import murr.imad.videarn.databinding.ActivityMainBinding
import murr.imad.videarn.databinding.NavHeaderMainBinding
import murr.imad.videarn.home.ui.view.HomeFragment
import murr.imad.videarn.internetsharing.ui.view.InternetSharingFragment
import murr.imad.videarn.offerwalls.ui.SurveysFragment
import murr.imad.videarn.payouts.ui.view.activities.PayoutsActivity
import murr.imad.videarn.shared.ui.viewmodel.MainViewModel
import murr.imad.videarn.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding
    private val viewModel: MainViewModel = MainViewModel()
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("CommitTransaction")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Access the nav_header_main layout's views through the navigation view
        val navHeaderView = binding.navView.getHeaderView(0)
        navHeaderMainBinding = NavHeaderMainBinding.bind(navHeaderView)

        setupActionBar()
        setupBottomBar()
        setupNavigationView()

        // Initialize SDKs and check for updates
        initializeSDKs()

        // Check for app updates
        viewModel.checkForAppUpdate(this)

        // Initialize shared preferences and check for FCM token updates
        sharedPreferences = getSharedPreferences(Constants.VIDEARN_PREFERENCES, Context.MODE_PRIVATE)
        viewModel.checkForFCMTokenUpdates(sharedPreferences)

        // Observe user details and update the UI accordingly
        viewModel.userDetails.observe(this) { user ->
            user?.let { updateNavigationUserDetails(it) }
        }
    }

    @SuppressLint("CommitTransaction")
    private fun setupBottomBar() {
        viewModel.userDetails.observe(this) { user ->
            if (user != null){
                supportFragmentManager.beginTransaction()
                    .replace(binding.mainFragment.id, HomeFragment())
                    .commit()
            }
        }

        binding.bottomBar.setOnItemSelectedListener { itemId ->
            val selectedFragment: Fragment? = when (itemId) {
                0 -> HomeFragment()
                1 -> viewModel.userDetails.value?.let { InternetSharingFragment.newInstance(it) }
                2 -> viewModel.userDetails.value?.let { SurveysFragment.newInstance(it) }
                3 -> viewModel.userDetails.value?.let { DailyEarnFragment.newInstance(it) }
                else -> HomeFragment()
            }
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(binding.mainFragment.id, it)
                    .commit()
            }
        }
    }

    private fun setupNavigationView() {
        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMainActivity)
        binding.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        binding.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun initializeSDKs() {
        TraffmonetizerSdk.init(
            this,
            "TRAFFMONETIZER_KEY",
            wifiOnly = false, manualMode = true
        )
    }

    private fun updateNavigationUserDetails(user: User) {
        navHeaderMainBinding.tvEmail.text = user.email

        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName
        binding.appVersion.text = "App version $versionName"
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_payouts -> startActivity(Intent(this, PayoutsActivity::class.java).apply {
                putExtra(USER_DATA_MODEL, viewModel.userDetails.value)
            })
            R.id.nav_faq -> startActivity(Intent(this, FaqActivity::class.java))
            R.id.nav_write_review -> {
                val appPackageName = packageName
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                    intent.setPackage("com.android.vending")
                    startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
                    startActivity(intent)
                }
            }
            R.id.nav_sign_out -> signOut()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOut() {
        AlertDialog.Builder(this)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.signOut(this)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkForAppUpdate(this)
    }

    companion object {
        const val USER_DATA_MODEL = "USER"
    }
}
