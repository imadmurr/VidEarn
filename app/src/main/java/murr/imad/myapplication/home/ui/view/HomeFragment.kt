package murr.imad.myapplication.home.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import murr.imad.myapplication.databinding.FragmentHomeBinding
import murr.imad.myapplication.home.ui.viewmodel.HomeViewModel
import murr.imad.myapplication.payouts.ui.view.activities.PayoutsActivity
import murr.imad.myapplication.shared.ui.view.MainActivity.Companion.USER_DATA_MODEL
import murr.imad.myapplication.utils.toDollars
import java.lang.Exception

/**
 * Fragment representing the home screen of the application.
 * Displays user information and payout totals.
 */
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel = HomeViewModel()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSkeleton()

        viewModel.paidOutTotal.observe(viewLifecycleOwner) { total ->
            binding.paidOutAmountText.text = total.toDollars()
            hideSkeleton()
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                "Welcome back, ${user.name.uppercase()}".also { binding.greetingText.text = it }
                binding.balanceText.text = user.points.toDollars()
                viewModel.fetchPayoutsAndCalculateTotals()
            }
        }

        binding.requestPayoutButton.setOnClickListener {
            try {
                val intent = Intent(requireActivity(), PayoutsActivity::class.java).apply {
                    putExtra(USER_DATA_MODEL, viewModel.user.value)
                }
                startActivity(intent)
            } catch (e: Exception){
                Toast.makeText(requireContext(),"Failed to open payouts screen",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSkeleton(){
        binding.homeRelativeLayout.loadSkeleton()
        binding.greetingText.loadSkeleton()
        binding.motivatingText.loadSkeleton()
        binding.currentBalanceText.loadSkeleton()
        binding.balanceText.loadSkeleton()
        binding.paidOutBalanceText.loadSkeleton()
        binding.paidOutAmountText.loadSkeleton()
    }

    private fun hideSkeleton(){
        binding.homeRelativeLayout.hideSkeleton()
        binding.greetingText.hideSkeleton()
        binding.motivatingText.hideSkeleton()
        binding.currentBalanceText.hideSkeleton()
        binding.balanceText.hideSkeleton()
        binding.paidOutBalanceText.hideSkeleton()
        binding.paidOutAmountText.hideSkeleton()
    }
}
