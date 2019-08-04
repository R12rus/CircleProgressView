package com.r12

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor

interface ColorChooser {

    @ColorInt
    fun getColor(position: Int): Int

    class EvenColorChooser(
            @ColorInt var evenColor: Int,
            @ColorInt var oddColor: Int
    ) : ColorChooser {

        constructor(
                context: Context,
                @ColorRes evenColor: Int,
                @ColorRes oddColor: Int
        ) : this(
                getColor(context, evenColor),
                getColor(context, oddColor)
        )

        override fun getColor(position: Int) = if (position % 2 == 0) evenColor else oddColor

    }

}