package com.fishrungames.hallyu.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.RelativeLayout
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

    private var dictionarySize: Int = 100
    private var hallyuApi: HallyuApi? = null
    private var question: OnRequestCard? = null
    private var answerLayouts: MutableList<RelativeLayout> = mutableListOf()
    private var dictionaryLayouts: MutableList<RelativeLayout> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_card_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        answerLayouts.add(firstWordLayout)
        answerLayouts.add(secondWordLayout)
        answerLayouts.add(thirdWordLayout)
        answerLayouts.add(fourthWordLayout)

        dictionaryLayouts.add(oneHundredWordsLayout)
        dictionaryLayouts.add(thousandWordsLayout)
        dictionaryLayouts.add(tenThousandWordsLayout)

        firstWordLayout.setOnClickListener { checkAnswer( 0) }
        secondWordLayout.setOnClickListener { checkAnswer(1) }
        thirdWordLayout.setOnClickListener { checkAnswer(2) }
        fourthWordLayout.setOnClickListener { checkAnswer(3) }

        oneHundredWordsLayout.setOnClickListener { setDictionarySize(100, oneHundredWordsLayout) }
        thousandWordsLayout.setOnClickListener { setDictionarySize(1000, thousandWordsLayout) }
        tenThousandWordsLayout.setOnClickListener { setDictionarySize(10000, tenThousandWordsLayout) }

        nextButton.setOnClickListener { getQuestion() }

        hallyuApi = RetrofitController.getHallyuApi()

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
        cardTestQuestionRequest.RequestCard = dictionarySize
        hallyuApi!!.getCardTestQuestion(cardTestQuestionRequest).enqueue(getCardTestQuestionCallback)
    }

    private fun showQuestion() {
        if (view == null) {
            return
        }
        questionTextView.text = question?.Translation
        firstWordTextView.text = question?.Word0
        secondWordTextView.text = question?.Word1
        thirdWordTextView.text = question?.Word2
        fourthWordTextView.text = question?.Word3
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

    private fun checkAnswer(index: Int) {
        if (index == question?.CorrectWord) {
            setLayoutDrawable(getAnswerLayout(index), getDrawable(R.drawable.shape_card_test_right))
        } else {
            setLayoutDrawable(getAnswerLayout(index), getDrawable(R.drawable.shape_card_test_wrong))
            setLayoutDrawable(getAnswerLayout(question?.CorrectWord!!), getDrawable(R.drawable.shape_card_test_right))
        }
        disableAnswerLayouts()
    }

    private fun getDrawable(id: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context!!.resources.getDrawable(id, context!!.theme)
        } else {
            context!!.resources.getDrawable(id)
        }
    }


    private fun setLayoutDrawable(layout: RelativeLayout, drawable: Drawable) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(drawable)
        } else {
            layout.background = drawable
        }
    }

    private fun getAnswerLayout(index: Int): RelativeLayout {
        return when (index) {
            0 -> firstWordLayout
            1 -> secondWordLayout
            2 -> thirdWordLayout
            else -> fourthWordLayout
        }
    }

    private fun setDictionarySize(size: Int, dictionaryLayout: RelativeLayout) {
        dictionarySize = size
        for (layout in dictionaryLayouts) {
            setLayoutDrawable(layout, getDrawable(R.drawable.shape_card_test_dictionary_default))
        }
        setLayoutDrawable(dictionaryLayout, getDrawable(R.drawable.shape_card_test_dictionary_selected))
    }

    private fun disableAnswerLayouts() {
        for (layout in answerLayouts) {
            layout.isEnabled = false
        }
    }

    private fun enableAnswerLayouts() {
        for (layout in answerLayouts) {
            layout.isEnabled = true
        }
    }

    private fun setDefaultAnswerLayoutsDrawable() {
        for (layout in answerLayouts) {
            setLayoutDrawable(layout, getDrawable(R.drawable.shape_card_test_default))
        }
    }

    private fun resetQuestionContent() {
        setDefaultAnswerLayoutsDrawable()
        enableAnswerLayouts()
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