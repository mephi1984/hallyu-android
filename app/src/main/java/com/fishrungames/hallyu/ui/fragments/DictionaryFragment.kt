package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.dictionary.CompoundVerb
import com.fishrungames.hallyu.models.dictionary.DictionaryResponse
import com.fishrungames.hallyu.models.dictionary.Lesson
import com.fishrungames.hallyu.models.dictionary.Word
import com.fishrungames.hallyu.models.requests.TranslateTextRequest
import com.fishrungames.hallyu.models.responses.TranslateTextResponse
import com.fishrungames.hallyu.ui.adapters.DictionaryAdapter
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.retrofit.HallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import kotlinx.android.synthetic.main.fragment_dictionary.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DictionaryFragment : BaseFragment() {

    private var dictionaryAdapter: DictionaryAdapter? = null
    private var words: MutableList<Word> = mutableListOf()
    private var compoundVerbs: MutableList<CompoundVerb> = mutableListOf()
    private var hallyuApi: HallyuApi? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDictionaryRecyclerView()

        hallyuApi = RetrofitController.getHallyuApi()

        translateButton.setOnClickListener { translateText() }
        clearButton.setOnClickListener { clearText() }

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_main)
    }

    private fun initDictionaryRecyclerView() {
        val layoutManager = LinearLayoutManager(context!!)
        dictionaryAdapter = DictionaryAdapter(compoundVerbs, words, context!!, object : DictionaryAdapter.LessonClickListener {
            override fun onClick(lesson: Lesson) {
                getActivityInstance()?.openLessonFragment(lesson)
            }
        })
        dictionaryRecyclerView.layoutManager = layoutManager
        dictionaryRecyclerView.adapter = dictionaryAdapter
        dictionaryRecyclerView.setHasFixedSize(true)
    }


    private fun hideDictionaryRecycler() {
        getActivityInstance()?.runOnUiThread { dictionaryRecyclerView.visibility = View.INVISIBLE }
    }

    private fun showDictionaryRecycler() {
        val showContentAnimation = AlphaAnimation(0.0f, 1.0f)
        showContentAnimation.duration = 800
        showContentAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }
            override fun onAnimationEnd(animation: Animation) {
                if (view == null) {
                    return
                }
                getActivityInstance()?.runOnUiThread { dictionaryRecyclerView.visibility = View.VISIBLE }
            }
            override fun onAnimationRepeat(animation: Animation) { }
        })
        dictionaryRecyclerView.startAnimation(showContentAnimation)
    }

    private fun translateText() {
        if (textInKoreanEditText.text.isEmpty()) {
            return
        }
        hideDictionaryRecycler()
        getActivityInstance()?.hideInputMethod()
        words.clear()
        compoundVerbs.clear()
        dictionaryAdapter?.notifyDataSetChanged()
        val translateTextRequest = TranslateTextRequest()
        translateTextRequest.RequestWordTranslation = textInKoreanEditText.text.toString()
        getActivityInstance()?.showProgressBar()
        hallyuApi!!.translateKoreanText(translateTextRequest).enqueue(translateTextCallback)
    }

    private fun clearText() {
        textInKoreanEditText.setText("")
    }

    private val translateTextCallback = object : Callback<DictionaryResponse> {
        override fun onResponse(call: Call<DictionaryResponse>?, response: Response<DictionaryResponse>?) {
            if (activity == null) {
                retainInstance
            }
            getActivityInstance()?.hideProgressBar()
            val dictionaryResponse = response?.body()
            val resultTable = dictionaryResponse?.resultTable
            val complexVerbResultArr = dictionaryResponse?.complexVerbResultArr

            if (resultTable != null) {
                words.addAll(dictionaryResponse.resultTable!!)
                words.addAll(dictionaryResponse.resultTable!!)
                words.addAll(dictionaryResponse.resultTable!!)
            }

            if (complexVerbResultArr != null) {
                compoundVerbs.addAll(dictionaryResponse.complexVerbResultArr!!)
            }

            dictionaryAdapter?.notifyDataSetChanged()
            showDictionaryRecycler()

        }

        override fun onFailure(call: Call<DictionaryResponse>?, t: Throwable?) {
            if (activity == null) {
                retainInstance
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

}