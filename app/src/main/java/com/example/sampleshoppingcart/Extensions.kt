package com.example.sampleshoppingcart

import androidx.annotation.DrawableRes

@DrawableRes
inline fun String.mapToDrawableId(): Int {
    return when (this) {
        "americano.png" -> R.drawable.americano
        "cappuccino.png" -> R.drawable.cappuccino
        "espresso.png" -> R.drawable.espresso
        "honeyroseyuzu.png" -> R.drawable.honey_rose_yuzu
        "iced_americano.png" -> R.drawable.iced_americano
        "iced_latte.png" -> R.drawable.iced_latte
        "iced_cappuccino.png" -> R.drawable.iced_cappuccino
        "iced_espresso.png" -> R.drawable.iced_espresso
        "icedvanillalatte.png" -> R.drawable.iced_vanilla_latte
        "vanilla_latte.png" -> R.drawable.vanilla_latte
        "latte.png" -> R.drawable.latte
        else -> R.drawable.ic_launcher_background
    }
}
