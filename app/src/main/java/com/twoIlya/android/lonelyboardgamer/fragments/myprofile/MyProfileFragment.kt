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
import com.github.ybq.android.spinkit.style.ThreeBounce
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.activities.error.ErrorActivity
import com.twoIlya.android.lonelyboardgamer.activities.login.LoginActivity
import com.twoIlya.android.lonelyboardgamer.dataClasses.EventType
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
    ): View? {
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
                EventType.Warning -> {
                    updateLogoutButton(true)
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

        binding.logoutButton.setOnClickListener {
            updateLogoutButton(false)
            viewModel.logout()
        }
    }

    private fun updateLogoutButton(isEnabled: Boolean) {
        binding.logoutButton.isEnabled = isEnabled

        when (isEnabled) {
            true -> {
                val leftDrawable = binding.logoutButton.compoundDrawables.first()
                if (leftDrawable is ThreeBounce) {
                    leftDrawable.stop()
                }
                binding.logoutButton.setCompoundDrawables(null, null, null, null)
            }
            false -> {
                val dots = ThreeBounce()
                dots.setBounds(0, 0, 100, 100)
                binding.logoutButton.setCompoundDrawables(dots, null, null, null)
                dots.start()
            }
        }
    }


    companion object {
        private const val TAG = "MyProfileFragment_TAG"
    }
}
