package com.fishrungames.hallyu.ui.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.constants.FileConstants
import com.fishrungames.hallyu.models.ComicsEpisode
import com.fishrungames.hallyu.models.EpisodePicture
import com.fishrungames.hallyu.ui.MainActivity
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.FileUtil
import com.fishrungames.hallyu.utils.NetworkUtil
import kotlinx.android.synthetic.main.item_comics_episode.view.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.graphics.Bitmap
import android.util.Log
import com.fishrungames.hallyu.models.ComicsImage
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import com.nostra13.universalimageloader.core.ImageLoader


class ComicsEpisodeAdapter(private val episodes : List<ComicsEpisode>, val context: Context, private val comicsId: String, private val clickListener: ClickListener) : RecyclerView.Adapter<ComicsEpisodeAdapter.ViewHolder>() {

    private val cacheEpisodes: MutableList<ComicsEpisode> = mutableListOf()
    private val STATE_NOT_LOADED = 0
    private val STATE_LOADING = 1
    private val STATE_LOADED = 2

    init {
        getEpisodesListFromFile()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_comics_episode, parent, false))
    }

    companion object {
        var mClickListener: ClickListener? = null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode : ComicsEpisode = episodes[position]
        mClickListener = clickListener

        holder.tvTitle.text = episode.title.toString()

        holder.contentLayout.setOnClickListener {
            if (mClickListener != null)
                mClickListener?.onClick(position, getEpisodeState(episode))
        }

        setEpisodeState(getEpisodeState(episode), holder.ivLoadEpisode, holder.pbLoadingEpisode)

        loadMissingImages(episode, holder.ivLoadEpisode, holder.pbLoadingEpisode)

        if (getEpisodeState(episode) == STATE_NOT_LOADED) {
            holder.ivLoadEpisode.setOnClickListener {
                if (NetworkUtil.isNetworkAvailable(context)) {
                    saveEpisodeImagesOnStorage(episode, getImagesToLoad(episode), holder.ivLoadEpisode, holder.pbLoadingEpisode)
                } else {
                    DialogUtil.showAlertDialog(context, context.getString(R.string.error_message_networkError))
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    interface ClickListener {
        fun onClick(position: Int, episodeState: Int)
    }

    private fun saveEpisodeImagesOnStorage(episode: ComicsEpisode, imagesList: List<ComicsImage>, loadEpisodeImage: ImageView, loadingEpisodeBar: ProgressBar) {
        setEpisodeState(STATE_LOADING, loadEpisodeImage, loadingEpisodeBar)
        val loadedImages: MutableList<ComicsImage> = mutableListOf()
        for (image in imagesList) {
            ImageLoader.getInstance().loadImage(image.imageUrl, object : SimpleImageLoadingListener() {
                override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                    loadedImages.add(image)
                    Log.d("UIL_LOG", "Loaded image: ${image.name}")
                    if (FileUtil.saveBitmapOnStorage(context, image.name!!, loadedImage!!)) {
                        Log.d("UIL_LOG", "Loaded images: ${loadedImages.size}/${imagesList.size}")
                        if (loadedImages.size == imagesList.size) {
                            updateEpisodeInCache(episode)
                            setEpisodeState(STATE_LOADED, loadEpisodeImage, loadingEpisodeBar)
                        }
                    }
                }
            })
        }
    }

    private fun loadMissingImages(episode: ComicsEpisode, loadEpisodeImage: ImageView, loadingEpisodeBar: ProgressBar) {
        if (getEpisodeState(episode) == STATE_LOADING) {
            saveEpisodeImagesOnStorage(episode, getImagesToLoad(episode), loadEpisodeImage, loadingEpisodeBar)
        }
    }

    private fun insertEpisodeToCache(episode: ComicsEpisode) {
        cacheEpisodes.add(episode)
        val gson = Gson()
        val listType = object : TypeToken<List<ComicsEpisode>>() {}.type
        val json = gson.toJson(cacheEpisodes, listType)
        FileUtil.writeToFile(json, FileConstants.getComicsEpisodesFilename(comicsId), context)
    }

    private fun updateEpisodeInCache(episode: ComicsEpisode) {
        cacheEpisodes[getEpisodeIndexByList(episode, cacheEpisodes)] = episode
        val gson = Gson()
        val listType = object : TypeToken<List<ComicsEpisode>>() {}.type
        val json = gson.toJson(cacheEpisodes, listType)
        FileUtil.writeToFile(json, FileConstants.getComicsEpisodesFilename(comicsId), context)
    }

    private fun getEpisodesListFromFile() {
        val filename = FileConstants.getComicsEpisodesFilename(comicsId)
        val fileData = FileUtil.readFromFile(filename, context)
        if (fileData.isEmpty()) {
            return
        }
        val listType = object : TypeToken<List<ComicsEpisode>>() {}.type
        cacheEpisodes.addAll(Gson().fromJson(fileData, listType))
    }

    private fun getEpisodeIndexByList(episode: ComicsEpisode, episodes: List<ComicsEpisode>): Int {
        for (episodeOfList in episodes) {
            if (episode.id!! == episodeOfList.id) {
                return episodes.indexOf(episodeOfList)
            }
        }
        return -1
    }

    private fun getEpisodePictures(episode: ComicsEpisode): List<EpisodePicture> {
        val episodeImages: MutableList<EpisodePicture> = mutableListOf()
        for (image in episode.images!!) {
            episodeImages.add(image)
        }
        return episodeImages
    }

    private fun getEpisodeImages(episode: ComicsEpisode): List<String> {
        val episodeImages: MutableList<String> = mutableListOf()
        for (image in episode.images!!) {
            episodeImages.add(image.originalImage?.name!!)
            episodeImages.add(image.originalImage?.name!!)
        }
        return episodeImages
    }

    private fun getLoadedImagesCount(episode: ComicsEpisode): Int {
        var loadedTextures = 0
        val episodeIndexFromCache = getEpisodeIndexByList(episode, cacheEpisodes)
        if (episodeIndexFromCache < 0) { // new episode
            insertEpisodeToCache(episode)
            return 0
        }
        val cacheEpisode = cacheEpisodes[episodeIndexFromCache]
        val cacheEpisodePictures = getEpisodePictures(cacheEpisode)
        val episodePictures = getEpisodePictures(episode)
        val fileList = FileUtil.getFilesList(context)
        for (episodePicture in episodePictures) {
            for (cacheEpisodePicture in cacheEpisodePictures) {
                if (fileList.contains(episodePicture.originalImage?.name) && episodePicture.originalImage?.imageHash.equals(cacheEpisodePicture.originalImage?.imageHash)) {
                    loadedTextures++
                }
                if (fileList.contains(episodePicture.translatedImage?.name) && episodePicture.translatedImage?.imageHash.equals(cacheEpisodePicture.translatedImage?.imageHash)) {
                    loadedTextures++
                }
            }
        }
        return loadedTextures
    }

    private fun getImagesToLoad(episode: ComicsEpisode): List<ComicsImage> {
        val imagesToLoad: MutableList<ComicsImage> = mutableListOf()
        val filesList = FileUtil.getFilesList(context)

        val episodeIndexFromCache = getEpisodeIndexByList(episode, cacheEpisodes)
        val cacheEpisode = cacheEpisodes[episodeIndexFromCache]

        val cacheEpisodePictures = getEpisodePictures(cacheEpisode)
        val episodePictures = getEpisodePictures(episode)

        for (episodePicture in episodePictures) {
            if (!filesList.contains(episodePicture.originalImage?.name)) {
                imagesToLoad.add(episodePicture.originalImage!!)
            }
            if (!filesList.contains(episodePicture.translatedImage?.name)) {
                imagesToLoad.add(episodePicture.translatedImage!!)
                continue
            }
            for (cacheEpisodePicture in cacheEpisodePictures) {
                if (episodePicture.originalImage?.name.equals(cacheEpisodePicture.originalImage?.name) && !episodePicture.originalImage?.imageHash.equals(cacheEpisodePicture.originalImage?.imageHash)) {
                    imagesToLoad.add(episodePicture.originalImage!!)
                }
                if (episodePicture.translatedImage?.name.equals(cacheEpisodePicture.translatedImage?.name) && !episodePicture.translatedImage?.imageHash.equals(cacheEpisodePicture.translatedImage?.imageHash)) {
                    imagesToLoad.add(episodePicture.translatedImage!!)
                }
            }

        }
        return imagesToLoad
    }

    private fun getEpisodeState(episode: ComicsEpisode): Int {
        val loadedTextures = getLoadedImagesCount(episode)
        return if (loadedTextures == 0) {
            STATE_NOT_LOADED
        } else if (loadedTextures > 0 && loadedTextures < getEpisodeImages(episode).size) {
            STATE_LOADING
        } else {
            STATE_LOADED
        }
    }

    private fun setEpisodeState(state: Int, loadEpisodeImage: ImageView, loadingEpisodeBar: ProgressBar) {
        (context as MainActivity)
        when (state) {
            STATE_NOT_LOADED -> {
                context.runOnUiThread { loadEpisodeImage.visibility = View.VISIBLE }
                context.runOnUiThread { loadingEpisodeBar.visibility = View.INVISIBLE }
                loadEpisodeImage.setImageResource(R.mipmap.ic_download_grey600_24dp)
            }
            STATE_LOADING -> {
                context.runOnUiThread { loadEpisodeImage.visibility = View.INVISIBLE }
                context.runOnUiThread { loadingEpisodeBar.visibility = View.VISIBLE }
            }
            STATE_LOADED -> {
                context.runOnUiThread { loadEpisodeImage.visibility = View.VISIBLE }
                context.runOnUiThread { loadingEpisodeBar.visibility = View.INVISIBLE }
                loadEpisodeImage.setImageResource(R.mipmap.ic_chevron_right_grey600_24dp)
            }
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val contentLayout: ConstraintLayout = view.comicsEpisodeContentLayout
        val tvTitle: TextView = view.titleTextView
        val ivLoadEpisode: ImageView = view.loadEpisodeImageView
        val pbLoadingEpisode: ProgressBar = view.loadingEpisodeProgressBar
    }

}