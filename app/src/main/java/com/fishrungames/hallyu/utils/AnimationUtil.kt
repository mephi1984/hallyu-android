package com.fishrungames.hallyu.utils

import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.animation.*

object AnimationUtil {

    fun expandView(v: View, initialHeight: Int, contentView: View?) {
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.layoutParams.height = LayoutParams.WRAP_CONTENT
                    if (contentView != null) {
                        showView(contentView)
                    }
                } else {
                    v.layoutParams.height = (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }
            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong() * 4
        v.startAnimation(a)
    }

    fun collapseView(v: View) {
        val initialHeight = v.measuredHeight
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }
            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = ((initialHeight / v.context.resources.displayMetrics.density).toInt()).toLong() * 4
        v.startAnimation(a)
    }

    fun showView(v: View) {
        v.visibility = View.INVISIBLE
        val showContentAnimation = AlphaAnimation(0.0f, 1.0f)
        showContentAnimation.duration = 800
        showContentAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                v.visibility = View.VISIBLE
                v.requestLayout()
            }
        })
        v.startAnimation(showContentAnimation)
    }

}