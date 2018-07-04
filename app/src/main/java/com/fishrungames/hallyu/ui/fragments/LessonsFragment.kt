package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.dictionary.Lesson
import com.fishrungames.hallyu.models.responses.LessonResponse
import com.fishrungames.hallyu.ui.adapters.LessonAdapter
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import kotlinx.android.synthetic.main.fragment_lessons.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LessonsFragment: BaseFragment() {

    private var lessons: MutableList<Lesson> = mutableListOf()
    private var newHallyuApi: NewHallyuApi? = null
    private var lessonAdapter: LessonAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lessons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val layoutManager = LinearLayoutManager(context!!)
        lessonAdapter = LessonAdapter(lessons, context!!, object : LessonAdapter.ClickListener {
            override fun onClick(position: Int) {
                val lesson: Lesson = lessons[position]
                getActivityInstance()?.openLessonFragment(lesson)
            }
        })
        lessonRecyclerView.layoutManager = layoutManager
        lessonRecyclerView.adapter = lessonAdapter
        lessonRecyclerView.setHasFixedSize(true)

        newHallyuApi = RetrofitController.getNewHallyuApi()
        getActivityInstance()?.showProgressBar()
        newHallyuApi!!.getLessons().enqueue(getLessonsCallback)

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_lessons)
    }

    private val getLessonsCallback = object : Callback<LessonResponse> {
        override fun onResponse(call: Call<LessonResponse>?, response: Response<LessonResponse>?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            val lessonResponse = response?.body()
            if (lessonResponse?.haveMessage()!!) {
                DialogUtil.showAlertDialog(context!!, lessonResponse.message!!)
                return
            }
            if (response.isSuccessful) {
                lessons.addAll(lessonResponse.lessons!!)
                lessonAdapter?.notifyDataSetChanged()
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<LessonResponse>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

}