package com.fishrungames.hallyu.ui.fragments

import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import com.fishrungames.hallyu.ui.MainActivity

abstract class BaseFragment : Fragment() {

    fun getActivityInstance(): MainActivity? {
        return if (activity != null) {
            (activity as MainActivity)
        } else {
            null
        }
    }

    fun setDefaultActionBar() {
        getActivityInstance()?.supportActionBar?.navigationMode = ActionBar.NAVIGATION_MODE_STANDARD
    }

    override fun onDestroy() {
        getActivityInstance()?.hideProgressBar()
        super.onDestroy()
    }

    override fun onResume() {
        setDefaultActionBar()
        getActivityInstance()?.setBottomNavigationViewState(this.tag)
        getActivityInstance()?.setBackButtonViewState(this.tag)
        super.onResume()
    }

}