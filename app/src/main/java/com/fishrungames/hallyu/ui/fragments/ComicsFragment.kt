package com.fishrungames.hallyu.ui.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.constants.FileConstants
import com.fishrungames.hallyu.models.Comics
import com.fishrungames.hallyu.models.ComicsImage
import com.fishrungames.hallyu.models.responses.ComicsResponse
import com.fishrungames.hallyu.ui.adapters.ComicsAdapter
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.FileUtil
import com.fishrungames.hallyu.utils.NetworkUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import kotlinx.android.synthetic.main.fragment_comics.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComicsFragment : BaseFragment() {

    private var newHallyuApi: NewHallyuApi? = null
    private var comicsAdapter: ComicsAdapter? = null
    private var comics: MutableList<Comics> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newHallyuApi = RetrofitController.getNewHallyuApi()

        val layoutManager = GridLayoutManager(context!!, 2)
        comicsAdapter = ComicsAdapter(comics, context!!, object : ComicsAdapter.ClickListener {
            override fun onClick(position: Int) {
                val comicsItem: Comics = comics[position]
                getActivityInstance()?.openComicsEpisodesFragment(comicsItem.id.toString())
            }
        })
        comicsRecyclerView.layoutManager = layoutManager
        comicsRecyclerView.adapter = comicsAdapter
        comicsRecyclerView.setHasFixedSize(true)

        if (NetworkUtil.isNetworkAvailable(context!!)) {
            getActivityInstance()?.showProgressBar()
            newHallyuApi!!.getComics().enqueue(getComicsCallback)
        } else {
            getComicsListFromFile()
        }

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_comics)
    }

    private fun getComicsListFromFile() {
        val filename = FileConstants.FILE_COMICS_DATA
        val fileData = FileUtil.readFromFile(filename, context!!)
        if (fileData.isEmpty()) {
            return
        }
        val listType = object : TypeToken<List<Comics>>() {}.type
        comics.addAll(Gson().fromJson(fileData, listType))
        comicsAdapter?.notifyDataSetChanged()
    }

    private fun writeComicsListToFile(comicsList: List<Comics>) {
        val filename = FileConstants.FILE_COMICS_DATA
        val gson = Gson()
        val listType = object : TypeToken<List<Comics>>(){}.type
        val json = gson.toJson(comicsList, listType)
        FileUtil.writeToFile(json, filename, context!!)
    }

    private fun saveComicsImages(comicsList: List<Comics>) {
        val images = getComicsImages(comicsList)
        for (image in images) {
            ImageLoader.getInstance().loadImage(image.imageUrl, object : SimpleImageLoadingListener() {
                override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                    if (getView() == null) {
                        return
                    }
                    FileUtil.saveBitmapOnStorage(context!!, image.name!!, loadedImage!!)
                }
            })
        }
    }

    private fun getComicsImages(comicsList: List<Comics>): List<ComicsImage> {
        val comicsImages: MutableList<ComicsImage> = mutableListOf()
        for (comics in comicsList) {
            comicsImages.add(comics.originalImage!!)
            comicsImages.add(comics.translatedImage!!)
        }
        return comicsImages
    }

    private val getComicsCallback = object : Callback<ComicsResponse> {
        override fun onResponse(call: Call<ComicsResponse>?, response: Response<ComicsResponse>?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            val comicsResponse = response?.body()
            if (comicsResponse?.haveMessage()!!) {
                DialogUtil.showAlertDialog(context!!, comicsResponse.message!!)
                return
            }
            if (response.isSuccessful && !comicsResponse.comics!!.isEmpty()) {
                writeComicsListToFile(comicsResponse.comics!!)
                saveComicsImages(comicsResponse.comics!!)
                comics.addAll(comicsResponse.comics!!)
                comicsAdapter?.notifyDataSetChanged()
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<ComicsResponse>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

}