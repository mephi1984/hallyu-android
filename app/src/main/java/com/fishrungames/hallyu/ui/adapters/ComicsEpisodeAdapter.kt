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
import com.fishrungames.hallyu.ui.MainActivity
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.FileUtil
import com.fishrungames.hallyu.utils.NetworkUtil
import kotlinx.android.synthetic.main.item_comics_episode.view.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ComicsEpisodeAdapter(private val episodes : List<ComicsEpisode>, val context: Context, val comicsId: String, private val clickListener: ClickListener) : RecyclerView.Adapter<ComicsEpisodeAdapter.ViewHolder>() {

    private val cacheEpisodes: MutableList<ComicsEpisode> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        getEpisodesListFromFile()

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
                mClickListener?.onClick(position)
        }

        holder.ivLoadEpisode.setOnClickListener { loadEpisode(holder.ivLoadEpisode, holder.pbLoadingEpisode) }

    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    interface ClickListener {
        fun onClick(position: Int)
    }

    private fun loadEpisode(loadEpisodeImage: ImageView, loadingEpisodeBar: ProgressBar) {
        if (NetworkUtil.isNetworkAvailable(context)) {
            (context as MainActivity)
            context.runOnUiThread { loadEpisodeImage.visibility = View.INVISIBLE }
            context.runOnUiThread { loadingEpisodeBar.visibility = View.VISIBLE }
        } else {
            DialogUtil.showAlertDialog(context, context.getString(R.string.error_message_networkError))
        }
    }

    private fun getEpisodesListFromFile() {
        val filename = FileConstants.getComicsFilename(comicsId)
        val fileData = FileUtil.readFromFile(filename, context)
        if (fileData.isEmpty()) {
            return
        }
        val listType = object : TypeToken<List<ComicsEpisode>>() {}.type
        cacheEpisodes.addAll(Gson().fromJson(fileData, listType))
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val contentLayout: ConstraintLayout = view.comicsEpisodeContentLayout
        val tvTitle: TextView = view.titleTextView
        val ivLoadEpisode: ImageView = view.loadEpisodeImageView
        val pbLoadingEpisode: ProgressBar = view.loadingEpisodeProgressBar
    }

}