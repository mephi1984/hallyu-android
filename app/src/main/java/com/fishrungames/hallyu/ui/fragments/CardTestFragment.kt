package com.fishrungames.hallyu.ui.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.OnRequestCard
import com.fishrungames.hallyu.models.requests.CardTestQuestionRequest
import com.fishrungames.hallyu.models.responses.CardTestQuestionResponse
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.retrofit.HallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import kotlinx.android.synthetic.main.fragment_card_test.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CardTestFragment : BaseFragment() {

    private var hallyuApi: HallyuApi? = null
    private var question: OnRequestCard? = null
    private var answerButtons: MutableList<Button> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_card_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        answerButtons.add(firstWordButton)
        answerButtons.add(secondWordButton)
        answerButtons.add(thirdWordButton)
        answerButtons.add(fourthWordButton)

        firstWordButton.setOnClickListener { checkAnswer( 0) }
        secondWordButton.setOnClickListener { checkAnswer(1) }
        thirdWordButton.setOnClickListener { checkAnswer(2) }
        fourthWordButton.setOnClickListener { checkAnswer(3) }
        nextButton.setOnClickListener { getQuestion() }

        hallyuApi = RetrofitController.getHallyuApiApi()

        getQuestion()

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_cardTest)
    }

    private fun getQuestion() {
        resetQuestionContent()
        getActivityInstance()?.showProgressBar()
        val cardTestQuestionRequest = CardTestQuestionRequest()
        cardTestQuestionRequest.RequestCard = 100
        hallyuApi!!.getCardTestQuestion(cardTestQuestionRequest).enqueue(getCardTestQuestionCallback)
    }

    private fun showQuestion() {
        questionTextView.text = question?.Translation
        firstWordButton.text = question?.Word0
        secondWordButton.text = question?.Word1
        thirdWordButton.text = question?.Word2
        fourthWordButton.text = question?.Word3
        val showContentAnimation = AlphaAnimation(0.0f, 1.0f)
        showContentAnimation.duration = 500
        showContentAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }
            override fun onAnimationEnd(animation: Animation) { getActivityInstance()?.runOnUiThread { contentLayout.visibility = View.VISIBLE } }
            override fun onAnimationRepeat(animation: Animation) { }
        })
        getActivityInstance()?.runOnUiThread { contentCardView.visibility = View.VISIBLE }
        contentLayout.startAnimation(showContentAnimation)
    }

    private fun checkAnswer(index: Int) {
        if (index == question?.CorrectWord) {
            setButtonColor(getAnswerButton(index), R.color.colorRight)
        } else {
            setButtonColor(getAnswerButton(index), R.color.colorWrong)
            setButtonColor(getAnswerButton(question?.CorrectWord!!), R.color.colorRight)
        }
        disableAnswerButtons()
    }

    private fun setButtonColor(button: Button, color: Int) {
        button.background.setColorFilter(ContextCompat.getColor(context!!, color), PorterDuff.Mode.MULTIPLY)
    }

    private fun getAnswerButton(index: Int): Button {
        return when (index) {
            0 -> firstWordButton
            1 -> secondWordButton
            2 -> thirdWordButton
            else -> fourthWordButton
        }
    }

    private fun disableAnswerButtons() {
        for (button in answerButtons) {
            button.isEnabled = false
        }
    }

    private fun enableAnswerButtons() {
        for (button in answerButtons) {
            button.isEnabled = true
        }
    }

    private fun setDefaultAnswerButtonsColor() {
        for (button in answerButtons) {
            setButtonColor(button, R.color.colorAccent)
        }
    }

    private fun resetQuestionContent() {
        setDefaultAnswerButtonsColor()
        enableAnswerButtons()
        getActivityInstance()?.runOnUiThread { contentCardView.visibility = View.INVISIBLE }
        getActivityInstance()?.runOnUiThread { contentLayout.visibility = View.INVISIBLE }
    }

    private val getCardTestQuestionCallback = object : Callback<CardTestQuestionResponse> {
        override fun onResponse(call: Call<CardTestQuestionResponse>?, response: Response<CardTestQuestionResponse>?) {
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

        override fun onFailure(call: Call<CardTestQuestionResponse>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

}