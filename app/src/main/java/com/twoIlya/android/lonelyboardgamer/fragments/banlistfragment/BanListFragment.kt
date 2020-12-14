package com.twoIlya.android.lonelyboardgamer.fragments.banlistfragment

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
import com.twoIlya.android.lonelyboardgamer.dataClasses.EventType
import com.twoIlya.android.lonelyboardgamer.databinding.FragmentBanListBinding
import com.twoIlya.android.lonelyboardgamer.databinding.FragmentFriendsListBinding
import com.twoIlya.android.lonelyboardgamer.fragments.friendslist.FriendsListViewModel
import com.twoIlya.android.lonelyboardgamer.paging.ListAdapter
import com.twoIlya.android.lonelyboardgamer.paging.LoadStateAdapter
import kotlinx.coroutines.launch

class BanListFragment : Fragment() {
    private lateinit var binding: FragmentBanListBinding
    private val viewModel: BanListViewModel by lazy {
        ViewModelProvider(this).get(
            BanListViewModel::class.java
        )
    }
    private lateinit var adapter: ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_ban_list, container, false
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

        viewModel.getBanList()
    }

    private fun initAdapter() {
        adapter = ListAdapter { id ->
            val bundle = Bundle()
            bundle.putInt("id", id)
            findNavController().navigate(
                R.id.action_banListFragment_to_userProfileFragment,
                bundle
            )
        }

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { adapter.retry() },
            footer = LoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener(viewModel::loadStateListener)

        viewModel.banListLiveData.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
    }

    companion object {
        private const val TAG = "BanListFragment_TAG"
    }
}