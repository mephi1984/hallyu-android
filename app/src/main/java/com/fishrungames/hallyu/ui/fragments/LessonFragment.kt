package com.fishrungames.hallyu.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.dictionary.Lesson
import kotlinx.android.synthetic.main.fragment_lesson.*

class LessonFragment : BaseFragment() {

    private var lesson: Lesson? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lesson, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromArguments()

        lessonWebView.settings.javaScriptEnabled = true
        lessonWebView.loadUrl(lesson?.path)

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = lesson?.title.toString()
    }

    private fun getDataFromArguments() {
        if (lesson == null && arguments != null) {
            lesson = arguments!!.getSerializable("lesson") as Lesson
        }
    }

}