package com.twoIlya.android.lonelyboardgamer

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.lifecycle.LiveData
import com.squareup.picasso.Picasso


@BindingConversion
fun convertListToString(list: LiveData<List<String>>) = list.value?.joinToString(", ") ?: ""

@BindingAdapter("bind:imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {
    Picasso
        .get()
        .load(imageUrl)
        .placeholder(R.drawable.ic_user_placeholder)
        .into(view)
}
