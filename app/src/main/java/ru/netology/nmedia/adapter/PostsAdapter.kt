package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.ItemSeparatorBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.ItemSeparator
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.extension.load
import ru.netology.nmedia.extension.loadAvatar
import java.time.format.DateTimeFormatter

const val URL = "http://192.168.1.54:9999"

interface OnInteractionListener {
    fun like(post: Post)
    fun remove(post: Post)
    fun edit(post: Post)
    fun share(post: Post)
    fun openCardPost(post: Post)
    fun openMedia(post: String)
}


class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is ItemSeparator -> R.layout.item_separator
            null -> error("unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }

            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }

            R.layout.item_separator -> {
                val binding =
                    ItemSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemSeparatorViewHolder(binding)
            }

            else -> error("unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is ItemSeparator -> (holder as? ItemSeparatorViewHolder)?.bind(item)
            null -> error("unknown view type")
        }
    }
}

class ItemSeparatorViewHolder(
    private val binding: ItemSeparatorBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ItemSeparator) {
        binding.header.text = item.text
    }

}

class AdViewHolder(
    private val binding: CardAdBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        binding.image.load("$URL/media/${ad.image}")
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    private val gson = Gson()
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published.toEpochSecond().toString()
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = post.numericFormat(post.likes)
            avatar.loadAvatar("$URL/avatars/${post.authorAvatar}")
            imageContent.let {
                if (post.attachment != null) {
                    it.visibility = View.VISIBLE
                    it.load("$URL/media/${post.attachment.url}")
                } else it.visibility = View.GONE
            }
            menu.isVisible = post.ownedByMe

            binding.imageContent.setOnClickListener {
                post.attachment ?: return@setOnClickListener
                onInteractionListener.openMedia(gson.toJson(post))
            }

            binding.cardPost.setOnClickListener {
                onInteractionListener.openCardPost(post)
            }

            like.setOnClickListener {
                onInteractionListener.like(post)
            }
            share.setOnClickListener {
                onInteractionListener.share(post)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.remove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.edit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}


class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }

}