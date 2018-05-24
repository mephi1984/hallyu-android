package com.fishrungames.hallyu.ui.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.Comics
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.item_comics.view.*

class ComicsAdapter(private val comics : List<Comics>, val context: Context, private val clickListener: ClickListener) : RecyclerView.Adapter<ComicsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_comics, parent, false))
    }

    companion object {
        var mClickListener: ClickListener? = null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comicsItem : Comics = comics[position]
        mClickListener = clickListener

        holder.tvOriginalTitle.text = comicsItem.originalTitle.toString()
        holder.tvTranslatedTitle.text = comicsItem.translatedTitle.toString()

        ImageLoader.getInstance().displayImage(comicsItem.originalImage?.imageUrl, holder.comicsImageView)

        holder.contentLayout.setOnClickListener {
            if (mClickListener != null)
                mClickListener?.onClick(position)
        }

    }

    override fun getItemCount(): Int {
        return comics.size
    }

    interface ClickListener {
        fun onClick(position: Int)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val contentLayout: ConstraintLayout = view.comicsContentLayout
        val comicsImageView: ImageView = view.comicsImageView
        val tvOriginalTitle: TextView = view.originalTitleTextView
        val tvTranslatedTitle: TextView = view.translatedTitleTextView

    }

}