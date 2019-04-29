package com.mycelium.wallet.activity.modern.adapter

import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.modern.adapter.holder.NewsItemAllHolder
import com.mycelium.wallet.activity.modern.adapter.holder.NewsV2BigHolder
import com.mycelium.wallet.activity.modern.adapter.holder.NewsV2Holder
import com.mycelium.wallet.activity.modern.adapter.holder.SpaceViewHolder
import com.mycelium.wallet.external.mediaflow.model.Category
import com.mycelium.wallet.external.mediaflow.model.News


class NewsAdapter(val preferences: SharedPreferences) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = mutableListOf<News>()
    private var nativeData = mutableListOf<News>()

    var dataMap = mutableMapOf<Category, MutableList<News>>()
    private var category: Category = ALL

    var shareClickListener: ((news: News) -> Unit)? = null
    var openClickListener: ((news: News) -> Unit)? = null
    var categoryClickListener: ((category: Category) -> Unit)? = null

    var searchMode = false
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    fun setData(data: List<News>) {
        dataMap.clear()
        data.forEach { news ->
            val list = dataMap.getOrElse(news.categories.values.elementAt(0)) {
                mutableListOf()
            }
            list.add(news)
            dataMap[news.getCategory()] = list
        }
        notifyDataSetChanged()
    }

    fun setCategory(category: Category) {
        this.category = category
        notifyDataSetChanged()
    }

    fun addData(data: List<News>) {
        this.data.addAll(data)
        this.nativeData.addAll(data)
        notifyDataSetChanged()
    }

    private lateinit var layoutInflater: LayoutInflater

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        layoutInflater = LayoutInflater.from(recyclerView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SPACE -> SpaceViewHolder(layoutInflater.inflate(R.layout.item_mediaflow_space, parent, false))
            TYPE_NEWS_ITEM_ALL -> NewsItemAllHolder(preferences, layoutInflater.inflate(R.layout.item_mediaflow_news_all, parent, false))
            TYPE_NEWS_V2_BIG -> NewsV2BigHolder(layoutInflater.inflate(R.layout.item_mediaflow_news_v2_big, parent, false), preferences)
            TYPE_NEWS_V2 -> NewsV2Holder(layoutInflater.inflate(R.layout.item_mediaflow_news_v2_wrap, parent, false), preferences)
            else -> SpaceViewHolder(layoutInflater.inflate(R.layout.item_mediaflow_space, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_NEWS_V2_BIG) {
            val bigHolder = holder as NewsV2BigHolder
            val news = dataMap[category]!![position]
            bigHolder.bind(news)
            bigHolder.openClickListener = {
                openClickListener?.invoke(it)
            }
        } else if (getItemViewType(position) == TYPE_NEWS_V2) {
            val news = dataMap[category]!![position]
            val newsSmallHolder = holder as NewsV2Holder
            newsSmallHolder.bind(news)
            newsSmallHolder.openClickListener = {
                openClickListener?.invoke(it)
            }
        } else if (getItemViewType(position) == TYPE_NEWS_ITEM_ALL) {
            val allHolder = holder as NewsItemAllHolder

            val category = dataMap.entries.elementAt(position).key
            val list = dataMap.entries.elementAt(position).value

            allHolder.bigHolder.bind(list[0])
            allHolder.bigHolder.openClickListener = {
                openClickListener?.invoke(it)
            }

            val listHolder = holder.listHolder
            if (list.size > 1) {
                listHolder.adapter.submitList(list.subList(1, list.size))
                listHolder.itemView.visibility = View.VISIBLE
            } else {
                listHolder.itemView.visibility = View.GONE
            }
            listHolder.clickListener = {
                openClickListener?.invoke(it)
            }

            val btnHolder = allHolder.categoryHolder
            btnHolder.text().text = btnHolder.text().resources.getString(R.string.media_flow_category_text, category.name.toUpperCase())
            btnHolder.itemView.setOnClickListener {
                categoryClickListener?.invoke(category)
            }
        }
    }


    override fun getItemCount(): Int {
        return (if (category == ALL) dataMap.size
        else dataMap[category]?.size ?: 0) + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            TYPE_SPACE
        } else if (category == ALL) {
            TYPE_NEWS_ITEM_ALL
        } else if (position == 0) {
            TYPE_NEWS_V2_BIG
        } else {
            TYPE_NEWS_V2
        }
    }

    companion object {
        const val PREF_FAVORITE = "favorite"

        const val TYPE_SPACE = 0

        const val TYPE_NEWS_V2_BIG = 1
        const val TYPE_NEWS_V2 = 2

        const val TYPE_NEWS_ITEM_ALL = 3

        val ALL = Category("All")
    }
}

fun News.getCategory(): Category = if (this.categories.values.isNotEmpty()) this.categories.values.elementAt(0) else Category("Uncategorized")


