package com.fishrungames.hallyu.ui

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.ui.fragments.*
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.util.Log
import com.fishrungames.hallyu.models.ComicsEpisode
import com.fishrungames.hallyu.models.Post
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.vk.sdk.api.VKError
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import android.content.Intent
import com.fishrungames.hallyu.models.User
import com.fishrungames.hallyu.models.dictionary.Lesson
import com.fishrungames.hallyu.models.responses.VKAuthorizationResponse
import com.fishrungames.hallyu.utils.DialogUtil
import com.fishrungames.hallyu.utils.PrefUtil
import com.fishrungames.hallyu.utils.retrofit.HallyuApi
import com.fishrungames.hallyu.utils.retrofit.NewHallyuApi
import com.fishrungames.hallyu.utils.retrofit.RetrofitController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val FRAGMENT_POSTS = "fragmentPosts"
    private val FRAGMENT_DICTIONARY = "fragmentDictionary"
    private val FRAGMENT_OTHER = "fragmentOther"
    private val FRAGMENT_PROFILE = "fragmentProfile"
    private val FRAGMENT_NUMERAL_TEST = "fragmentNumeralTest"
    private val FRAGMENT_CARD_TEST = "fragmentCardTest"
    private val FRAGMENT_COMICS = "fragmentComics"
    private val FRAGMENT_COMICS_EPISODES = "fragmentComicsEpisodes"
    private val FRAGMENT_EPISODE_PICTURES = "fragmentEpisodePictures"
    private val FRAGMENT_POST_DETAILS = "fragmentPostDetails"
    private val FRAGMENT_LESSON = "fragmentLesson"
    private val FRAGMENT_LOGIN = "fragmentLogin"

    private var bottomNavigationFragments: MutableList<String> = mutableListOf()
    private var hallyuApi: HallyuApi? = null
    private var newHallyuApi: NewHallyuApi? = null

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
                if (PrefUtil.getUserToken(this) != "") {
                    replaceToProfileFragment()
                    return@OnNavigationItemSelectedListener true
                } else {
                    openLoginFragment()
                }
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hallyuApi = RetrofitController.getHallyuApi()
        newHallyuApi = RetrofitController.getNewHallyuApi()

        initImageLoader()

        navigation.disableShiftMode()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        replaceToPostsFragment()

        bottomNavigationFragments.add(FRAGMENT_POSTS)
        bottomNavigationFragments.add(FRAGMENT_DICTIONARY)
        bottomNavigationFragments.add(FRAGMENT_OTHER)
        bottomNavigationFragments.add(FRAGMENT_PROFILE)

    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
                    override fun onResult(res: VKAccessToken) {
                        vkAuthorization(res)
                        Log.d("VKLog", VKAccessToken.currentToken().userId)
                        Log.d("VKLog", "TOKEN: " + VKAccessToken.currentToken().accessToken)
                    }
                    override fun onError(error: VKError) {}
                })) {
        }
    }

    private fun vkAuthorization(vkAccessToken: VKAccessToken) {
        showProgressBar()
        newHallyuApi!!.vkLogin(vkAccessToken.accessToken).enqueue(vkLoginCallback)
    }

    private fun saveUserData (user: User) {
        PrefUtil.setUserFirstName(this, user.firstName!!)
        PrefUtil.setUserLastName(this, user.lastName!!)
        PrefUtil.setUserToken(this, user.token!!)
    }

    @SuppressLint("RestrictedApi")
    private fun BottomNavigationView.disableShiftMode() {
        val menuView = getChildAt(0) as BottomNavigationMenuView
        try {
            val shiftingMode = menuView::class.java.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(menuView, false)
            shiftingMode.isAccessible = false
            for (i in 0 until menuView.childCount) {
                val item = menuView.getChildAt(i) as BottomNavigationItemView
                item.setShiftingMode(false)
                item.setChecked(item.itemData.isChecked)
            }
        } catch (e: NoSuchFieldException) {
            Log.e("BottomNavigationView", "Unable to get shift mode field", e)
        } catch (e: IllegalStateException) {
            Log.e("BottomNavigationView", "Unable to change value of shift mode", e)
        }
    }

    private fun initImageLoader() {
        val options = DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.placeholder_photo)
                .cacheOnDisk(true)
                .build()

        val config = ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .build()

        ImageLoader.getInstance().init(config)
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
        ft.commitAllowingStateLoss()
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

    fun openCardTestFragment() {
        val cardTestFragment = CardTestFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.translate_rigth_in, 0, 0, R.anim.tratslate_left_out)
        ft.add(R.id.container, cardTestFragment, FRAGMENT_CARD_TEST).addToBackStack(FRAGMENT_CARD_TEST)
        ft.commit()
    }

    fun openComicsFragment() {
        val comicsFragment = ComicsFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.translate_rigth_in, 0, 0, R.anim.tratslate_left_out)
        ft.add(R.id.container, comicsFragment, FRAGMENT_COMICS).addToBackStack(FRAGMENT_COMICS)
        ft.commit()
    }

    fun openComicsEpisodesFragment(comicsId: String) {
        val comicsEpisodesFragment = ComicsEpisodesFragment()
        val bundle = Bundle()
        bundle.putString("comicsId", comicsId)
        comicsEpisodesFragment.arguments = bundle
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.translate_rigth_in, 0, 0, R.anim.tratslate_left_out)
        ft.add(R.id.container, comicsEpisodesFragment, FRAGMENT_COMICS_EPISODES).addToBackStack(FRAGMENT_COMICS_EPISODES)
        ft.commit()
    }

    fun openEpisodePicturesFragment(episode: ComicsEpisode) {
        val episodePicturesFragment = EpisodePicturesFragment()
        val bundle = Bundle()
        bundle.putParcelable("episode", episode)
        episodePicturesFragment.arguments = bundle
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.translate_rigth_in, 0, 0, R.anim.tratslate_left_out)
        ft.add(R.id.container, episodePicturesFragment, FRAGMENT_EPISODE_PICTURES).addToBackStack(FRAGMENT_EPISODE_PICTURES)
        ft.commit()
    }

    fun openPostDetailsFragment(post: Post) {
        val postDetailsFragment = PostDetailsFragment()
        val bundle = Bundle()
        bundle.putSerializable("post", post)
        postDetailsFragment.arguments = bundle
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.translate_rigth_in, 0, 0, R.anim.tratslate_left_out)
        ft.add(R.id.container, postDetailsFragment, FRAGMENT_POST_DETAILS).addToBackStack(FRAGMENT_POST_DETAILS)
        ft.commit()
    }

    fun openLessonFragment(lesson: Lesson) {
        val lessonFragment = LessonFragment()
        val bundle = Bundle()
        bundle.putSerializable("lesson", lesson)
        lessonFragment.arguments = bundle
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.translate_rigth_in, 0, 0, R.anim.tratslate_left_out)
        ft.add(R.id.container, lessonFragment, FRAGMENT_LESSON).addToBackStack(FRAGMENT_LESSON)
        ft.commit()
    }

    fun openLoginFragment() {
        val loginFragment = LoginFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.translate_rigth_in, 0, 0, R.anim.tratslate_left_out)
        ft.add(R.id.container, loginFragment, FRAGMENT_LOGIN).addToBackStack(FRAGMENT_LOGIN)
        ft.commit()
    }

    fun setBottomNavigationViewState(currentFragmentTag: String?) {
        if (currentFragmentTag != null && bottomNavigationFragments.contains(currentFragmentTag)) {
            updateBottomNavigationViewState(true)
        } else {
            updateBottomNavigationViewState(false)
        }
    }

    fun setBackButtonViewState(currentFragmentTag: String?) {
        if (currentFragmentTag != null && bottomNavigationFragments.contains(currentFragmentTag)) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
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

    private val vkLoginCallback = object : Callback<VKAuthorizationResponse> {
        override fun onResponse(call: Call<VKAuthorizationResponse>?, response: Response<VKAuthorizationResponse>?) {
            hideProgressBar()
            val vkAuthorizationResponse = response?.body()
            if (vkAuthorizationResponse?.haveMessage()!!) {
                DialogUtil.showAlertDialog(this@MainActivity, vkAuthorizationResponse.message!!)
                return
            } else {
                if (vkAuthorizationResponse.user != null) {
                    saveUserData(vkAuthorizationResponse.user!!)
                    navigation.selectedItemId = R.id.navigation_profile
                    replaceToProfileFragment()
                } else {
                    DialogUtil.showAlertDialog(this@MainActivity, getString(R.string.error_message_serverError))
                }
            }
        }

        override fun onFailure(call: Call<VKAuthorizationResponse>?, t: Throwable?) {
            hideProgressBar()
            DialogUtil.showAlertDialog(this@MainActivity, getString(R.string.error_message_networkError))
        }

    }

}
