package com.fishrungames.hallyu.ui.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.ComicsEpisode
import kotlinx.android.synthetic.main.item_comics_episode.view.*

class ComiscEpisodeAdapter(private val episodes : List<ComicsEpisode>, val context: Context, private val clickListener: ClickListener) : RecyclerView.Adapter<ComiscEpisodeAdapter.ViewHolder>() {

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
                mClickListener?.onClick(position)
        }

    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    interface ClickListener {
        fun onClick(position: Int)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val contentLayout: ConstraintLayout = view.comicsEpisodeContentLayout
        val tvTitle: TextView = view.titleTextView

    }

}