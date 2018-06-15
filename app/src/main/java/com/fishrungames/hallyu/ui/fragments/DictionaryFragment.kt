package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.dictionary.DictionaryResponse
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
        dictionaryAdapter = DictionaryAdapter(words, context!!, object : DictionaryAdapter.ClickListener {
            override fun onClick(position: Int) {

            }
        })
        dictionaryRecyclerView.layoutManager = layoutManager
        dictionaryRecyclerView.adapter = dictionaryAdapter
        dictionaryRecyclerView.setHasFixedSize(true)
    }

    private fun translateText() {
        if (textInKoreanEditText.text.isEmpty()) {
            return
        }
        getActivityInstance()?.hideInputMethod()
        words.clear()
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
            Log.d("myLog", "succeed")
            words.addAll(dictionaryResponse?.resultTable!!)
            dictionaryAdapter?.notifyDataSetChanged()
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