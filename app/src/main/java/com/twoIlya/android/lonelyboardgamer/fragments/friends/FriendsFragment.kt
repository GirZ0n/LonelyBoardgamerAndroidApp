package com.twoIlya.android.lonelyboardgamer.fragments.friends

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
import com.twoIlya.android.lonelyboardgamer.databinding.FragmentFriendsBinding
import com.twoIlya.android.lonelyboardgamer.paging.LoadStateAdapter
import com.twoIlya.android.lonelyboardgamer.paging.ListAdapter
import kotlinx.coroutines.launch

class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentFriendsBinding
    private val viewModel: FriendsViewModel by lazy {
        ViewModelProvider(this).get(
            FriendsViewModel::class.java
        )
    }
    private lateinit var adapter: ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_friends, container, false
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
                Event.Type.NOTIFICATION -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
                Event.Type.ERROR -> {
                    val intent = ErrorActivity.newActivity(requireContext(), it.message)
                    startActivity(intent)
                    activity?.finish()
                }
                Event.Type.MOVE -> {
                    val intent = when (it.message) {
                        "Login" -> LoginActivity.newActivity(requireContext())
                        else -> ErrorActivity.newActivity(
                            requireContext(),
                            "FriendsFragment: unknown destination"
                        )
                    }
                    startActivity(intent)
                    activity?.finish()
                }
            }
        }

        viewModel.getFriends()
    }

    private fun initAdapter() {
        adapter = ListAdapter { id ->
            val bundle = Bundle()
            bundle.putInt("id", id)
            findNavController().navigate(
                R.id.action_friendsFragment_to_userProfileFragment,
                bundle
            )
        }

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { adapter.retry() },
            footer = LoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener(viewModel::loadStateListener)

        viewModel.friendsLiveData.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
    }

    companion object {
        private const val TAG = "FriendsListFragment_TAG"
    }
}
