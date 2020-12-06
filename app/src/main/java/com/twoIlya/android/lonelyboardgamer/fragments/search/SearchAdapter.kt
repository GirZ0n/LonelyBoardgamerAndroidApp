package com.twoIlya.android.lonelyboardgamer.fragments.search

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.twoIlya.android.lonelyboardgamer.OnItemClickListener
import com.twoIlya.android.lonelyboardgamer.dataClasses.SearchProfile

class SearchAdapter(private val itemClickListener: OnItemClickListener) :
    PagingDataAdapter<SearchProfile, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchProfileViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val profile = getItem(position)
        profile?.let {
            (holder as SearchProfileViewHolder).bind(profile, itemClickListener)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<SearchProfile>() {
            override fun areItemsTheSame(oldItem: SearchProfile, newItem: SearchProfile): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: SearchProfile,
                newItem: SearchProfile
            ): Boolean =
                oldItem.firstName == newItem.firstName &&
                        oldItem.secondName == newItem.secondName &&
                        oldItem.distance == oldItem.distance
        }
    }
}
