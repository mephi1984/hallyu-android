package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.fishrungames.hallyu.R
import kotlinx.android.synthetic.main.fragment_numeral_test.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

class NumeralTestFragment : BaseFragment() {

    private val TEXT_TO_NUMBER_TEST = 0
    private val NUMBER_TO_TEXT_TEST = 1

    private var currentTestType: Int? = null
    private var digits: Int? = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_numeral_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDigits()
        setupNumberToTextTest()

        digitsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                setDigits()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) { }
            override fun onStopTrackingTouch(seekBar: SeekBar) { }
        })

        startButton.setOnClickListener { startTest() }

    }

    private fun setDigits() {
        digits = digitsSeekBar.progress + 1
        digitsTextView.text = digits.toString()
    }

    private fun setupNumberToTextTest() {
        currentTestType = NUMBER_TO_TEXT_TEST
        testLabelTextView.text = getString(R.string.text_numeralTestFragment_number)
        answerLabelTextView.text = getString(R.string.text_numeralTestFragment_enterTheNumberInKorean)
    }

    private fun setupTextToNumberTest() {
        currentTestType = TEXT_TO_NUMBER_TEST
        testLabelTextView.text = getString(R.string.text_numeralTestFragment_numberInKorean)
        answerLabelTextView.text = getString(R.string.text_numeralTestFragment_enterTheNumber)
    }

    private fun startTest() {
        val showAnimation = AlphaAnimation(0.0f, 1.0f)
        showAnimation.duration = 500
        showAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                getActivityInstance()?.runOnUiThread { startButton.visibility = View.INVISIBLE }
            }
            override fun onAnimationEnd(animation: Animation) {
                getActivityInstance()?.runOnUiThread { contentCardView.visibility = View.VISIBLE }
            }
            override fun onAnimationRepeat(animation: Animation) { }
        })
        contentCardView.startAnimation(showAnimation)
    }

}