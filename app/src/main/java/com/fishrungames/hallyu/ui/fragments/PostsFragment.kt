package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.Post
import com.fishrungames.hallyu.models.responses.PostsResponse
import com.fishrungames.hallyu.ui.adapters.PostAdapter
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import kotlinx.android.synthetic.main.fragment_posts.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostsFragment : BaseFragment() {

    private var posts: MutableList<Post> = mutableListOf()
    private var newHallyuApi: NewHallyuApi? = null
    private var postAdapter: PostAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context!!)
        postAdapter = PostAdapter(posts, context!!, object : PostAdapter.ClickListener {
            override fun onClick(position: Int) {
                val post: Post = posts[position]
                getActivityInstance()?.openPostDetailsFragment(post)
            }
        })
        postsRecyclerView.layoutManager = layoutManager
        postsRecyclerView.adapter = postAdapter
        postsRecyclerView.setHasFixedSize(true)

//        searchPostsEditText.setOnEditorActionListener({ _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                searchPosts()
//                return@setOnEditorActionListener true
//            }
//            false
//        })
//
//        searchPostsImageView.setOnClickListener { searchPosts() }

        newHallyuApi = RetrofitController.getNewHallyuApi()
        getActivityInstance()?.showProgressBar()
        newHallyuApi!!.getPosts("1").enqueue(getPostsCallback)

    }


    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_main)
    }

//    private fun searchPosts() {
//        getActivityInstance()?.hideInputMethod()
//        val searchPostsViewLayoutParams = searchPostsCardView.layoutParams as ViewGroup.MarginLayoutParams
//        val objectAnimator = ObjectAnimator.ofFloat(
//                searchPostsCardView,
//                "y",
//                searchPostsCardView.y,
//                0f + searchPostsViewLayoutParams.leftMargin)
//        objectAnimator.duration = 200
//        objectAnimator.addListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(animation: Animator) { }
//            override fun onAnimationCancel(animation: Animator) { }
//            override fun onAnimationRepeat(animation: Animator) { }
//            override fun onAnimationEnd(animation: Animator) {
//                getActivityInstance()?.showProgressBar()
//            }
//        })
//        objectAnimator.start()
//    }

    private val getPostsCallback = object : Callback<PostsResponse> {
        override fun onResponse(call: Call<PostsResponse>?, response: Response<PostsResponse>?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            val postsResponse = response?.body()
            if (postsResponse?.haveMessage()!!) {
                DialogUtil.showAlertDialog(context!!, postsResponse.message!!)
            }
            if (response.isSuccessful) {
                posts.addAll(postsResponse.posts!!)
                postAdapter?.notifyDataSetChanged()
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<PostsResponse>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

}