package com.onemillionbot.sdk.core

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

fun interface ViewHolderFactory<T : RecyclerView.ViewHolder> : (ViewGroup, Int) -> T
