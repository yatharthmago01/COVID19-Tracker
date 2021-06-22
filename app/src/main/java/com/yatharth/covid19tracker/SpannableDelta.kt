package com.yatharth.covid19tracker

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan

class SpannableDelta(text: String, color: String, start: Int): SpannableString(text) {
    init {
        setSpan(ForegroundColorSpan(Color.parseColor(color)), start, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}