package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.view.*
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.utils.PrefUtil
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUserData()

    }

    override fun onResume() {
        super.onResume()
        getActivityInstance()?.supportActionBar?.title = getString(R.string.barTitle_profile)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.menu_profile_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_profile_logout -> getActivityInstance()?.logout()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showUserData() {
        ImageLoader.getInstance().displayImage(PrefUtil.getUserPhotoUrl(context!!), profileImageView)
        firstNameTextView.text = PrefUtil.getUserFirstName(context!!)
        lastNameTextView.text = PrefUtil.getUserLastName(context!!)
    }

}