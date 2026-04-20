package murr.imad.myapplication.offerwalls.ui

import ai.bitlabs.sdk.BitLabs
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kiwiwall.sdk.KiwiOWClient
import murr.imad.myapplication.R
import murr.imad.myapplication.auth.data.model.User
import murr.imad.myapplication.auth.data.repository.UserRepository
import murr.imad.myapplication.databinding.FragmentSurveysBinding


/**
 * A simple [Fragment] subclass.
 * Use the [SurveysFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class SurveysFragment : Fragment() {
    private lateinit var user: User
    private lateinit var binding: FragmentSurveysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable(ARG_USER) ?: throw IllegalArgumentException("User argument missing")
        }
        context?.let { BitLabs.init(it, "BITLABS_API_KEY", user.id) };

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSurveysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup click listener for the info icon to display a popup window
        binding.infoIcon.setOnClickListener {
            showInfoPopup(it)
        }

        binding.pollfishCard.apply {
            alpha = 0.5f
            isEnabled = false
            isClickable = false
            isFocusable = false
        }
        binding.cpxResearchCard.setOnClickListener {
            /*alpha = 0.5f
            isEnabled = false
            isClickable = false
            isFocusable = false*/
            val url = "https://offers.cpx-research.com/index.php?app_id=24804&ext_user_id=${user.id}&username=${user.name}&email=${user.email}"
            val intent = Intent(requireActivity(), CPXResearchActivity::class.java).apply {
                putExtra("URL", url)
            }
            startActivity(intent)
        }

        // Setup click listeners for CardViews
        binding.bitlabsCard.setOnClickListener {
            context?.let { it1 -> BitLabs.launchOfferWall(it1) }
        }

        binding.cpaLeadCard.setOnClickListener {
            //CpaLead
            val intent = Intent(requireActivity(), CpaLeadWebViewActivity::class.java).apply {
                putExtra("URL", "https://www.lnksforyou.com/list/37659?subid=${user.id}")
            }
            startActivity(intent)
        }

        binding.kiwiWallCard.setOnClickListener {
            //KiwiWall
            try {
                KiwiOWClient.showOfferWall(requireActivity(),
                    "6vuucVJiHAnzVnZ0clX5nvrGd30PPjF4",
                    user.id)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    /**
     * Displays a popup window with additional information.
     *
     * @param anchorView The view to which the popup window will be anchored.
     */
    private fun showInfoPopup(anchorView: View) {
        val popupView = layoutInflater.inflate(R.layout.popup_info, null)
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAsDropDown(anchorView, 0, 0)
    }

    companion object {
        const val ARG_USER = "user"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param user The user object to be passed to the fragment.
         * @return A new instance of fragment SurveysFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(user: User) =
            SurveysFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }
}