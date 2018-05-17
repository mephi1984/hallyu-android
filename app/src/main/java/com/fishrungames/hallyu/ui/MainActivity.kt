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

    private var bottomNavigationFragments: MutableList<String> = mutableListOf()

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

        bottomNavigationFragments.add(FRAGMENT_POSTS)
        bottomNavigationFragments.add(FRAGMENT_DICTIONARY)
        bottomNavigationFragments.add(FRAGMENT_OTHER)
        bottomNavigationFragments.add(FRAGMENT_PROFILE)

    }

    override fun onBackPressed() {

        val backStackCount = supportFragmentManager.backStackEntryCount

        if (backStackCount <= 0) {
            moveTaskToBack(true)
            return
        }

        val fragment = supportFragmentManager.fragments[backStackCount - 1]
        fragment.onResume()

        val currentFragmentBackStack = supportFragmentManager.getBackStackEntryAt(backStackCount - 1).name

        super.onBackPressed()
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

    fun openNumeralTestFragment(testType: Int) {
        val numeralTestFragment = NumeralTestFragment()
        val bundle = Bundle()
        bundle.putInt("testType", testType)
        numeralTestFragment.arguments = bundle
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.translate_rigth_in, 0, 0, R.anim.tratslate_left_out)
        ft.add(R.id.container, numeralTestFragment, FRAGMENT_NUMERAL_TEST).addToBackStack(FRAGMENT_NUMERAL_TEST)
        ft.commit()
    }

    fun setBottomNavigationViewState(currentFragmentTag: String?) {
        if (currentFragmentTag != null && bottomNavigationFragments.contains(currentFragmentTag)) {
            updateBottomNavigationViewState(true)
        } else {
            updateBottomNavigationViewState(false)
        }
    }

    private fun updateBottomNavigationViewState(show: Boolean) {
        val visible = if (show) View.VISIBLE else View.GONE
        if (navigation.visibility != visible) {
            this.runOnUiThread { navigation.visibility = visible }
        }
    }

    fun showProgressBar() {
        this.runOnUiThread { progressBar.visibility = View.VISIBLE }
    }

    fun hideProgressBar() {
        this.runOnUiThread { progressBar.visibility = View.GONE }
    }

}