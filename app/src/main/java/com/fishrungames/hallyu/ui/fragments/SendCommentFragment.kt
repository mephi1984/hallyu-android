package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.Post
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.PrefUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import kotlinx.android.synthetic.main.fragment_send_comment.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendCommentFragment: BaseFragment() {

    private var post: Post? = null
    private var newHallyuApi: NewHallyuApi? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromArguments()

        newHallyuApi = RetrofitController.getNewHallyuApi()

        sendCommentButton.setOnClickListener { sendComment() }

    }

    private fun sendComment() {
        if (sendCommentTextView.text.isEmpty()) {
            return
        }
        getActivityInstance()?.hideInputMethod()
        getActivityInstance()?.showProgressBar()
        newHallyuApi!!.sendComment(PrefUtil.getUserToken(context!!), post?.id.toString(), sendCommentTextView.text.toString()).enqueue(sendCommentCallback)
    }

    private fun getDataFromArguments() {
        if (post == null && arguments != null) {
            post = arguments!!.getSerializable("post") as Post
        }
    }

    private val sendCommentCallback = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.newPostComment = true
            getActivityInstance()?.hideProgressBar()
            if (response!!.isSuccessful) {
                getActivityInstance()?.onBackPressed()
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))

        }

    }

}