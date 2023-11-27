package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()

    companion object {
        var Bundle.number: Long? by LongArg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(inflater, container, false)

        val postId = arguments?.number

        viewModel.data.observe(viewLifecycleOwner) { state ->
            val post = state.posts.firstOrNull { it.id == postId } ?: return@observe
            val viewHolder = PostViewHolder(binding.singlePost, object : OnInteractionListener {
                override fun like(post: Post) {
                    viewModel.likeById(post.id, post.likedByMe)
                }

                override fun remove(post: Post) {
                    viewModel.removeById(post.id)
                    findNavController().navigateUp()
                }

                override fun edit(post: Post) {
                    viewModel.edit(post)
                    findNavController().navigate(
                        R.id.action_postFragment_to_editPostFragment,
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
                    return
                }

                override fun openMedia(url: String) {
                    TODO("Not yet implemented")
                }
            })

            viewHolder.bind(post)

        }
        return binding.root
    }
}
