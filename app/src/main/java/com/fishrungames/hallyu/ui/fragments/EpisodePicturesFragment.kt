package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.responses.EpisodePicturesResponse
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.fragment_episode_pictures.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EpisodePicturesFragment : BaseFragment() {

    private var episodeId: String? = null
    private var newHallyuApi: NewHallyuApi? = null
    private var originalImages: MutableList<String> = mutableListOf()
    private var translatedImages: MutableList<String> = mutableListOf()
    private var currentPicture: Int = 1
    private var picturesCount: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_episode_pictures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromArguments()

        newHallyuApi = RetrofitController.getNewHallyuApi()

        getActivityInstance()?.showProgressBar()
        newHallyuApi!!.getPictures(episodeId!!).enqueue(getPicturesCallback)

        nextTextView.setOnClickListener { nextPicture() }

    }

    private fun getDataFromArguments() {
        if (arguments != null) {
            episodeId = arguments!!.getString("episodeId")
        }
    }

    private fun updateCountLabel() {
        val countLabel = currentPicture.toString() + "/" + picturesCount.toString()
        countTextView.text = countLabel
    }

    private fun setupNavigationLayout() {
        updateCountLabel()
        if (currentPicture == 1) {
            getActivityInstance()?.runOnUiThread { backTextView.visibility = View.INVISIBLE }
        } else {
            getActivityInstance()?.runOnUiThread { backTextView.visibility = View.VISIBLE }
        }
        if (currentPicture == picturesCount) {
            getActivityInstance()?.runOnUiThread { nextTextView.visibility = View.INVISIBLE }
        } else {
            getActivityInstance()?.runOnUiThread { nextTextView.visibility = View.VISIBLE }
        }
    }

    private fun nextPicture() {
        if (currentPicture < picturesCount) {
            currentPicture++
        }
        setupNavigationLayout()
        showPicture()
    }

    private fun showPicture() {
        ImageLoader.getInstance().displayImage(originalImages[currentPicture], pictureImageView)
    }

    private val getPicturesCallback = object : Callback<EpisodePicturesResponse> {
        override fun onResponse(call: Call<EpisodePicturesResponse>?, response: Response<EpisodePicturesResponse>?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            val episodePicturesResponse = response?.body()
            if (episodePicturesResponse?.haveMessage()!!) {
                DialogUtil.showAlertDialog(context!!, episodePicturesResponse.message!!)
                return
            }
            if (response.isSuccessful) {
                for (episodePicture in episodePicturesResponse.pictures!!) {
                    originalImages.add(episodePicture.originalImage?.imageUrl!!)
                    translatedImages.add(episodePicture.translatedImage?.imageUrl!!)
                }
                picturesCount = originalImages.size
                setupNavigationLayout()
                showPicture()
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<EpisodePicturesResponse>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

}