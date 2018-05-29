package com.fishrungames.hallyu.ui.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.Post
import com.fishrungames.hallyu.ui.MainActivity
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.item_post.view.*

class PostAdapter(private val posts : List<Post>, val context: Context, private val clickListener: ClickListener) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_post, parent, false))
    }

    companion object {
        var mClickListener: ClickListener? = null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post : Post = posts[position]
        mClickListener = clickListener

        holder.tvPostDate.text = post.date.toString()
        holder.tvPostTitle.text = post.header.toString()
        holder.tvPostContent.text = post.text.toString()
        holder.tvPostCommentsCount.text = post.commentsCount.toString()

        if (post.images?.size!! > 0) {
            (context as MainActivity).runOnUiThread { holder.imagesLayout.visibility = View.VISIBLE }
            ImageLoader.getInstance().displayImage(post.images!![0].imageUrl, holder.ivPost)

        } else {
            (context as MainActivity).runOnUiThread { holder.imagesLayout.visibility = View.GONE }
        }

        holder.contentLayout.setOnClickListener {
            if (mClickListener != null)
                mClickListener?.onClick(position)
        }

    }

    override fun getItemCount(): Int {
        return posts.size
    }

    interface ClickListener {
        fun onClick(position: Int)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val contentLayout: ConstraintLayout = view.postContentLayout
        val tvPostDate: TextView = view.postDateTextView
        val tvPostTitle: TextView = view.postTitleTextView
        val tvPostCommentsCount: TextView = view.postCommentsCountTextView
        val imagesLayout: LinearLayout = view.postImagesLayout
        val ivPost: ImageView = view.postImageView
        val tvPostContent: TextView = view.postContentTextView

    }

}