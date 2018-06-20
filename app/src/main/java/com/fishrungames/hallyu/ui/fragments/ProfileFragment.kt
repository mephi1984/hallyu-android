package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.utils.PrefUtil
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUserData()

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_main)
    }

    private fun showUserData() {
        ImageLoader.getInstance().displayImage(PrefUtil.getUserPhotoUrl(context!!), profileImageView)
        firstNameTextView.text = PrefUtil.getUserFirstName(context!!)
        lastNameTextView.text = PrefUtil.getUserLastName(context!!)
    }

}