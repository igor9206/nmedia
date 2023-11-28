package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.activity.PostFragment.Companion.number
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dialog.AppDialog
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)


        val authViewModel by viewModels<AuthViewModel>()

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun like(post: Post) {
                if (authViewModel.authenticated) {
                    viewModel.likeById(post.id, post.likedByMe)
                } else {
                    AppDialog.dialogAuthorization(requireView())
                }
            }

            override fun remove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun edit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
                    Bundle().also { it.text = post.content }
                )
            }

            override fun share(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
                viewModel.share(post.id)
            }

            override fun openCardPost(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().also { it.number = post.id })
            }

            override fun openMedia(post: String) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_mediaFragment,
                    Bundle().also { it.text = post }
                )
            }

        })

        binding.recyclerList.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.load() }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            val newPost = state.posts.size > adapter.currentList.size && adapter.itemCount > 0
            adapter.submitList(state.posts) {
                if (newPost) {
                    binding.recyclerList.smoothScrollToPosition(0)
                }
            }

            binding.empty.isVisible = state.empty
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            binding.recentPosts.isVisible = it > 0
        }


        binding.recentPosts.setOnClickListener {
            binding.recyclerList.smoothScrollToPosition(0)
//            viewModel.load()
            viewModel.loadFromLocalDB()
            it.isVisible = false
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.load()
            binding.swipeRefresh.isRefreshing = false
            binding.recentPosts.isVisible = false
        }

        binding.retryButton.setOnClickListener {
            viewModel.load()
        }

        binding.fab.setOnClickListener {
            if (authViewModel.authenticated) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else {
                AppDialog.dialogAuthorization(requireView())
            }
        }

        return binding.root
    }
}