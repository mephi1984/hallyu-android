package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.constants.FileConstants
import com.fishrungames.hallyu.models.Post
import com.fishrungames.hallyu.models.PostCategory
import com.fishrungames.hallyu.models.responses.PostCategoriesResponse
import com.fishrungames.hallyu.models.responses.PostsResponse
import com.fishrungames.hallyu.ui.adapters.PostAdapter
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.FileUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_posts.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostsFragment : BaseFragment() {

    private var spinner: Spinner? = null
    private var postCategoriesAdapter: ArrayAdapter<String>? = null
    private var postCategoriesList: MutableList<PostCategory> = mutableListOf()
    private var postCategories = arrayListOf<String>()
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

        spinner = Spinner(context!!)

        getPostCategoriesDataFromFile()

        newHallyuApi = RetrofitController.getNewHallyuApi()
        getActivityInstance()?.showProgressBar()
        newHallyuApi!!.getPostCategories().enqueue(getPostCategoriesCallback)

    }

    override fun onResume() {
        super.onResume()
        setupActionBar()
    }

    private fun setupActionBar() {

        getActivityInstance()?.supportActionBar?.title = ""
        getActivityInstance()?.supportActionBar?.navigationMode = ActionBar.NAVIGATION_MODE_LIST

        if (spinner?.adapter == null) {
            postCategoriesAdapter = ArrayAdapter(context!!, R.layout.spinner_item, postCategories)
            postCategoriesAdapter!!.setDropDownViewResource(R.layout.spinner_drop_down_item)
            spinner?.adapter = postCategoriesAdapter

            getActivityInstance()?.supportActionBar?.setListNavigationCallbacks(postCategoriesAdapter, { itemPosition, _ ->
                posts.clear()
                postAdapter?.notifyDataSetChanged()
                getActivityInstance()?.showProgressBar()
                newHallyuApi!!.getPosts(postCategoriesList[itemPosition].id.toString()).enqueue(getPostsCallback)
                true
            })
        }
    }

    private fun getPostCategoriesDataFromFile() {
        val filename = FileConstants.FILE_POST_CATEGORIES
        val fileData = FileUtil.readFromFile(filename, context!!)
        if (fileData.isEmpty()) {
            return
        }
        val listType = object : TypeToken<List<PostCategory>>() {}.type
        postCategoriesList.addAll(Gson().fromJson(fileData, listType))
        if (postCategoriesList.size > 0) {
            for (category in postCategoriesList) {
                postCategories.add(category.name!!)
            }
            postCategoriesAdapter?.notifyDataSetChanged()
        }
    }

    private fun writePostCategoriesListToFile(postCategoriesList: List<PostCategory>) {
        val filename = FileConstants.FILE_POST_CATEGORIES
        val gson = Gson()
        val listType = object : TypeToken<List<PostCategory>>(){}.type
        val json = gson.toJson(postCategoriesList, listType)
        FileUtil.writeToFile(json, filename, context!!)
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
            if (response?.isSuccessful!!) {
                val postCategoriesResponse = response.body()
                if (postCategoriesResponse?.result!! && postCategoriesResponse.categories?.size!! > 0) {
                    writePostCategoriesListToFile(postCategoriesResponse.categories!!)
                    if (postCategoriesList.size == 0) {
                        postCategoriesList.addAll(postCategoriesResponse.categories!!)
                        for (category in postCategoriesList) {
                            postCategories.add(category.name!!)
                        }
                        postCategoriesAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        override fun onFailure(call: Call<PostCategoriesResponse>?, t: Throwable?) {

        }

    }

}