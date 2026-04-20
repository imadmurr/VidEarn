package murr.imad.myapplication.dailyearn.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import murr.imad.myapplication.R
import murr.imad.myapplication.auth.data.model.User
import murr.imad.myapplication.dailyearn.ui.viewmodel.DailyEarnViewModel
import murr.imad.myapplication.databinding.FragmentDailyEarnBinding


/**
 * A simple [Fragment] subclass.
 * Use the [DailyEarnFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DailyEarnFragment : Fragment() {
    private lateinit var user: User
    private lateinit var binding: FragmentDailyEarnBinding
    private val viewModel = DailyEarnViewModel()

    private val timer =  86400000
    // 86400000 One day
    // 60000  1min for testing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable(ARG_USER) ?: throw IllegalArgumentException("User argument missing")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDailyEarnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()

        /*binding.spinAndWinCard.setOnClickListener {
            viewModel.user.value?.let { user ->
                if ((System.currentTimeMillis() - user.dailyBonusTime) >= timer) {
                    val dialog = SpinWheelFragment.newInstance()
                    dialog.show(parentFragmentManager, SpinWheelFragment.TAG)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You already claimed your bonus today!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }*/
        Toast.makeText(requireContext(), "Under Maintenance", Toast.LENGTH_SHORT).show()
        binding.spinAndWinCard.apply {
            alpha = 0.5f
            isEnabled = false
            isClickable = false
            isFocusable = false
        }
    }

    private fun observeData() {
        // Observe user data from the ViewModel
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                try {
                    updateUI(it)
                } catch (e: Exception) {
                    Log.e("Daily Earn",e.printStackTrace().toString())
                }
            }
        }

    }

    private fun updateUI(user: User) {
        if ((System.currentTimeMillis() - user.dailyBonusTime) >= timer) {
            binding.tvSpinTheWheelNextBonusIn.text = getString(R.string.bonusredeem)
            binding.spinAndWinBar.progress = 1
        } else {
            binding.spinAndWinBar.progress = 10
            //86400000
            val timeRemaining = (timer - (System.currentTimeMillis() - user.dailyBonusTime))
            ("Next in: ${timeRemaining / 1000 / 3600} h" +
                    " ${timeRemaining / 1000 / 60 % 60} min").also {
                binding.tvSpinTheWheelNextBonusIn.text = it
            }
        }
    }

    companion object {
        const val ARG_USER = "user"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param user The user object to be passed to the fragment.
         * @return A new instance of fragment DailyEarnFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(user: User) =
            DailyEarnFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }
}