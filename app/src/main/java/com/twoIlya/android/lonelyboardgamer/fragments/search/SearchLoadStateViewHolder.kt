package com.twoIlya.android.lonelyboardgamer.fragments.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.databinding.LoadStateFooterViewItemBinding

class SearchLoadStateViewHolder(
    private val binding: LoadStateFooterViewItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState !is LoadState.Loading
        binding.errorMsg.isVisible = loadState !is LoadState.Loading
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): SearchLoadStateViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: LoadStateFooterViewItemBinding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.load_state_footer_view_item,
                    parent,
                    false
                )
            return SearchLoadStateViewHolder(binding, retry)
        }
    }
}
