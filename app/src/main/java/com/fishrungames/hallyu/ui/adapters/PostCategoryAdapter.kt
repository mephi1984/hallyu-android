package com.fishrungames.hallyu.ui.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.PostCategory
import com.fishrungames.hallyu.ui.MainActivity
import kotlinx.android.synthetic.main.item_post_category.view.*

class PostCategoryAdapter(private val categories : List<PostCategory>, val context: Context, private val clickListener: ClickListener) : RecyclerView.Adapter<PostCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_post_category, parent, false))
    }

    companion object {
        var mClickListener: ClickListener? = null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category : PostCategory= categories[position]
        mClickListener = clickListener

        holder.tvCategoryName.text = category.name.toString()

        if (position == categories.size - 1) {
            (context as MainActivity).runOnUiThread { holder.bottomView.visibility = View.GONE }
        } else {
            (context as MainActivity).runOnUiThread { holder.bottomView.visibility = View.VISIBLE }
        }

        holder.contentLayout.setOnClickListener {
            if (mClickListener != null)
                mClickListener?.onClick(position)
        }

    }

    override fun getItemCount(): Int {
        return categories.size
    }

    interface ClickListener {
        fun onClick(position: Int)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val contentLayout: ConstraintLayout = view.postCategoryContentLayout
        val tvCategoryName: TextView = view.postCategoryNameTextView
        val bottomView: View = view.postCategoryBottomView
    }

}