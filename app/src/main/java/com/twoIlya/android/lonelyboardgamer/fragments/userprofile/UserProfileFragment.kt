package com.twoIlya.android.lonelyboardgamer.fragments.userprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.activities.error.ErrorActivity
import com.twoIlya.android.lonelyboardgamer.activities.login.LoginActivity
import com.twoIlya.android.lonelyboardgamer.dataClasses.EventType
import com.twoIlya.android.lonelyboardgamer.databinding.FragmentUserProfileBinding
import com.twoIlya.android.lonelyboardgamer.fragments.myprofile.MyProfileFragment

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private val viewModel: UserProfileViewModel by lazy {
        ViewModelProvider(this).get(
                UserProfileViewModel::class.java
        )
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_user_profile, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = (arguments?.get("id") as? Int) ?: -1
        viewModel.updateProfile(id)

        viewModel.events.observe(viewLifecycleOwner) {
            Log.d(TAG, "Event: $it")

            when (it.isHandle) {
                true -> return@observe
                false -> it.isHandle = true
            }

            when (it.type) {
                EventType.Notification -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
                EventType.Error -> {
                    val intent = ErrorActivity.newActivity(requireContext(), it.message)
                    startActivity(intent)
                    activity?.finish()
                }
                EventType.Move -> {
                    val intent = when (it.message) {
                        "Login" -> LoginActivity.newActivity(requireContext())
                        else -> ErrorActivity.newActivity(requireContext(), "Unknown destination")
                    }
                    startActivity(intent)
                    activity?.finish()
                }
            }
        }

        viewModel.stateCode.observe(viewLifecycleOwner) {
            when (it) {
                3 -> {
                    binding.bottomButtom.setText(R.string.chat_button)
                    binding.bottomButtom.setOnClickListener {
                        viewModel.bottomButtonClick("chat")
                    }

                    binding.upButton.setImageResource(R.drawable.ic_baseline_delete_24)
                    binding.upButton.isVisible = true
                    binding.upButton.setOnClickListener {
                        viewModel.bottomButtonClick("unfriend")
                    }
                }
                2 -> {
                    binding.bottomButtom.setText(R.string.withdraw_request_button)
                    binding.bottomButtom.setOnClickListener {
                        viewModel.bottomButtonClick("withdraw")
                    }
                    binding.upButton.isVisible = false
                }
                1 -> {
                    binding.bottomButtom.setText(R.string.answer_request_button)
                    binding.bottomButtom.setOnClickListener {
                        // TODO: диалог
                        // viewModel.bottomButtonClick()
                    }
                    binding.upButton.isVisible = false
                }
                0 -> {
                    binding.bottomButtom.text = ""
                    binding.upButton.isVisible = false
                }
                else -> {
                    val intent = ErrorActivity.newActivity(requireContext(),
                            "Something went wrong. UPF(statusCode: $it)")
                    startActivity(intent)
                    activity?.finish()
                }
            }
        }
    }

    companion object {
        private const val TAG = "UserProfileFragment_TAG"
    }
}
