package com.fishrungames.hallyu.ui.fragments

import android.support.v4.app.Fragment
import com.fishrungames.hallyu.ui.MainActivity

abstract class BaseFragment : Fragment() {

    fun getActivityInstance(): MainActivity? {
        return if (activity != null) {
            (activity as MainActivity)
        } else {
            null
        }
    }

    override fun onDestroy() {
        getActivityInstance()?.hideProgressBar()
        super.onDestroy()
    }

    override fun onResume() {
        getActivityInstance()?.setBottomNavigationViewState(this.tag)
        getActivityInstance()?.setBackButtonViewState(this.tag)
        super.onResume()
    }

}