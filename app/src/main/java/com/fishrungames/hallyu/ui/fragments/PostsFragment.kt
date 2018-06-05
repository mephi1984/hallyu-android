package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.Post
import com.fishrungames.hallyu.models.PostCategory
import com.fishrungames.hallyu.models.responses.PostCategoriesResponse
import com.fishrungames.hallyu.models.responses.PostsResponse
import com.fishrungames.hallyu.ui.adapters.PostAdapter
import com.fishrungames.hallyu.ui.adapters.PostCategoryAdapter
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import kotlinx.android.synthetic.main.fragment_posts.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostsFragment : BaseFragment() {

    private var postCategoryAdapter: PostCategoryAdapter? = null
    private var postAdapter: PostAdapter? = null
    private var postCategories: MutableList<PostCategory> = mutableListOf()
    private var posts: MutableList<Post> = mutableListOf()
    private var newHallyuApi: NewHallyuApi? = null
    private var categoriesIsShowing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPostRecyclerView()
        initPostCategoryRecyclerView()

        newHallyuApi = RetrofitController.getNewHallyuApi()
        getActivityInstance()?.showProgressBar()
        newHallyuApi!!.getPostCategories().enqueue(getPostCategoriesCallback)
        newHallyuApi!!.getPosts("1").enqueue(getPostsCallback)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.menu_posts_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_posts_menu -> if (categoriesIsShowing) hidePostCategories() else showPostCategories()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun initPostRecyclerView() {
        val postLayoutManager = LinearLayoutManager(context!!)
        postAdapter = PostAdapter(posts, context!!, object : PostAdapter.ClickListener {
            override fun onClick(position: Int) {
                val post: Post = posts[position]
                getActivityInstance()?.openPostDetailsFragment(post)
            }
        })
        postsRecyclerView.layoutManager = postLayoutManager
        postsRecyclerView.adapter = postAdapter
        postsRecyclerView.setHasFixedSize(true)
    }

    private fun initPostCategoryRecyclerView() {
        val postCategoryLayoutManager = LinearLayoutManager(context!!)
        postCategoryAdapter = PostCategoryAdapter(postCategories, context!!, object : PostCategoryAdapter.ClickListener {
            override fun onClick(position: Int) {
                val category: PostCategory = postCategories[position]
                hidePostCategories()
                posts.clear()
                postAdapter?.notifyDataSetChanged()
                getActivityInstance()?.supportActionBar?.title = category.name
                getActivityInstance()?.showProgressBar()
                newHallyuApi!!.getPosts(category.id.toString()).enqueue(getPostsCallback)
            }
        })
        postCategoriesRecyclerView.layoutManager = postCategoryLayoutManager
        postCategoriesRecyclerView.adapter = postCategoryAdapter
        postCategoriesRecyclerView.setHasFixedSize(true)
    }

    private fun showPostCategories() {
        val showAnimation = AnimationUtils.loadAnimation(context!!, R.anim.translate_top_in)
        showAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }
            override fun onAnimationEnd(animation: Animation) {
                getActivityInstance()?.runOnUiThread { postCategoriesRecyclerView.visibility = View.VISIBLE }
                categoriesIsShowing = true
            }
            override fun onAnimationRepeat(animation: Animation) { }
        })
        postCategoriesRecyclerView.startAnimation(showAnimation)
    }

    private fun hidePostCategories() {
        val hideAnimation = AnimationUtils.loadAnimation(context!!, R.anim.translate_top_out)
        hideAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) { }
            override fun onAnimationEnd(animation: Animation) {
                getActivityInstance()?.runOnUiThread { postCategoriesRecyclerView.visibility = View.INVISIBLE }
                categoriesIsShowing = false
            }
            override fun onAnimationRepeat(animation: Animation) { }
        })
        postCategoriesRecyclerView.startAnimation(hideAnimation)
    }

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

    private val getPostCategoriesCallback = object : Callback<PostCategoriesResponse> {
        override fun onResponse(call: Call<PostCategoriesResponse>?, response: Response<PostCategoriesResponse>?) {
            if (activity == null) {
                return
            }
            if (response?.isSuccessful!!) {
                val postCategoriesResponse = response.body()
                if (postCategoriesResponse?.result!! && postCategoriesResponse.categories?.size!! > 0) {
                    postCategories.addAll(postCategoriesResponse.categories!!)
                    getActivityInstance()?.supportActionBar?.title = postCategories[0].name
                    postCategoryAdapter?.notifyDataSetChanged()
                }
            }
        }

        override fun onFailure(call: Call<PostCategoriesResponse>?, t: Throwable?) {
            if (activity == null) {
                return
            }
        }

    }

}