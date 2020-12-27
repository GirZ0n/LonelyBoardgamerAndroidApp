package com.twoIlya.android.lonelyboardgamer.fragments.myprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.activities.error.ErrorActivity
import com.twoIlya.android.lonelyboardgamer.activities.login.LoginActivity
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.databinding.FragmentMyProfileBinding

class MyProfileFragment : Fragment() {

    private lateinit var binding: FragmentMyProfileBinding
    private val viewModel: MyProfileViewModel by lazy {
        ViewModelProvider(this).get(
            MyProfileViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_my_profile, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.events.observe(viewLifecycleOwner) {
            Log.d(TAG, "Event: $it")

            when (it.isHandle) {
                true -> return@observe
                false -> it.isHandle = true
            }

            when (it.type) {
                Event.Type.Notification -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
                Event.Type.Error -> {
                    val intent = ErrorActivity.newActivity(requireContext(), it.message)
                    startActivity(intent)
                    activity?.finish()
                }
                Event.Type.Move -> {
                    val intent = when (it.message) {
                        "Login" -> LoginActivity.newActivity(requireContext())
                        else -> ErrorActivity.newActivity(
                            requireContext(),
                            "MyProfileFragment: unknown destination"
                        )
                    }
                    startActivity(intent)
                    activity?.finish()
                }
            }
        }

        binding.editButton.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_editProfileFragment)
        }

        binding.logoutButton.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.my_profile_fragment_logout_dialog_title)
                positiveButton(R.string.my_profile_fragment_logout_dialog_positive_button) {
                    viewModel.logout()
                }
                negativeButton(R.string.my_profile_fragment_logout_dialog_negative_button)
            }
        }

        viewModel.updateProfileFromCache()
    }

    companion object {
        private const val TAG = "MyProfileFragment_TAG"
    }
}
