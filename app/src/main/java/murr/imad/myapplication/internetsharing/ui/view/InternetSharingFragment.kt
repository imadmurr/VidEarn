package murr.imad.myapplication.internetsharing.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dev.jahidhasanco.networkusage.Interval
import dev.jahidhasanco.networkusage.NetworkType
import dev.jahidhasanco.networkusage.NetworkUsageManager
import dev.jahidhasanco.networkusage.Util
import murr.imad.myapplication.R
import murr.imad.myapplication.databinding.FragmentInternetSharingBinding
import murr.imad.myapplication.auth.data.model.User
import murr.imad.myapplication.auth.data.repository.UserRepository
import murr.imad.myapplication.internetsharing.ui.view.utils.network.MyNetworkUsageManager
import murr.imad.myapplication.internetsharing.ui.view.utils.network.ServiceUtils
import murr.imad.myapplication.internetsharing.ui.view.utils.network.TrafficChecker
import murr.imad.myapplication.internetsharing.ui.view.utils.network.formatDataWithPrecision
import murr.imad.myapplication.internetsharing.ui.view.utils.network.getPointsToReward
import murr.imad.myapplication.internetsharing.ui.view.utils.network.isOnMobileData
import murr.imad.myapplication.internetsharing.ui.view.utils.network.isOnWifi
import murr.imad.myapplication.internetsharing.ui.view.utils.permissions.PermissionCallback
import murr.imad.myapplication.internetsharing.ui.view.utils.permissions.PermissionType
import murr.imad.myapplication.internetsharing.ui.view.utils.permissions.PermissionUtil

/**
 * [InternetSharingFragment] handles internet sharing functionalities, managing permissions
 * and network traffic usage, and tracking the user's earned points for shared traffic.
 */
@Suppress("DEPRECATION")
class InternetSharingFragment : Fragment(), PermissionCallback {

    private var _binding: FragmentInternetSharingBinding? = null
    private val binding get() = _binding!!

    private lateinit var user: User
    private var permissionsAllowed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable(ARG_USER)
                ?: throw IllegalArgumentException("User argument missing")
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("BatteryLife")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInternetSharingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionsAllowed = PermissionUtil.isPackageUsageStatsPermissionGranted(requireContext())
        initSwitchesState()
        setupService()
        updateBatteryOptimizationStatus()
        manageTrafficAndPoints()
        checkAndRequestNotificationPermission()

        binding.shareInternetButton.setOnClickListener { handleInternetSharing() }
        binding.switchSetupInternetSharing.setOnCheckedChangeListener { _, isChecked ->
            handleServiceSwitchChange(isChecked)
        }
        binding.switchBatteryOptimization.setOnCheckedChangeListener { _, isChecked ->
            handleBatteryOptimizationSwitch(isChecked)
        }
        binding.switchMobileDataUsage.setOnCheckedChangeListener { _, isChecked ->
            handleMobileDataSwitchChange(isChecked)
        }
        binding.infoSlideshow.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateDots(binding.infoSlideshow.displayedChild)
        }
    }

    /**
     * Initializes the state of the switches based on permission status.
     */
    private fun initSwitchesState() {
        binding.switchMobileDataUsage.isChecked = PermissionUtil.isReadPhoneStatePermissionGranted(requireContext())
        binding.switchSetupInternetSharing.isChecked = permissionsAllowed
    }

    /**
     * Updates the dots indicator in the slideshow based on the current position.
     *
     * @param position Current position of the slideshow.
     */
    private fun updateDots(position: Int) {
        binding.dot1.setBackgroundResource(if (position == 0) R.drawable.dot_selected else R.drawable.dot_unselected)
        binding.dot2.setBackgroundResource(if (position == 1) R.drawable.dot_selected else R.drawable.dot_unselected)
        binding.dot3.setBackgroundResource(if (position == 2) R.drawable.dot_selected else R.drawable.dot_unselected)
    }

    /**
     * Sets up the service and configures the UI elements based on service status and permissions.
     */
    private fun setupService() {
        if (permissionsAllowed) binding.switchSetupInternetSharing.isChecked = true

        if (ServiceUtils().isServiceRunning(requireContext(), InternetSharingService::class.java)) {
            binding.switchSetupInternetSharing.isChecked = true
            updateEarningStatus(true)
            binding.shareInternetButton.text = getString(R.string.stop_sharing)
        }
    }

    /**
     * Manages the traffic usage calculation and updates the UI with earned points.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun manageTrafficAndPoints() {
        val networkUsage = MyNetworkUsageManager(
            NetworkUsageManager(requireContext(), Util.getSubscriberId(requireContext())), requireContext()
        )
        val trafficChecker = TrafficChecker(requireContext())

        if (trafficChecker.isNewDay()) {
            val traffic = networkUsage.getForegroundUsageForMyApp(Interval.today, NetworkType.ALL).uploads / 2L
            trafficChecker.sharedPreferences.edit().putLong("previousTraffic", traffic).apply()
            trafficChecker.setLastResetDate(System.currentTimeMillis())
        }

        val trafficToday = trafficChecker.sharedPreferences.getLong("previousTraffic", 0L)
        binding.trafficSharedAmount.text = formatDataWithPrecision(trafficToday, 0L)[2]
        binding.earnedPointsToday.text = getString(R.string.earned_points_today, getPointsToReward(trafficToday))
    }

    /**
     * Starts or stops the internet sharing service based on the current state.
     */
    private fun handleInternetSharing() {
        if (isOnMobileData(requireContext()) && PermissionUtil.isReadPhoneStatePermissionGranted(requireContext())) {
            toggleInternetSharing()
        } else if (isOnWifi(requireContext())) {
            toggleInternetSharing()
        } else {
            Toast.makeText(requireContext(), "Please enable mobile data usage permission", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Toggles the internet sharing service and updates UI accordingly.
     */
    private fun toggleInternetSharing() {
        if (ServiceUtils().isServiceRunning(requireContext(), InternetSharingService::class.java)) {
            stopInternetSharingService()
            updateEarningStatus(false)
        } else if (permissionsAllowed) {
            startInternetSharingService()
            updateEarningStatus(true)
        } else {
            Toast.makeText(requireContext(), "Setup Service First!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Updates the earning status UI elements.
     *
     * @param isEarning Boolean indicating whether the user is earning points.
     */
    private fun updateEarningStatus(isEarning: Boolean) {
        if (isEarning) {
            binding.earningStatus.apply {
                text = getString(R.string.you_re_now_earning)
                setBackgroundResource(R.drawable.earning_status_background)
            }
            binding.shareInternetButton.text = getString(R.string.stop_sharing)
        } else {
            binding.earningStatus.apply {
                text = getString(R.string.not_earning)
                setBackgroundResource(R.drawable.not_earning_status_background)
            }
            binding.shareInternetButton.text = getString(R.string.share_internet)
        }
    }

    /**
     * Handles changes in the service switch and prompts for permissions if needed.
     *
     * @param isChecked Whether the switch is checked or not.
     */
    private fun handleServiceSwitchChange(isChecked: Boolean) {
        if (isChecked && !PermissionUtil.isPackageUsageStatsPermissionGranted(requireContext())) {
            binding.switchSetupInternetSharing.isChecked = false
            promptPermissionsBeforeService()
        }
    }

    /**
     * Shows an alert dialog prompting the user to grant the required permissions.
     */
    private fun promptPermissionsBeforeService() {
        PermissionUtil.showPermissionAlertDialog(
            requireContext(),
            "In order for this feature to work as expected," +
                    " search for VidEarn in the screen that will open, then Allow",
            "Open Settings", "Cancel"
        ) {
            PermissionUtil.checkAndRequestPackageUsageStatsPermission(requireActivity(), this)
        }
    }

    /**
     * Handles changes to the mobile data usage switch, requesting the required permission.
     *
     * @param isChecked Whether the switch is checked or not.
     */
    private fun handleMobileDataSwitchChange(isChecked: Boolean) {
        if (isChecked) {
            if (!PermissionUtil.isReadPhoneStatePermissionGranted(requireContext())) {
                PermissionUtil.showPermissionAlertDialog(
                    requireContext(),
                    "VidEarn needs permission to monitor your mobile data usage to ensure accurate rewards.",
                    "Ok", "Cancel"
                ) {
                    PermissionUtil.checkAndRequestReadPhoneStatePermission(requireActivity(), this)
                }
                binding.switchMobileDataUsage.isChecked = false
            }
        } else {
            PermissionUtil.promptUserToRevokePermission(requireContext())
            binding.switchMobileDataUsage.isChecked =
                PermissionUtil.isReadPhoneStatePermissionGranted(requireContext())
        }
    }

    /**
     * Handles changes to the battery optimization switch, showing a prompt to disable battery optimization.
     *
     * @param isChecked Whether the switch is checked or not.
     */
    private fun handleBatteryOptimizationSwitch(isChecked: Boolean) {
        if (isChecked) {
            PermissionUtil.showPermissionAlertDialog(
                requireContext(),
                "Disable battery optimization for the app by going to App Battery Usage -> Select Unrestricted",
                "Open Settings", "Cancel"
            ) {
                PermissionUtil.openAppSettings(requireContext())
            }
            updateBatteryOptimizationStatus()
        }
    }

    /**
     * Updates the battery optimization switch status based on the current setting.
     */
    private fun updateBatteryOptimizationStatus() {
        val powerManager = requireContext().getSystemService(PowerManager::class.java)
        if (powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)) {
            binding.switchBatteryOptimization.isChecked = true
        }
    }

    /**
     * Checks and requests notification permission for Android 13+ devices.
     */
    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    /**
     * Starts the internet sharing service.
     */
    private fun startInternetSharingService() {
        val serviceIntent = Intent(requireContext(), InternetSharingService::class.java).apply {
            putExtra("userId", UserRepository().getCurrentUserID())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent)
        } else {
            requireContext().startService(serviceIntent)
        }
    }

    /**
     * Stops the internet sharing service.
     */
    private fun stopInternetSharingService() {
        requireContext().stopService(Intent(requireContext(), InternetSharingService::class.java).apply {
            putExtra("userId", UserRepository().getCurrentUserID())
        })
    }

    /**
     * A launcher for requesting permissions at runtime.
     */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(requireContext(), "Notification permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onPermissionGranted() {
        TODO("Not yet implemented")
    }

    override fun onPermissionGranted(permissionType: PermissionType) {
        when (permissionType) {
            PermissionType.PACKAGE_USAGE_STATS -> {
                permissionsAllowed = PermissionUtil.isPackageUsageStatsPermissionGranted(requireContext())
                setupService()
            }
            PermissionType.READ_PHONE_STATE -> binding.switchMobileDataUsage.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_USER = "user"

        /**
         * Creates a new instance of [InternetSharingFragment] with the specified user.
         *
         * @param user The [User] object passed as an argument.
         * @return A new instance of [InternetSharingFragment].
         */
        @JvmStatic
        fun newInstance(user: User) =
            InternetSharingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }

    override fun onPermissionDenied() {
        TODO("Not yet implemented")
    }
}
