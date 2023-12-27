package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.ItemLoadingBinding

class PostLoadingStateAdapter(
    private val retryListener: () -> Unit
) : LoadStateAdapter<PostLoadingViewHolder>() {
    override fun onBindViewHolder(holder: PostLoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PostLoadingViewHolder {
        return PostLoadingViewHolder(
            ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            retryListener
        )
    }
}

class PostLoadingViewHolder(
    private val binding: ItemLoadingBinding,
    private val retryListener: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(loadState: LoadState) {
        binding.apply {
            progress.isVisible = loadState is LoadState.Loading
            retryButton.isVisible = loadState is LoadState.Error
            retryButton.setOnClickListener {
                retryListener
            }
        }

    }
}