package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.fishrungames.hallyu.R
import kotlinx.android.synthetic.main.fragment_numeral_test.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.fishrungames.hallyu.models.OnRequestChineseNumberRecognize
import com.fishrungames.hallyu.models.requests.NumeralTestQuestionRequest
import com.fishrungames.hallyu.models.responses.NumeralTestQuestionResponse
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.retrofit.HallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NumeralTestFragment : BaseFragment() {

    private val TEXT_TO_NUMBER_TEST = 0
    private val NUMBER_TO_TEXT_TEST = 1

    private var currentTestType: Int? = null
    private var digits: Int? = 1
    private var hallyuApi: HallyuApi? = null
    private var question: OnRequestChineseNumberRecognize? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_numeral_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hallyuApi = RetrofitController.getHallyuApi()

        setDigits()
        getDataFromArguments()

        digitsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                setDigits()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) { }
            override fun onStopTrackingTouch(seekBar: SeekBar) { }
        })

        startButton.setOnClickListener {
            getActivityInstance()?.runOnUiThread { startButton.visibility = View.GONE }
            getQuestion()
        }
        nextButton.setOnClickListener { getQuestion() }
        okButton.setOnClickListener { checkAnswer() }

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_numeralTest)
    }

    private fun setDigits() {
        digits = digitsSeekBar.progress + 1
        digitsTextView.text = digits.toString()
    }

    private fun getDataFromArguments() {
        if (arguments != null) {
            val testType = arguments!!.getInt("testType")
            if (testType == 0) {
                setupNumberToTextTest()
            } else {
                setupTextToNumberTest()
            }
        }
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

    private fun checkAnswer() {
        if (currentTestType == TEXT_TO_NUMBER_TEST) {
            if (answerEditText.text.toString() == question?.number) {
                rightAnswer()
            } else {
                wrongAnswer()
            }
        } else {
            if (answerEditText.text.toString() == question?.numberStr) {
                rightAnswer()
            } else {
                wrongAnswer()
            }
        }
    }

    private fun rightAnswer() {
        getActivityInstance()?.runOnUiThread { resultLayout.visibility = View.VISIBLE }
        resultImageView.setImageResource(R.mipmap.ic_right_24dp)
        resultTextView.text = getString(R.string.text_numeralTestFragment_right)
    }

    private fun wrongAnswer() {
        getActivityInstance()?.runOnUiThread { resultLayout.visibility = View.VISIBLE }
        getActivityInstance()?.runOnUiThread { rightAnswerLayout.visibility = View.VISIBLE }
        resultImageView.setImageResource(R.mipmap.ic_wrong_24dp)
        resultTextView.text = getString(R.string.text_numeralTestFragment_wrong)
        if (currentTestType == TEXT_TO_NUMBER_TEST) {
            rightAnswerTextView.text = question?.number
        } else {
            rightAnswerTextView.text = question?.numberStr
        }
    }

    private fun getQuestion() {
        getActivityInstance()?.runOnUiThread { contentCardView.visibility = View.INVISIBLE }
        getActivityInstance()?.runOnUiThread { contentLayout.visibility = View.INVISIBLE }
        getActivityInstance()?.runOnUiThread { rightAnswerLayout.visibility = View.INVISIBLE }
        getActivityInstance()?.runOnUiThread { resultLayout.visibility = View.INVISIBLE }
        answerEditText.setText("")
        getActivityInstance()?.showProgressBar()
        val numeralTestQuestionRequest = NumeralTestQuestionRequest()
        numeralTestQuestionRequest.RequestChineseNumberRecognize = digits.toString()
        hallyuApi!!.getNumeralTestQuestion(numeralTestQuestionRequest).enqueue(getNumeralTestQuestionCallback)
    }

    private fun showQuestion() {
        if (view == null) {
            return
        }
        if (currentTestType == TEXT_TO_NUMBER_TEST) {
            testQuestionTextView.text = question?.numberStr
            answerLabelTextView.text = getString(R.string.text_numeralTestFragment_enterTheNumber)
        } else {
            testQuestionTextView.text = question?.number
            answerLabelTextView.text = getString(R.string.text_numeralTestFragment_enterTheNumberInKorean)
        }
        val showContentAnimation = AlphaAnimation(0.0f, 1.0f)
        showContentAnimation.duration = 500
        showContentAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }
            override fun onAnimationEnd(animation: Animation) {
                if (view == null) {
                    return
                }
                getActivityInstance()?.runOnUiThread { contentLayout.visibility = View.VISIBLE } }
            override fun onAnimationRepeat(animation: Animation) { }
        })
        getActivityInstance()?.runOnUiThread { contentCardView.visibility = View.VISIBLE }
        contentLayout.startAnimation(showContentAnimation)
    }

    private val getNumeralTestQuestionCallback = object : Callback<NumeralTestQuestionResponse> {
        override fun onResponse(call: Call<NumeralTestQuestionResponse>?, response: Response<NumeralTestQuestionResponse>?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            if (response?.isSuccessful!!) {
                if (response.body()?.haveQuestion()!!) {
                    question = response.body()?.question
                    showQuestion()
                }
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<NumeralTestQuestionResponse>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

}