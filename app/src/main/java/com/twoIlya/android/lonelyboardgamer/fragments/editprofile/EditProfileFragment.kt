package com.twoIlya.android.lonelyboardgamer.fragments.editprofile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LocationPickerActivity
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.activities.error.ErrorActivity
import com.twoIlya.android.lonelyboardgamer.activities.login.LoginActivity
import com.twoIlya.android.lonelyboardgamer.dataClasses.EventType
import com.twoIlya.android.lonelyboardgamer.databinding.FragmentEditProfileBinding
import com.twoIlya.android.lonelyboardgamer.repository.PreferencesRepository


class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProvider(this).get(
            EditProfileViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_profile, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.locationButton.setOnClickListener {
            val locationPickerIntent = locationPickerSetup()
            startActivityForResult(locationPickerIntent, 1)
        }

        categoriesSpinnerSetup()
        mechanicsSpinnerSetup()

        viewModel.categories.observe(viewLifecycleOwner) {
            categoriesSpinnerSetup(it)
        }

        viewModel.mechanics.observe(viewLifecycleOwner) {
            mechanicsSpinnerSetup(it)
        }

        viewModel.events.observe(viewLifecycleOwner) {
            Log.d(TAG, "Event: $it")

            when (it.isHandle) {
                true -> return@observe
                false -> it.isHandle = true
            }

            when (it.type) {
                EventType.Warning -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
                EventType.Error -> {
                    val intent = ErrorActivity.newActivity(requireContext(), it.message)
                    startActivity(intent)
                    activity?.finish()
                }
                EventType.Move -> {
                    when (it.message) {
                        "Login" -> {
                            val intent = LoginActivity.newActivity(requireContext())
                            startActivity(intent)
                            activity?.finish()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 1) {
                val address = data.getStringExtra(LOCATION_ADDRESS) ?: ""
                viewModel.updateAddress(address)
            }
        }
    }

    private fun categoriesSpinnerSetup(categories: List<String> = emptyList()) {
        val categoryMultiSpinner = binding.categoryMultiSpinner
        categoryMultiSpinner.isSearchEnabled = false
        categoryMultiSpinner.setClearText("Clear")
        categoryMultiSpinner.setItems(PreferencesRepository.getCategories(categories)) { items ->
            viewModel.updateCategories(items)
            Log.d(TAG, "Categories: $items")
        }
    }

    private fun mechanicsSpinnerSetup(mechanics: List<String> = emptyList()) {
        val mechanicsMultiSpinner = binding.mechanicsMultiSpinner
        mechanicsMultiSpinner.isSearchEnabled = false
        mechanicsMultiSpinner.setClearText("Clear")
        mechanicsMultiSpinner.setItems(PreferencesRepository.getMechanics(mechanics)) { items ->
            viewModel.updateMechanics(items)
            Log.d(TAG, "Mechanics: $items")
        }
    }

    private fun locationPickerSetup(): Intent {
        return LocationPickerActivity.Builder()
            .withLocation(59.938706, 30.315033)
            .withGeolocApiKey("AIzaSyBxNpr3sAzxUHf8dk9T3GoPjcMgsK657Rw")
            .withDefaultLocaleSearchZone()
            .shouldReturnOkOnBackPressed()
            .withCityHidden() // Прячем название города
            .withZipCodeHidden() // Прячем ZIP
            .withSatelliteViewHidden() // Прячем вид со спутника
            .withVoiceSearchHidden() // Прячем голосовой поиск
            .withUnnamedRoadHidden() // Прячем неизвестные дороги
            .build(requireContext())
    }

    companion object {
        private const val TAG = "EditProfile_TAG"
    }
}