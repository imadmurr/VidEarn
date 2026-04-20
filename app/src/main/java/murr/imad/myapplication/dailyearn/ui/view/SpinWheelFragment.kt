package murr.imad.myapplication.dailyearn.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import murr.imad.myapplication.R
import murr.imad.myapplication.databinding.FragmentSpinWheelDialogBinding
import murr.imad.myapplication.dailyearn.ui.viewmodel.SpinWheelViewModel

/**
 * Dialog fragment for spinning the wheel.
 */
class SpinWheelFragment : DialogFragment() {

    private var _binding: FragmentSpinWheelDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SpinWheelViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        _binding = FragmentSpinWheelDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        setupSpinWheel()

        // Observe daily time update status
        viewModel.updateUserStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "You Won ${viewModel.coinAmount.value} Coins", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Error Occured", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        // Handle the spin button click
        binding.spinButton.setOnClickListener {
            val index = viewModel.getRandomIndex()
            binding.luckyWheel.startLuckyWheelWithTargetIndex(index)

            this.isCancelable = false
            binding.spinButton.isEnabled = false
            binding.spinButton.alpha = 0.5f
        }

        binding.luckyWheel.setLuckyRoundItemSelectedListener { index ->
            viewModel.updateCoinAmount(index)
        }

        return view
    }

    private fun setupSpinWheel() {
        val luckyWheelView = binding.luckyWheel

        luckyWheelView.setData(viewModel.getLuckyItems())
        luckyWheelView.setRound(viewModel.getRandomRound())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SpinWheelDialogFragment"

        /**
         * Creates a new instance of [SpinWheelFragment].
         *
         * @return A new instance of [SpinWheelFragment].
         */
        fun newInstance(): SpinWheelFragment {
            return SpinWheelFragment()
        }
    }
}
