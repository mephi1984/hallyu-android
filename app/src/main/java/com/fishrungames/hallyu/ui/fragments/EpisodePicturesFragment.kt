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
import com.fishrungames.hallyu.models.ComicsEpisode
import com.fishrungames.hallyu.models.ComicsImage
import com.fishrungames.hallyu.utils.FileUtil
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import kotlinx.android.synthetic.main.fragment_episode_pictures.*

class EpisodePicturesFragment : BaseFragment() {

    private val LANGUAGE_RUSSIAN = 0
    private val LANGUAGE_KOREAN = 1

    private var episode: ComicsEpisode? = null
    private var originalImages: MutableList<ComicsImage> = mutableListOf()
    private var translatedImages: MutableList<ComicsImage> = mutableListOf()
    private var currentPicture: Int = 1
    private var currentLanguage = 0
    private var picturesCount: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_episode_pictures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromArguments()

        nextTextView.setOnClickListener { nextBtnClicked() }
        backTextView.setOnClickListener { backBtnClicked() }

        pictureImageView.setOnClickListener {
            currentLanguage = if (currentLanguage == LANGUAGE_RUSSIAN) { LANGUAGE_KOREAN } else { LANGUAGE_RUSSIAN }
            showPicture()
        }

        initData()

    }

    override fun onResume() {
        super.onResume()
        setupActionBar()
    }

    private fun getDataFromArguments() {
        if (episode == null && arguments != null) {
            episode = arguments!!.getParcelable("episode") as ComicsEpisode
        }
    }

    private fun setupActionBar() {
        if (episode != null) {
            getActivityInstance()?.supportActionBar?.title = episode!!.title
        }
    }

    private fun initData() {
        for (episodePicture in episode?.images!!) {
            originalImages.add(episodePicture.originalImage!!)
            translatedImages.add(episodePicture.translatedImage!!)
        }
        picturesCount = originalImages.size
        setupNavigationLayout()
        showPicture()
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
        currentLanguage = LANGUAGE_RUSSIAN
        setupNavigationLayout()
        showPicture()
        pictureScrollView.fullScroll(ScrollView.FOCUS_UP)
    }

    private fun backBtnClicked() {
        currentPicture--
        currentLanguage = LANGUAGE_RUSSIAN
        setupNavigationLayout()
        showPicture()
        pictureScrollView.fullScroll(ScrollView.FOCUS_UP)
    }

    private fun showPicture() {
        getActivityInstance()?.runOnUiThread { pictureImageView.visibility = View.INVISIBLE }

        val showPictureAnimation = AlphaAnimation(0.0f, 1.0f)
        showPictureAnimation.duration = 500
        showPictureAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }
            override fun onAnimationEnd(animation: Animation) {
                if (view == null) {
                    return
                }
                getActivityInstance()?.runOnUiThread { pictureImageView.visibility = View.VISIBLE } }
            override fun onAnimationRepeat(animation: Animation) { }
        })

        val bitmap: Bitmap?
        val image: ComicsImage?

        if (currentLanguage == LANGUAGE_RUSSIAN) {
            bitmap = FileUtil.getBitmapFromStorage(context!!, originalImages[currentPicture - 1].name!!)
            image = originalImages[currentPicture - 1]
        } else {
            bitmap = FileUtil.getBitmapFromStorage(context!!, translatedImages[currentPicture - 1].name!!)
            image = translatedImages[currentPicture - 1]
        }

        if (bitmap == null) {
            getActivityInstance()?.showProgressBar()
            ImageLoader.getInstance().loadImage(image.imageUrl, object : SimpleImageLoadingListener() {
                override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                    if (getView() == null) {
                        return
                    }
                    getActivityInstance()?.hideProgressBar()
                    pictureImageView.setImageBitmap(loadedImage)
                    pictureImageView.startAnimation(showPictureAnimation)
                    FileUtil.saveBitmapOnStorage(context!!, image.name!!, loadedImage!!)
                }
            })
        } else {
            pictureImageView.setImageBitmap(bitmap)
            pictureImageView.startAnimation(showPictureAnimation)
        }

    }

}