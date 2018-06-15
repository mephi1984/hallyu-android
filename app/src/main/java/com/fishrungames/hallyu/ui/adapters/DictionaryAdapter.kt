package com.fishrungames.hallyu.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.dictionary.Word
import com.fishrungames.hallyu.ui.MainActivity
import kotlinx.android.synthetic.main.item_dictionary.view.*
import kotlinx.android.synthetic.main.item_dictionary_word.view.*
import kotlinx.android.synthetic.main.item_dictionary_word_lesson.view.*
import kotlinx.android.synthetic.main.item_dictionary_word_meaning.view.*
import kotlinx.android.synthetic.main.item_dictionary_word_verbose.view.*

class DictionaryAdapter(private val words : List<Word>, val context: Context, private val clickListener: ClickListener) : RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dictionary, parent, false))
    }

    companion object {
        var mClickListener: ClickListener? = null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val word = words[position]

        holder.wordsLayout.removeAllViews()
        addWordLayout(word, holder.wordsLayout)

    }

    override fun getItemCount(): Int {
        return words.size
    }

    interface ClickListener {
        fun onClick(position: Int)
    }

    @SuppressLint("InflateParams")
    private fun addWordLayout(word: Word, wordsLayout: LinearLayout) {

        var firstWord = true

        for (translatedWord in word.resultRecord!!) {

            (context as MainActivity)

            val wordItem = LayoutInflater.from(context).inflate(R.layout.item_dictionary_word, null)

            if (firstWord) {
                context.runOnUiThread { wordItem.wordTextView.visibility = View.VISIBLE }
                wordItem.wordTextView.text = translatedWord.originalWord.toString()
                firstWord = false
            } else {
                context.runOnUiThread { wordItem.wordTextView.visibility = View.GONE }
            }


            wordItem.firstWordTextView.text = translatedWord.originalWord.toString()
            wordItem.secondWordTextView.text = translatedWord.dictStruct?.base.toString()

            if (translatedWord.dictStruct?.words != null) {
                context.runOnUiThread { wordItem.wordMeaningTextView.visibility = View.VISIBLE }
                context.runOnUiThread { wordItem.wordMeaningsLayout.visibility = View.VISIBLE }
                for (wordMeaning in translatedWord.dictStruct?.words!!) {
                    val wordMeaningItem = LayoutInflater.from(context).inflate(R.layout.item_dictionary_word_meaning, null)
                    wordMeaningItem.meaningTextView.text = wordMeaning
                    wordItem.wordMeaningsLayout.addView(wordMeaningItem)
                }
            } else {
                context.runOnUiThread { wordItem.wordMeaningTextView.visibility = View.GONE }
                context.runOnUiThread { wordItem.wordMeaningsLayout.visibility = View.GONE }
            }

            val verboseList = translatedWord.verbose.toString().split("\n")
            if (!verboseList.isEmpty()) {
                context.runOnUiThread { wordItem.verboseLayout.visibility = View.VISIBLE }
                for (verbose in verboseList) {
                    if (!verbose.isEmpty()) {
                        val wordVerboseItem = LayoutInflater.from(context).inflate(R.layout.item_dictionary_word_verbose, null)
                        wordVerboseItem.wordVerboseTextView.text = verbose
                        wordItem.verboseLayout.addView(wordVerboseItem)
                    }
                }
            } else {
                context.runOnUiThread { wordItem.verboseLayout.visibility = View.GONE }
            }


            if (translatedWord.lessons != null) {
                context.runOnUiThread { wordItem.wordLessonsTextView.visibility = View.VISIBLE }
                context.runOnUiThread { wordItem.lessonsLayout.visibility = View.VISIBLE }
                for (lesson in translatedWord.lessons!!) {
                    val wordLessonItem = LayoutInflater.from(context).inflate(R.layout.item_dictionary_word_lesson, null)
                    wordLessonItem.wordLessonTextView.text = lesson.title.toString()
                    wordItem.lessonsLayout.addView(wordLessonItem)
                }
            } else {
                context.runOnUiThread { wordItem.wordLessonsTextView.visibility = View.GONE }
                context.runOnUiThread { wordItem.lessonsLayout.visibility = View.GONE }
            }

            wordsLayout.wordsLayout.addView(wordItem)

        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val wordsLayout: LinearLayout= view.wordsLayout

    }

}