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
import com.fishrungames.hallyu.models.Image
import com.fishrungames.hallyu.models.PostComment
import com.fishrungames.hallyu.ui.MainActivity
import com.fishrungames.hallyu.utils.DateUtil
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.item_post_comment.view.*
import java.util.*

class PostCommentAdapter(private val comments : List<PostComment>, val context: Context, private val clickListener: ClickListener) : RecyclerView.Adapter<PostCommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_post_comment, parent, false))
    }

    companion object {
        var mClickListener: ClickListener? = null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment : PostComment= comments[position]
        mClickListener = clickListener

        holder.tvCommentUserFirstName.text = comment.firstName.toString()
        holder.tvCommentUserLastName.text = comment.lastName.toString()
        holder.tvCommentDate.text = comment.date.toString()
        holder.tvCommentText.text = comment.text.toString()

        ImageLoader.getInstance().displayImage(comment.photoUrl, holder.ivCommentUserPhoto)

        if (comment.images?.size!! > 0) {
            (context as MainActivity).runOnUiThread { holder.imagesLayout.visibility = View.VISIBLE }
            ImageLoader.getInstance().displayImage(comment.images!![0].imageUrl, holder.ivComment)
        } else {
            (context as MainActivity).runOnUiThread { holder.imagesLayout.visibility = View.GONE }
        }

        holder.contentLayout.setOnClickListener {
            if (mClickListener != null)
                mClickListener?.onClick(position)
        }

    }

    override fun getItemCount(): Int {
        return comments.size
    }

    interface ClickListener {
        fun onClick(position: Int)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val contentLayout: ConstraintLayout = view.commentContentLayout
        val tvCommentUserFirstName: TextView = view.commentUserFirstNameTextView
        val tvCommentUserLastName: TextView = view.commentUserLastNameTextView
        val ivCommentUserPhoto: ImageView = view.commentUserPhoto
        val tvCommentDate: TextView = view.commentDateTextView
        val imagesLayout: LinearLayout = view.commentImagesLayout
        val ivComment: ImageView = view.commentImageView
        val tvCommentText: TextView = view.commentTextTextView

    }

}