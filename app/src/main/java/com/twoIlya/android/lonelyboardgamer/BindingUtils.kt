package com.twoIlya.android.lonelyboardgamer

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.lifecycle.LiveData
import com.github.ybq.android.spinkit.style.ThreeBounce
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

@BindingAdapter("bind:isLoading")
fun updateDrawable(view: Button, isLoading: Boolean) {
    when (isLoading) {
        true -> {
            val dots = ThreeBounce()
            dots.setBounds(0, 0, 100, 100)
            dots.start()
            view.setCompoundDrawables(dots, null, null, null)
        }
        false -> {
            val leftDrawable = view.compoundDrawables.first()
            if (leftDrawable is ThreeBounce) {
                leftDrawable.stop()
            }
            view.setCompoundDrawables(null, null, null, null)
        }
    }
}

@BindingAdapter("bind:isVisible")
fun setVisibility(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}
