package com.mycelium.wallet.activity.modern.adapter.holder

import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.modern.adapter.NewsAdapter
import com.mycelium.wallet.activity.news.NewsUtils
import com.mycelium.wallet.activity.news.getFitImage
import com.mycelium.wallet.external.mediaflow.model.News
import kotlinx.android.synthetic.main.item_mediaflow_news_v2.view.*


class NewsV2Holder(itemView: View, val preferences: SharedPreferences) : RecyclerView.ViewHolder(itemView) {
    var openClickListener: ((News) -> Unit)? = null

    fun bind(news: News) {
        itemView.title.text = news.title
        itemView.date.text = NewsUtils.getDateAuthorString(itemView.date.context, news)

        itemView.setOnClickListener {
            openClickListener?.invoke(news)
        }

        itemView.favoriteButton.setImageDrawable(itemView.resources.getDrawable(
                if (preferences.getBoolean(NewsAdapter.PREF_FAVORITE + news.id, false)) R.drawable.ic_favorite else R.drawable.ic_not_favorite))

        val requestOptions = RequestOptions()
                .transforms(CenterCrop(), RoundedCorners(itemView.image.resources.getDimensionPixelSize(R.dimen.media_flow_round_corner)))
        Glide.with(itemView.image)
                .load(news.getFitImage(itemView.image.resources.displayMetrics.widthPixels))
                .error(Glide.with(itemView.image).load(R.drawable.news_default_image))
                .apply(requestOptions)
                .into(itemView.image)
    }
}