package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.vk.sdk.VKSdk

class ProfileFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        VKSdk.login(getActivityInstance()!!)

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_main)
    }

}