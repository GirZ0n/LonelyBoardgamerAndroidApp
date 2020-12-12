package com.twoIlya.android.lonelyboardgamer.fragments.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.dataClasses.SearchProfile
import com.twoIlya.android.lonelyboardgamer.databinding.SearchProfileViewItemBinding


class SearchViewHolder(private val binding: SearchProfileViewItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(profile: SearchProfile?, clickListener: (Int) -> Unit) {
        if (profile == null) {
            binding.profile = SearchProfile(-1, "?", "?", "?")
        } else {
            showRepoData(profile)
            binding.root.setOnClickListener {
                clickListener(profile.id)
            }
        }
    }

    private fun showRepoData(profile: SearchProfile) {
        binding.profile = profile
        binding.executePendingBindings()

        val imageUrl = "https://eu.ui-avatars.com/api/" +
                "?name=${profile.firstName}+${profile.secondName}" +
                "&bold=true" +
                "&size=512" +
                "&rounded=true" +
                "&color=fff" +
                "&background=000"
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_user_placeholder)
            .into(binding.avatar)
    }

    companion object {
        fun create(parent: ViewGroup): SearchViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: SearchProfileViewItemBinding =
                DataBindingUtil.inflate(inflater, R.layout.search_profile_view_item, parent, false)
            return SearchViewHolder(binding)
        }
    }
}
