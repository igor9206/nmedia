package ru.netology.nmedia.extension

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import kotlin.math.ceil


fun ImageView.loadAvatar(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_loading_100dp)
        .error(R.drawable.ic_error_100dp)
        .timeout(10_000)
        .circleCrop()
        .into(this)
}

fun ImageView.load(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_loading_100dp)
        .error(R.drawable.ic_error_100dp)
        .timeout(10_000)
        .into(this)
}

class ResizableImageView(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d = drawable
        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height =
                ceil((width.toFloat() * d.intrinsicHeight.toFloat() / d.intrinsicWidth.toFloat()).toDouble())
                    .toInt()
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}