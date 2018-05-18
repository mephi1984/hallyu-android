package com.fishrungames.hallyu.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fishrungames.hallyu.R
import kotlinx.android.synthetic.main.fragment_other.*

class OtherFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_other, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numberToTextTestButton.setOnClickListener { getActivityInstance()?.openNumeralTestFragment(0) }
        textToNumberTestButton.setOnClickListener { getActivityInstance()?.openNumeralTestFragment(1) }
        cardTestButton.setOnClickListener { getActivityInstance()?.openCardTestFragment() }

    }
}