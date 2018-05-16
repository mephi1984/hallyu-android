package com.fishrungames.hallyu.ui

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.ui.fragments.DictionaryFragment
import com.fishrungames.hallyu.ui.fragments.LessonsFragment
import com.fishrungames.hallyu.ui.fragments.PostsFragment
import com.fishrungames.hallyu.ui.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val FRAGMENT_POSTS = "fragmentPosts"
    private val FRAGMENT_DICTIONARY = "fragmentDictionary"
    private val FRAGMENT_LESSONS = "fragmentLessons"
    private val FRAGMENT_PROFILE = "fragmentProfile"

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
                replaceToLessonsFragment()
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
        supportActionBar?.show()
        navigation.menu.findItem(R.id.navigation_posts).isChecked = true
        val postsFragment = PostsFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, postsFragment, FRAGMENT_POSTS)
        ft.commit()
    }

    private fun replaceToDictionaryFragment() {
        supportActionBar?.show()
        navigation.menu.findItem(R.id.navigation_dictionary).isChecked = true
        val dictionaryFragment = DictionaryFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, dictionaryFragment, FRAGMENT_DICTIONARY)
        ft.commit()
    }

    private fun replaceToLessonsFragment() {
        supportActionBar?.show()
        navigation.menu.findItem(R.id.navigation_lessons).isChecked = true
        val lessonsFragment = LessonsFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, lessonsFragment, FRAGMENT_LESSONS)
        ft.commit()
    }

    private fun replaceToProfileFragment() {
        supportActionBar?.show()
        navigation.menu.findItem(R.id.navigation_profile).isChecked = true
        val profileFragment = ProfileFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, profileFragment, FRAGMENT_PROFILE)
        ft.commit()
    }

    fun showProgressBar() {
        this.runOnUiThread { progressBar.visibility = View.VISIBLE }
    }

    fun hideProgressBar() {
        this.runOnUiThread { progressBar.visibility = View.GONE }
    }

}
