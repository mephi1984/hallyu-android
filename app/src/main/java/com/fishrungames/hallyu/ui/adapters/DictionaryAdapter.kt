package com.fishrungames.hallyu.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.fishrungames.hallyu.R
import com.fishrungames.hallyu.models.dictionary.CompoundVerb
import com.fishrungames.hallyu.models.dictionary.Lesson
import com.fishrungames.hallyu.models.dictionary.Word
import com.fishrungames.hallyu.ui.MainActivity
import com.fishrungames.hallyu.utils.AnimationUtil
import kotlinx.android.synthetic.main.item_dictionary.view.*
import kotlinx.android.synthetic.main.item_dictionary_compound_verb.view.*
import kotlinx.android.synthetic.main.item_dictionary_word.view.*
import kotlinx.android.synthetic.main.item_dictionary_word_lesson.view.*
import kotlinx.android.synthetic.main.item_dictionary_word_meaning.view.*
import kotlinx.android.synthetic.main.item_dictionary_word_verbose.view.*
import android.view.View.MeasureSpec

class DictionaryAdapter(private val compoundVerbs: List<CompoundVerb>,
                        private val words : List<Word>,
                        val context: Context,
                        private val clickListener: LessonClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val wordsHolder = WordsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dictionary, parent, false))
        val compoundVerbHolder = CompoundVerbsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dictionary_compound_verb, parent, false))
        when (viewType) {
            TYPE_COMPOUND_VERBS -> return compoundVerbHolder
            TYPE_WORDS -> return wordsHolder
        }
        return wordsHolder
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mContext: Context? = null
        var lessonClickListener: LessonClickListener? = null
        const val TYPE_COMPOUND_VERBS = 0
        const val TYPE_WORDS = 1

        const val BLOCK_NONE = 0
        const val BLOCK_MAIN = 1
        const val BLOCK_SECOND = 2

        fun setTextColorByWordType(textView: TextView, wordType: String) {
            when (wordType) {
                "NOUN"              ->  textView.setTextColor(0xffff8284.toInt())
                "VERB"              ->  textView.setTextColor(0xff008000.toInt())
                "SPECIAL"           ->  textView.setTextColor(0xff551a8b.toInt())
                "CHINESE_NUMBER"    ->  return
            }
        }

        fun getStrWordType(wordType: String): String? {
            when (wordType) {
                "NOUN"              -> return "Существительное"
                "VERB"              -> return "Глагол"
                "SPECIAL"           -> return "Специальное слово"
                "CHINESE_NUMBER"    -> return "Китайское числительное"
            }
            return null
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        mContext = context
        lessonClickListener = clickListener
        val viewType = holder.itemViewType

        (context as MainActivity)

        when (viewType) {
            TYPE_COMPOUND_VERBS -> {
                holder as CompoundVerbsViewHolder
                val verb = compoundVerbs[position]

                holder.firstVerbBlockLayout.setOnClickListener {
                    if (verb.selectedBlock == BLOCK_MAIN) {
                        verb.selectedBlock = BLOCK_NONE
                    } else {
                        verb.selectedBlock = BLOCK_MAIN
                        holder.showVerbDetails(verb)
                    }
                    updateVerbBlockBackground(verb, holder.firstVerbBlockLayout, holder.secondVerbBlockLayout, holder.verbDetailsLayout, holder.verbDetailsContentLayout)
                }

                holder.secondVerbBlockLayout.setOnClickListener {
                    if (verb.selectedBlock == BLOCK_SECOND) {
                        verb.selectedBlock = BLOCK_NONE
                    } else {
                        verb.selectedBlock = BLOCK_SECOND
                        holder.showVerbDetails(verb)
                    }
                    updateVerbBlockBackground(verb, holder.firstVerbBlockLayout, holder.secondVerbBlockLayout, holder.verbDetailsLayout, holder.verbDetailsContentLayout)
                }

                updateVerbBlockBackground(verb, holder.firstVerbBlockLayout, holder.secondVerbBlockLayout, holder.verbDetailsLayout, holder.verbDetailsContentLayout)

                holder.firstVerbBlockWordTextView.text = verb.mainWordStruct?.originalWord.toString()
                holder.secondVerbBlockWordTextView.text = verb.secondaryWordStruct?.originalWord.toString()

                val mainBlockWords = verb.mainWordStruct?.dictStruct?.words
                var mainBlockWordsStr = ""
                for (word in mainBlockWords!!) {
                    mainBlockWordsStr += "$word\n"
                    if (mainBlockWords.indexOf(word) > 3 ) {
                        return
                    }
                }

                val secondBlockWords = verb.secondaryWordStruct?.dictStruct?.words
                var secondBlockWordsStr = ""
                for (word in secondBlockWords!!) {
                    secondBlockWordsStr += "$word\n"
                    if (secondBlockWords.indexOf(word) > 3 ) {
                        return
                    }
                }

                holder.firstVerbBlockMeaningTextView.text = mainBlockWordsStr
                holder.secondVerbBlockMeaningTextView.text = secondBlockWordsStr

                holder.compoundVerbVerboseLayout.removeAllViews()
                holder.compoundVerbLessonsLayout.removeAllViews()

                val verboseList = verb.verbose.toString().split("\n")
                if (!verboseList.isEmpty()) {
                    context.runOnUiThread { holder.compoundVerbVerboseLayout.visibility = View.VISIBLE }
                    for (verbose in verboseList) {
                        if (!verbose.isEmpty()) {
                            val wordVerboseItem = LayoutInflater.from(context).inflate(R.layout.item_dictionary_word_verbose, null)
                            wordVerboseItem.wordVerboseTextView.text = verbose
                            holder.compoundVerbVerboseLayout.addView(wordVerboseItem)
                        }
                    }
                } else {
                    context.runOnUiThread { holder.compoundVerbVerboseLayout.visibility = View.GONE }
                }

                if (verb.lessons != null) {
                    context.runOnUiThread { holder.compoundVerbWordLessonsTextView.visibility = View.VISIBLE }
                    context.runOnUiThread { holder.compoundVerbLessonsLayout.visibility = View.VISIBLE }
                    for (lesson in verb.lessons!!) {
                        val wordLessonItem = LayoutInflater.from(context).inflate(R.layout.item_dictionary_word_lesson, null)
                        wordLessonItem.wordLessonTextView.text = lesson.title.toString()
                        wordLessonItem.setOnClickListener {
                            if (lessonClickListener != null)
                                lessonClickListener?.onClick(lesson)
                        }
                        holder.compoundVerbLessonsLayout.addView(wordLessonItem)
                    }
                } else {
                    context.runOnUiThread { holder.compoundVerbWordLessonsTextView.visibility = View.GONE }
                    context.runOnUiThread { holder.compoundVerbLessonsLayout.visibility = View.GONE }
                }

            }
            TYPE_WORDS -> {
                holder as WordsViewHolder
                val word = words[position - compoundVerbs.size]
                holder.wordsLayout.removeAllViews()
                addWordLayout(word, holder.wordsLayout)
            }
        }

    }

    override fun getItemCount(): Int {
        return words.size + compoundVerbs.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (!compoundVerbs.isEmpty() && position <= compoundVerbs.size - 1) {
            TYPE_COMPOUND_VERBS
        } else {
            TYPE_WORDS
        }
    }

    interface LessonClickListener {
        fun onClick(lesson: Lesson)
    }

    private fun getDrawable(id: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.resources.getDrawable(id, context.theme)
        } else {
            context.resources.getDrawable(id)
        }
    }

    private fun setLayoutDrawable(layout: ConstraintLayout, drawable: Drawable) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(drawable)
        } else {
            layout.background = drawable
        }
    }

    private fun updateVerbBlockBackground(verb: CompoundVerb, firstBlock: ConstraintLayout, secondBlock: ConstraintLayout, detailsLayout: ConstraintLayout, contentLayout: ConstraintLayout) {

        (context as MainActivity)

        when {
            verb.selectedBlock == BLOCK_MAIN -> {
                setLayoutDrawable(firstBlock, getDrawable(R.drawable.shape_dictionary_verb_block_selected))
                setLayoutDrawable(secondBlock, getDrawable(R.drawable.shape_dictionary_verb_block_default))
                context.runOnUiThread { detailsLayout.visibility = View.VISIBLE }
                context.runOnUiThread { contentLayout.visibility = View.VISIBLE }
                AnimationUtil.showView(contentLayout)
            }
            verb.selectedBlock == BLOCK_SECOND -> {
                setLayoutDrawable(firstBlock, getDrawable(R.drawable.shape_dictionary_verb_block_default))
                setLayoutDrawable(secondBlock, getDrawable(R.drawable.shape_dictionary_verb_block_selected))
                context.runOnUiThread { detailsLayout.visibility = View.VISIBLE }
                context.runOnUiThread { contentLayout.visibility = View.VISIBLE }
                AnimationUtil.showView(contentLayout)
            }
            else -> {
                setLayoutDrawable(firstBlock, getDrawable(R.drawable.shape_dictionary_verb_block_default))
                setLayoutDrawable(secondBlock, getDrawable(R.drawable.shape_dictionary_verb_block_default))
                context.runOnUiThread { detailsLayout.visibility = View.GONE }
                context.runOnUiThread { contentLayout.visibility = View.INVISIBLE }
            }
        }
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

            setTextColorByWordType(wordItem.firstWordTextView, translatedWord.wordType.toString())

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

            val wordTypeStr = getStrWordType(translatedWord.wordType!!)
            if (wordTypeStr != null) {
                val wordVerboseItem = LayoutInflater.from(mContext).inflate(R.layout.item_dictionary_word_verbose, null)
                wordVerboseItem.wordVerboseTextView.text = wordTypeStr
                wordVerboseItem.wordVerboseTextView.setTypeface(null, Typeface.BOLD)
                wordVerboseItem.wordVerboseTextView.setTextColor(0xff3d9887.toInt())
                wordItem.verboseLayout.addView(wordVerboseItem)
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
                    wordLessonItem.setOnClickListener {
                        if (lessonClickListener != null)
                            lessonClickListener?.onClick(lesson)
                    }
                    wordItem.lessonsLayout.addView(wordLessonItem)
                }
            } else {
                context.runOnUiThread { wordItem.wordLessonsTextView.visibility = View.GONE }
                context.runOnUiThread { wordItem.lessonsLayout.visibility = View.GONE }
            }

            wordsLayout.wordsLayout.addView(wordItem)

        }
    }

    class WordsViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val wordsLayout: LinearLayout = view.wordsLayout

    }

    class CompoundVerbsViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        var contentLayout: ConstraintLayout = view.contentLayout
        val firstVerbBlockLayout: ConstraintLayout = view.firstVerbBlockLayout
        val secondVerbBlockLayout: ConstraintLayout = view.secondVerbBlockLayout
        val firstVerbBlockWordTextView: TextView = view.firstVerbBlockWordTextView
        val secondVerbBlockWordTextView: TextView = view.secondVerbBlockWordTextView
        val firstVerbBlockMeaningTextView: TextView = view.firstVerbBlockMeaningTextView
        val secondVerbBlockMeaningTextView: TextView = view.secondVerbBlockMeaningTextView
        val verbDetailsLayout: ConstraintLayout = view.verbDetailsLayout
        val verbDetailsContentLayout: ConstraintLayout = view.verbDetailsContentLayout
        val verbDetailsFirstWordTextView: TextView = view.verbDetailsFirstWordTextView
        val verbDetailsSecondWordTextView: TextView = view.verbDetailsSecondWordTextView
        val verbDetailsWordMeaningTextView: TextView = view.verbDetailsWordMeaningTextView
        val verbDetailsWordMeaningsLayout: LinearLayout = view.verbDetailsWordMeaningsLayout
        val verbDetailsVerboseLayout: LinearLayout = view.verbDetailsVerboseLayout
        val compoundVerbVerboseLayout: LinearLayout = view.compoundVerbVerboseLayout
        val compoundVerbWordLessonsTextView: TextView = view.compoundVerbWordLessonsTextView
        val compoundVerbLessonsLayout: LinearLayout = view.compoundVerbLessonsLayout

        @SuppressLint("InflateParams", "ResourceAsColor")
        fun showVerbDetails(verb: CompoundVerb) {
            val translatedWord = if (verb.selectedBlock == BLOCK_MAIN) {
                verb.mainWordStruct
            } else {
                verb.secondaryWordStruct
            }

            verbDetailsFirstWordTextView.text = translatedWord?.dictStruct?.base.toString()
            verbDetailsSecondWordTextView.text = translatedWord?.originalWord.toString()
            setTextColorByWordType(verbDetailsFirstWordTextView, translatedWord?.wordType.toString())

            verbDetailsWordMeaningsLayout.removeAllViews()
            verbDetailsVerboseLayout.removeAllViews()

            if (translatedWord?.dictStruct?.words != null) {
                (mContext as MainActivity).runOnUiThread { verbDetailsWordMeaningTextView.visibility = View.VISIBLE }
                (mContext as MainActivity).runOnUiThread { verbDetailsWordMeaningsLayout.visibility = View.VISIBLE }
                for (wordMeaning in translatedWord.dictStruct?.words!!) {
                    val wordMeaningItem = LayoutInflater.from(mContext).inflate(R.layout.item_dictionary_word_meaning, null)
                    wordMeaningItem.meaningTextView.text = wordMeaning
                    verbDetailsWordMeaningsLayout.addView(wordMeaningItem)
                }
            } else {
                (mContext as MainActivity).runOnUiThread { verbDetailsWordMeaningTextView.visibility = View.GONE }
                (mContext as MainActivity).runOnUiThread { verbDetailsWordMeaningsLayout.visibility = View.GONE }
            }

            val wordTypeStr = getStrWordType(translatedWord?.wordType!!)
            if (wordTypeStr != null) {
                val wordVerboseItem = LayoutInflater.from(mContext).inflate(R.layout.item_dictionary_word_verbose, null)
                wordVerboseItem.wordVerboseTextView.text = wordTypeStr
                wordVerboseItem.wordVerboseTextView.setTypeface(null, Typeface.BOLD)
                wordVerboseItem.wordVerboseTextView.setTextColor(0xff3d9887.toInt())
                verbDetailsVerboseLayout.addView(wordVerboseItem)
            }

            val verboseList = translatedWord?.verbose.toString().split("\n")
            if (!verboseList.isEmpty()) {
                (mContext as MainActivity).runOnUiThread { verbDetailsVerboseLayout.visibility = View.VISIBLE }
                for (verbose in verboseList) {
                    if (!verbose.isEmpty()) {
                        val wordVerboseItem = LayoutInflater.from(mContext).inflate(R.layout.item_dictionary_word_verbose, null)
                        wordVerboseItem.wordVerboseTextView.text = verbose
                        verbDetailsVerboseLayout.addView(wordVerboseItem)
                    }
                }
            } else {
                (mContext as MainActivity).runOnUiThread { verbDetailsVerboseLayout.verboseLayout.visibility = View.GONE }
            }

            val widthSpec = MeasureSpec.makeMeasureSpec(contentLayout.width, MeasureSpec.EXACTLY)
            val heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            verbDetailsLayout.measure(widthSpec, heightSpec)
            val height = verbDetailsLayout.measuredHeight

            if (verb.selectedBlock != BLOCK_NONE) {
                (mContext as MainActivity).runOnUiThread { verbDetailsContentLayout.visibility = View.INVISIBLE }
            }

        }


    }

}