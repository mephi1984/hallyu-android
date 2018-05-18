package com.fishrungames.hallyu.ui.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import kotlinx.android.synthetic.main.fragment_posts.*
import android.view.inputmethod.EditorInfo
import android.animation.Animator

class PostsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchPostsEditText.setOnEditorActionListener({ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchPosts()
                return@setOnEditorActionListener true
            }
            false
        })

        searchPostsImageView.setOnClickListener { searchPosts() }

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_main)
    }

    private fun searchPosts() {
        getActivityInstance()?.hideInputMethod()
        val searchPostsViewLayoutParams = searchPostsCardView.layoutParams as ViewGroup.MarginLayoutParams
        val objectAnimator = ObjectAnimator.ofFloat(
                searchPostsCardView,
                "y",
                searchPostsCardView.y,
                0f + searchPostsViewLayoutParams.leftMargin)
        objectAnimator.duration = 200
        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) { }
            override fun onAnimationCancel(animation: Animator) { }
            override fun onAnimationRepeat(animation: Animator) { }
            override fun onAnimationEnd(animation: Animator) {
                getActivityInstance()?.showProgressBar()
            }
        })
        objectAnimator.start()
    }

}