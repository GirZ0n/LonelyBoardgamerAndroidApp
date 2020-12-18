package com.twoIlya.android.lonelyboardgamer.fragments.inrequests

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.activities.error.ErrorActivity
import com.twoIlya.android.lonelyboardgamer.activities.login.LoginActivity
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.databinding.FragmentInRequestsBinding
import com.twoIlya.android.lonelyboardgamer.paging.ListAdapter
import com.twoIlya.android.lonelyboardgamer.paging.LoadStateAdapter
import kotlinx.coroutines.launch

class InRequestsFragment : Fragment() {
    private lateinit var binding: FragmentInRequestsBinding
    private val viewModel: InRequestsViewModel by lazy {
        ViewModelProvider(this).get(
            InRequestsViewModel::class.java
        )
    }
    private lateinit var adapter: ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_in_requests, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.retryButton.setOnClickListener { adapter.retry() }

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
                        else -> ErrorActivity.newActivity(requireContext(), "Unknown destination")
                    }
                    startActivity(intent)
                    activity?.finish()
                }
            }
        }

        viewModel.getInRequests()
    }

    private fun initAdapter() {
        adapter = ListAdapter { id ->
            val bundle = Bundle()
            bundle.putInt("id", id)
            findNavController().navigate(
                R.id.action_inRequestsFragment_to_userProfileFragment,
                bundle
            )
        }

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { adapter.retry() },
            footer = LoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener(viewModel::loadStateListener)

        viewModel.inRequestsLiveData.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
    }

    companion object {
        private const val TAG = "InRequestsFragment_TAG"
    }
}