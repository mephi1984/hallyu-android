package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.constants.FileConstants
import com.fishrungames.hallyu.models.ComicsEpisode
import com.fishrungames.hallyu.models.responses.ComicsEpisodesResponse
import com.fishrungames.hallyu.ui.adapters.ComicsEpisodeAdapter
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.FileUtil
import com.fishrungames.hallyu.utils.NetworkUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_comics_episodes.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComicsEpisodesFragment : BaseFragment() {

    private var episodeAdapter: ComicsEpisodeAdapter? = null
    private var comicsId: String? = null
    private var newHallyuApi: NewHallyuApi? = null
    private var episodes: MutableList<ComicsEpisode> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comics_episodes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromArguments()

        newHallyuApi = RetrofitController.getNewHallyuApi()

        val layoutManager = LinearLayoutManager(context!!)
        episodeAdapter = ComicsEpisodeAdapter(episodes, context!!, comicsId!!, object : ComicsEpisodeAdapter.ClickListener {
            override fun onClick(position: Int, episodeState: Int) {
                val episode: ComicsEpisode = episodes[position]
                if (episodeState == 2) {
                    getActivityInstance()?.openEpisodePicturesFragment(episode.title.toString())
                }
            }
        })
        comicsEpisodesRecyclerView.layoutManager = layoutManager
        comicsEpisodesRecyclerView.adapter = episodeAdapter
        comicsEpisodesRecyclerView.setHasFixedSize(true)

        if (NetworkUtil.isNetworkAvailable(context!!)) {
            getActivityInstance()?.showProgressBar()
            newHallyuApi!!.getEpisodes(comicsId!!).enqueue(getEpisodesCallback)
        } else {
            getEpisodesListFromFile()
        }

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_comicsEpisodes)
    }

    private fun getDataFromArguments() {
        if (arguments != null) {
            comicsId = arguments!!.getString("comicsId")
        }
    }

    private fun getEpisodesListFromFile() {
        val filename = FileConstants.getComicsFilename(comicsId!!)
        val fileData = FileUtil.readFromFile(filename, context!!)
        if (fileData.isEmpty()) {
            return
        }
        val listType = object : TypeToken<List<ComicsEpisode>>() {}.type
        episodes.addAll(Gson().fromJson(fileData, listType))
        episodeAdapter?.notifyDataSetChanged()
    }

    private fun writeEpisodesListToFile(episodes: List<ComicsEpisode>) {
        val filename = FileConstants.getComicsFilename(comicsId!!)
        val fileData = FileUtil.readFromFile(filename, context!!)
        if (fileData.isEmpty()) {
            val gson = Gson()
            val listType = object : TypeToken<List<ComicsEpisode>>(){}.type
            val json = gson.toJson(episodes, listType)
            FileUtil.writeToFile(json, filename, context!!)
        }
    }

    private val getEpisodesCallback = object : Callback<ComicsEpisodesResponse> {
        override fun onResponse(call: Call<ComicsEpisodesResponse>?, response: Response<ComicsEpisodesResponse>?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            val comicsEpisodesResponse = response?.body()
            if (comicsEpisodesResponse?.haveMessage()!!) {
                DialogUtil.showAlertDialog(context!!, comicsEpisodesResponse.message!!)
                return
            }
            if (response.isSuccessful && !comicsEpisodesResponse.episodes!!.isEmpty()) {
                writeEpisodesListToFile(comicsEpisodesResponse.episodes!!)
                episodes.addAll(comicsEpisodesResponse.episodes!!)
                episodeAdapter?.notifyDataSetChanged()
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<ComicsEpisodesResponse>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

}