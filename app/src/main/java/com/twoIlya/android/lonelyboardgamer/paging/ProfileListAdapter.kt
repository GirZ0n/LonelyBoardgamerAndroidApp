package com.twoIlya.android.lonelyboardgamer.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.twoIlya.android.lonelyboardgamer.dataClasses.ListProfile

class ProfileListAdapter(private val itemClickListener: (Int) -> Unit) :
    PagingDataAdapter<ListProfile, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProfileListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val profile = getItem(position)
        profile?.let {
            (holder as ProfileListViewHolder).bind(profile, itemClickListener)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<ListProfile>() {
            override fun areItemsTheSame(oldItem: ListProfile, newItem: ListProfile): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ListProfile,
                newItem: ListProfile
            ): Boolean =
                oldItem.firstName == newItem.firstName &&
                        oldItem.secondName == newItem.secondName
        }
    }
}
