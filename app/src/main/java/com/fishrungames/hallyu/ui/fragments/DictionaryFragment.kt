package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.requests.TranslateTextRequest
import com.fishrungames.hallyu.models.responses.TranslateTextResponse
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.retrofit.HallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import kotlinx.android.synthetic.main.fragment_dictionary.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DictionaryFragment : BaseFragment() {

    private var hallyuApi: HallyuApi? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hallyuApi = RetrofitController.getHallyuApiApi()

        translateButton.setOnClickListener { translateText() }
        clearButton.setOnClickListener { clearText() }

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_main)
    }

    private fun translateText() {
        getActivityInstance()?.hideInputMethod()
        val translateTextRequest = TranslateTextRequest()
        translateTextRequest.RequestWordTranslation = textInKoreanEditText.text.toString()
        getActivityInstance()?.showProgressBar()
        hallyuApi!!.translateKoreanText(translateTextRequest).enqueue(translateTextCallback)
    }

    private fun clearText() {
        textInKoreanEditText.setText("")
        translatedEditText.text = ""
    }

    private val translateTextCallback = object : Callback<TranslateTextResponse> {
        override fun onResponse(call: Call<TranslateTextResponse>?, response: Response<TranslateTextResponse>?) {
            if (activity == null) {
                retainInstance
            }
            getActivityInstance()?.hideProgressBar()
            if (response?.isSuccessful!!) {
                if (response.body()?.haveTranslatedText()!!) {
                    translatedEditText.text = response.body()?.translatedText?.Verbose!!
                }
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<TranslateTextResponse>?, t: Throwable?) {
            if (activity == null) {
                retainInstance
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

}