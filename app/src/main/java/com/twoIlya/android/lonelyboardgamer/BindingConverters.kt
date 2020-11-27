package com.twoIlya.android.lonelyboardgamer

import androidx.databinding.BindingConversion
import androidx.lifecycle.LiveData

@BindingConversion
fun convertListToString(list: LiveData<List<String>>) = list.value?.joinToString(", ") ?: ""
