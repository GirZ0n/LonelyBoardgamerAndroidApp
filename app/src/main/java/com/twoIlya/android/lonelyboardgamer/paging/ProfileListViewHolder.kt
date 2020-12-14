package com.twoIlya.android.lonelyboardgamer.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.twoIlya.android.lonelyboardgamer.R
import com.twoIlya.android.lonelyboardgamer.dataClasses.ListProfile
import com.twoIlya.android.lonelyboardgamer.databinding.ProfileListViewItemBinding


class ProfileListViewHolder(private val binding: ProfileListViewItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(profile: ListProfile?, clickListener: (Int) -> Unit) {
        profile?.let {
            showRepoData(profile)
            binding.root.setOnClickListener {
                clickListener(profile.id)
            }
        } ?: run {
            binding.profile = ListProfile(-1, "?", "?")
        }
    }

    private fun showRepoData(profile: ListProfile) {
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
        fun create(parent: ViewGroup): ProfileListViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ProfileListViewItemBinding =
                DataBindingUtil.inflate(inflater, R.layout.profile_list_view_item, parent, false)
            return ProfileListViewHolder(binding)
        }
    }
}
