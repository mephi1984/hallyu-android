package com.fishrungames.hallyu.ui.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ScrollView
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.responses.EpisodePicturesResponse
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import kotlinx.android.synthetic.main.fragment_episode_pictures.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EpisodePicturesFragment : BaseFragment() {

    private val LANGUAGE_RUSSIAN = 0
    private val LANGUAGE_KOREAN = 1

    private var episodeTitle: String? = null
    private var newHallyuApi: NewHallyuApi? = null
    private var originalImages: MutableList<String> = mutableListOf()
    private var translatedImages: MutableList<String> = mutableListOf()
    private var currentPicture: Int = 1
    private var currentLanguage = 0
    private var picturesCount: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_episode_pictures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromArguments()

        newHallyuApi = RetrofitController.getNewHallyuApi()

//        getActivityInstance()?.showProgressBar()
//        newHallyuApi!!.getPictures(episodeId!!).enqueue(getPicturesCallback)

        nextTextView.setOnClickListener { nextBtnClicked() }
        backTextView.setOnClickListener { backBtnClicked() }

        pictureImageView.setOnClickListener {
            currentLanguage = if (currentLanguage == 0) { 1 } else { 0 }
            showPicture()
        }

    }

    private fun getDataFromArguments() {
        if (arguments != null) {
            episodeTitle = arguments!!.getString("episodeTitle")
            getActivityInstance()?.supportActionBar?.title = episodeTitle
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

    private fun nextBtnClicked() {
        currentPicture++
        currentLanguage = 0
        setupNavigationLayout()
        showPicture()
    }

    private fun backBtnClicked() {
        currentPicture--
        currentLanguage = 0
        setupNavigationLayout()
        showPicture()
    }

    private fun showPicture() {
        getActivityInstance()?.runOnUiThread { pictureImageView.visibility = View.INVISIBLE }
        getActivityInstance()?.showProgressBar()
        val url = if (currentLanguage == 0) {
            originalImages[currentPicture - 1]
        } else {
            translatedImages[currentPicture - 1]
        }
        ImageLoader.getInstance().loadImage(url, object : SimpleImageLoadingListener() {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {

                getActivityInstance()?.hideProgressBar()

                pictureImageView.setImageBitmap(loadedImage)
                pictureScrollView.fullScroll(ScrollView.FOCUS_UP)

                val showPictureAnimation = AlphaAnimation(0.0f, 1.0f)
                showPictureAnimation.duration = 500
                showPictureAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) { }
                    override fun onAnimationEnd(animation: Animation) { getActivityInstance()?.runOnUiThread { pictureImageView.visibility = View.VISIBLE } }
                    override fun onAnimationRepeat(animation: Animation) { }
                })

                pictureImageView.startAnimation(showPictureAnimation)
            }
        })
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