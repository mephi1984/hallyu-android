package com.fishrungames.hallyu.ui

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.ui.fragments.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val FRAGMENT_POSTS = "fragmentPosts"
    private val FRAGMENT_DICTIONARY = "fragmentDictionary"
    private val FRAGMENT_OTHER = "fragmentOther"
    private val FRAGMENT_PROFILE = "fragmentProfile"
    private val FRAGMENT_NUMERAL_TEST = "fragmentNumeralTest"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_posts -> {
                replaceToPostsFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dictionary -> {
                replaceToDictionaryFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_lessons -> {
                replaceToOtherFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                replaceToProfileFragment()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        supportActionBar?.hide()

        replaceToPostsFragment()

    }

    fun hideInputMethod() {
        if (this.currentFocus != null) {
            (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(this.currentFocus!!
                            .windowToken,0)
        }
    }

    private fun replaceToPostsFragment() {
        navigation.menu.findItem(R.id.navigation_posts).isChecked = true
        val postsFragment = PostsFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, postsFragment, FRAGMENT_POSTS)
        ft.commit()
    }

    private fun replaceToDictionaryFragment() {
        navigation.menu.findItem(R.id.navigation_dictionary).isChecked = true
        val dictionaryFragment = DictionaryFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, dictionaryFragment, FRAGMENT_DICTIONARY)
        ft.commit()
    }

    private fun replaceToOtherFragment() {
        navigation.menu.findItem(R.id.navigation_lessons).isChecked = true
        val otherFragment = OtherFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, otherFragment, FRAGMENT_OTHER)
        ft.commit()
    }

    private fun replaceToProfileFragment() {
        navigation.menu.findItem(R.id.navigation_profile).isChecked = true
        val profileFragment = ProfileFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, profileFragment, FRAGMENT_PROFILE)
        ft.commit()
    }

    fun openNumeralTestFragment() {
        val numeralTestFragment = NumeralTestFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
//        ft.setCustomAnimations(R.anim.translate_rigth_in, 0, 0, R.anim.tratslate_left_out)
        ft.add(R.id.container, numeralTestFragment, FRAGMENT_NUMERAL_TEST).addToBackStack(FRAGMENT_NUMERAL_TEST)
        ft.commit()
    }

    fun showProgressBar() {
        this.runOnUiThread { progressBar.visibility = View.VISIBLE }
    }

    fun hideProgressBar() {
        this.runOnUiThread { progressBar.visibility = View.GONE }
    }

}
