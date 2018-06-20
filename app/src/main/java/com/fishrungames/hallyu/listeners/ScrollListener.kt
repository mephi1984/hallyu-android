package com.fishrungames.hallyu.listeners

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView


class ScrollListener : RecyclerView.OnScrollListener {

    private var buttons: MutableList<FloatingActionButton>? = null

    constructor(buttons: MutableList<FloatingActionButton>) {
        this.buttons = buttons
    }

    constructor(button: FloatingActionButton) {
        buttons = ArrayList()
        buttons!!.add(button)
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        if (dy > 0) {
            hideButtons()
        } else if (dy < 0)
            showButtons()
    }

    private fun hideButtons() {
        for (button in buttons!!) {
            button.hide()
        }
    }

    private fun showButtons() {
        for (button in buttons!!) {
            button.show()
        }
    }
}