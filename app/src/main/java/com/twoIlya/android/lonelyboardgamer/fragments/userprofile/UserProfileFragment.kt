package com.twoIlya.android.lonelyboardgamer.fragments.userprofile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.activities.error.ErrorActivity
import com.twoIlya.android.lonelyboardgamer.activities.login.LoginActivity
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.databinding.FragmentUserProfileBinding


class UserProfileFragment : Fragment() {

    private var state: State = LoadingState()

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

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.id = (arguments?.get("id") as? Int) ?: -1
        viewModel.updateProfile()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.updateProfile()
        }

        binding.upButton.setOnClickListener {
            state.upButtonClickAction()
        }

        binding.bottomButtom.setOnClickListener {
            state.bottomButtonClickAction()
        }

        viewModel.friendStatus.observe(viewLifecycleOwner) {
            when (it) {
                FriendStatus.Loading -> state = LoadingState()
                FriendStatus.None -> state = NoneState()
                FriendStatus.OutRequest -> state = OutRequestState()
                FriendStatus.InRequest -> state = InRequestState()
                FriendStatus.Friend -> state = FriendState()
                else -> {
                    val intent = ErrorActivity.newActivity(
                        requireContext(),
                        "Something went wrong. UPF(statusCode: $it)"
                    )
                    startActivity(intent)
                    activity?.finish()
                }
            }

            state.updateLayout()
        }

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
                    when {
                        it.message == "Login" -> {
                            val intent = LoginActivity.newActivity(requireContext())
                            startActivity(intent)
                            activity?.finish()
                        }
                        it.message.isDigitsOnly() -> {
                            Log.d(TAG, "it.message: $it")
                            val intent =
                                getOpenVKIntent(it.message, requireContext().packageManager)
                            startActivity(intent)
                        }
                        else -> {
                            val intent =
                                ErrorActivity.newActivity(requireContext(), "Unknown destination")
                            startActivity(intent)
                            activity?.finish()
                        }
                    }
                }
            }
        }
    }

    private fun getOpenVKIntent(
        id: String,
        packageManager: PackageManager
    ): Intent {
        val url = if (isPackageInstalled(VK_APP_PACKAGE_NAME, packageManager)) {
            "vk://$VK_APP_USER_PAGE_URL$id"
        } else {
            "https://$VK_APP_USER_PAGE_URL$id"
        }

        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // region State Pattern

    private interface State {
        fun upButtonClickAction()
        fun bottomButtonClickAction()
        fun updateLayout()
    }

    private inner class LoadingState : State {
        // Do nothing
        override fun upButtonClickAction() {}

        // Do nothing
        override fun bottomButtonClickAction() {}

        override fun updateLayout() {
            binding.bottomButtom.text = ""
            binding.upButton.isVisible = false
        }
    }

    private inner class FriendState : State {
        override fun upButtonClickAction() {
            viewModel.upButtonClick(UserProfileAction.UNFRIEND)
        }

        override fun bottomButtonClickAction() {
            viewModel.bottomButtonClick(UserProfileAction.CHAT)
        }

        override fun updateLayout() {
            binding.bottomButtom.setText(R.string.chat_button)

            binding.upButton.isVisible = true
            binding.upButton.setImageResource(R.drawable.ic_baseline_delete_24)
        }
    }

    private inner class InRequestState : State {
        // Do nothing
        override fun upButtonClickAction() {}

        override fun bottomButtonClickAction() {
            MaterialDialog(requireContext()).show {
                title(R.string.answer_dialog_title)
                positiveButton(R.string.answer_agree_button) {
                    viewModel.bottomButtonClick(UserProfileAction.ACCEPT)
                }
                negativeButton(R.string.answer_disagree_button) {
                    viewModel.bottomButtonClick(UserProfileAction.DECLINE)
                }
            }
        }

        override fun updateLayout() {
            binding.bottomButtom.setText(R.string.answer_request_button)
            binding.upButton.isVisible = false
        }
    }

    private inner class OutRequestState : State {
        // Do nothing
        override fun upButtonClickAction() {}

        override fun bottomButtonClickAction() {
            viewModel.bottomButtonClick(UserProfileAction.REVOKE)
        }

        override fun updateLayout() {
            binding.bottomButtom.setText(R.string.withdraw_request_button)
            binding.upButton.isVisible = false
        }
    }

    private inner class NoneState : State {
        // Do nothing
        override fun upButtonClickAction() {}

        override fun bottomButtonClickAction() {
            viewModel.bottomButtonClick(UserProfileAction.ADD)
        }

        override fun updateLayout() {
            binding.bottomButtom.setText(R.string.send_friend_request_button)
            binding.upButton.isVisible = false
        }

    }

    // endregion

    companion object {
        private const val TAG = "UserProfileFragment_TAG"
        private const val VK_APP_PACKAGE_NAME = "com.vkontakte.android"
        private const val VK_APP_USER_PAGE_URL = "www.vk.com/id"
    }
}
