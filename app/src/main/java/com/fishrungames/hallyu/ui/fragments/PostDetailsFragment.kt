package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.Post
import com.fishrungames.hallyu.models.PostComment
import com.fishrungames.hallyu.models.responses.PostCommentsResponse
import com.fishrungames.hallyu.ui.adapters.PostCommentAdapter
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.PrefUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import kotlinx.android.synthetic.main.fragment_post_details.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostDetailsFragment : BaseFragment() {

    private var recyclerViewState: Parcelable? = null
    private var comments: MutableList<PostComment> = mutableListOf()
    private var newHallyuApi: NewHallyuApi? = null
    private var postCommentAdapter: PostCommentAdapter? = null
    private var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromArguments()

        val layoutManager = LinearLayoutManager(context!!)
        postCommentAdapter = PostCommentAdapter(comments, context!!, object : PostCommentAdapter.ClickListener {
            override fun onClick(position: Int) {
                val comment: PostComment = comments[position]

            }
        })
        commentsRecyclerView.layoutManager = layoutManager
        commentsRecyclerView.adapter = postCommentAdapter
        commentsRecyclerView.setHasFixedSize(true)

        newHallyuApi = RetrofitController.getNewHallyuApi()

        if (post?.commentsCount!! > 0) {
            getActivityInstance()?.runOnUiThread { noCommentsTextView.visibility = View.GONE }
            getActivityInstance()?.showProgressBar()
            newHallyuApi!!.getComments(post?.id.toString()).enqueue(getCommentsCallback)
        } else {
            getActivityInstance()?.runOnUiThread { noCommentsTextView.visibility = View.VISIBLE }
        }

        if (PrefUtil.getUserToken(context!!) == "") {
            getActivityInstance()?.runOnUiThread { sendCommentLayout.visibility = View.GONE }
        } else {
            getActivityInstance()?.runOnUiThread { sendCommentLayout.visibility = View.VISIBLE }
        }

        sendCommentImageView.setOnClickListener { sendComment() }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    override fun onResume() {
        super.onResume()
        setupActionBar()

        if (getActivityInstance()?.newPostComment!!) {
            getActivityInstance()?.newPostComment = false
            getActivityInstance()?.updatePosts = true
            getActivityInstance()?.runOnUiThread { noCommentsTextView.visibility = View.INVISIBLE }
            comments.clear()
            postCommentAdapter?.notifyDataSetChanged()
            getActivityInstance()?.showProgressBar()
            newHallyuApi!!.getComments(post?.id.toString()).enqueue(getCommentsCallback)
        }
    }

    private fun getDataFromArguments() {
        if (post == null && arguments != null) {
            post = arguments!!.getSerializable("post") as Post
        }
    }

    private fun setupActionBar() {
        if (post != null) {
            getActivityInstance()?.supportActionBar?.title = post!!.header
        }
    }

    private fun sendComment() {
        if (sendCommentEditText.text.isEmpty()) {
            return
        }
        recyclerViewState = commentsRecyclerView.layoutManager.onSaveInstanceState()
        getActivityInstance()?.hideInputMethod()
        getActivityInstance()?.showProgressBar()
        newHallyuApi!!.sendComment(PrefUtil.getUserToken(context!!), post?.id.toString(), sendCommentEditText.text.toString()).enqueue(sendCommentCallback)
        sendCommentEditText.text.clear()
    }

    private val getCommentsCallback = object : Callback<PostCommentsResponse> {
        override fun onResponse(call: Call<PostCommentsResponse>?, response: Response<PostCommentsResponse>?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            val postCommentsResponse = response?.body()
            if (postCommentsResponse?.haveMessage()!!) {
                DialogUtil.showAlertDialog(context!!, postCommentsResponse.message!!)
                return
            }
            if (response.isSuccessful) {
                comments.addAll(postCommentsResponse.comments!!)
                postCommentAdapter?.notifyDataSetChanged()
                commentsRecyclerView.layoutManager.onRestoreInstanceState(recyclerViewState)
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<PostCommentsResponse>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

    private val sendCommentCallback = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.newPostComment = true
            getActivityInstance()?.hideProgressBar()
            if (response?.code() == 400) {
                PrefUtil.setUserToken(context!!, "")
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_needAuthorization))
                return
            }
            if (response!!.isSuccessful) {
                comments.clear()
                postCommentAdapter?.notifyDataSetChanged()
                getActivityInstance()?.showProgressBar()
                newHallyuApi!!.getComments(post?.id.toString()).enqueue(getCommentsCallback)
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))

        }

    }

}