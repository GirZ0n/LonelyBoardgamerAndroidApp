package com.twoIlya.android.lonelyboardgamer.fragments.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
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
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LocationPickerActivity
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.activities.error.ErrorActivity
import com.twoIlya.android.lonelyboardgamer.activities.login.LoginActivity
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.databinding.FragmentRegistrationBinding
import com.twoIlya.android.lonelyboardgamer.repository.PreferencesRepository


class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private val viewModel: RegistrationViewModel by lazy {
        ViewModelProvider(this).get(
            RegistrationViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_registration, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val locationPickerIntent = locationPickerSetup()
        binding.address.setOnClickListener {
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

        binding.aboutMe.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.registration_fragment_about_me_dialog_title)
                positiveButton()

                input(
                    prefill = viewModel.aboutMe.value,
                    inputType = InputType.TYPE_CLASS_TEXT,
                    maxLength = RegistrationViewModel.MAX_LENGTH_OF_DESCRIPTION,
                    allowEmpty = true,
                    hintRes = R.string.registration_fragment_about_me_dialog_hint
                ) { _, description ->
                    viewModel.updateAboutMe(description.toString())
                }
            }
        }

        viewModel.events.observe(viewLifecycleOwner) {
            Log.d(TAG, "Event: $it")

            when (it.isHandle) {
                true -> return@observe
                false -> it.isHandle = true
            }

            when (it.type) {
                Event.Type.NOTIFICATION -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
                Event.Type.ERROR -> {
                    val intent = ErrorActivity.newActivity(requireContext(), it.message)
                    startActivity(intent)
                    activity?.finish()
                }
                Event.Type.MOVE -> {
                    when (it.message) {
                        "MyProfile" -> {
                            findNavController().navigate(R.id.action_registrationFragment_to_myProfile)
                            return@observe
                        }
                        "Login" -> {
                            val intent = LoginActivity.newActivity(requireContext())
                            startActivity(intent)
                            activity?.finish()
                        }
                        else -> {
                            val intent =
                                ErrorActivity.newActivity(
                                    requireContext(),
                                    "RegistrationFragment: unknown destination"
                                )
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
        val items = PreferencesRepository.categories
        val initialSelection = PreferencesRepository.getIndicesOfSelectedCategories(categories)

        binding.categories.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.registration_fragment_categories_dialog_title)
                positiveButton()

                listItemsMultiChoice(
                    items = items,
                    initialSelection = initialSelection,
                    allowEmptySelection = true
                ) { _, indices, _ ->
                    viewModel.updateCategories(indices)
                }
            }
        }
    }

    private fun mechanicsSpinnerSetup(mechanics: List<String> = emptyList()) {
        val items = PreferencesRepository.mechanics
        val initialSelection = PreferencesRepository.getIndicesOfSelectedMechanics(mechanics)

        binding.mechanics.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.registration_fragment_mechanics_dialog_title)
                positiveButton()

                listItemsMultiChoice(
                    items = items,
                    initialSelection = initialSelection,
                    allowEmptySelection = true
                ) { _, indices, _ ->
                    viewModel.updateMechanics(indices)
                }
            }
        }
    }

    private fun locationPickerSetup(): Intent {
        val apiKey = getString(R.string.google_maps_api_key)

        return LocationPickerActivity.Builder()
            .withLocation(59.938706, 30.315033)
            .withGeolocApiKey(apiKey)
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
        private const val TAG = "Registration_TAG"
    }
}
