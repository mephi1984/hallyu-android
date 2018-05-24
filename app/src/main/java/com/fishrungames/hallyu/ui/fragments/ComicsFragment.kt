package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.Comics
import com.fishrungames.hallyu.models.responses.ComicsResponse
import com.fishrungames.hallyu.ui.adapters.ComicsAdapter
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import kotlinx.android.synthetic.main.fragment_comics.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComicsFragment : BaseFragment() {

    private var newHallyuApi: NewHallyuApi? = null
    private var comicsAdapter: ComicsAdapter? = null
    private var comics: MutableList<Comics> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newHallyuApi = RetrofitController.getNewHallyuApi()

        val layoutManager = GridLayoutManager(context!!, 2)
        comicsAdapter = ComicsAdapter(comics, context!!, object : ComicsAdapter.ClickListener {
            override fun onClick(position: Int) {
                val comicsItem: Comics = comics[position]

            }
        })
        comicsRecyclerView.layoutManager = layoutManager
        comicsRecyclerView.adapter = comicsAdapter
        comicsRecyclerView.setHasFixedSize(true)

        getActivityInstance()?.showProgressBar()
        newHallyuApi!!.getComics().enqueue(getComicsCallback)

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_comics)
    }

    private val getComicsCallback = object : Callback<ComicsResponse> {
        override fun onResponse(call: Call<ComicsResponse>?, response: Response<ComicsResponse>?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            if (response?.isSuccessful!!) {
                comics.addAll(response.body()?.comics!!)
                comics.addAll(response.body()?.comics!!)
                comics.addAll(response.body()?.comics!!)
                comics.addAll(response.body()?.comics!!)
                comics.addAll(response.body()?.comics!!)
                comics.addAll(response.body()?.comics!!)
                comics.addAll(response.body()?.comics!!)
                comics.addAll(response.body()?.comics!!)
                comicsAdapter?.notifyDataSetChanged()
            } else {
                DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_serverError))
            }
        }

        override fun onFailure(call: Call<ComicsResponse>?, t: Throwable?) {
            if (activity == null) {
                return
            }
            getActivityInstance()?.hideProgressBar()
            DialogUtil.showAlertDialog(context!!, getString(R.string.error_message_networkError))
        }

    }

}