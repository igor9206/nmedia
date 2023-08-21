package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.text
import ru.netology.nmedia.databinding.FragmentPostBinding
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

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.firstOrNull { it.id == postId } ?: return@observe
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                like.isChecked = post.likedByMe
                like.text = post.numericFormat(post.likes)
                share.text = post.numericFormat(post.share)
                view.text = post.numericFormat(post.view)

                like.setOnClickListener {
                    viewModel.likeById(post.id)
                }

                share.setOnClickListener {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }

                menu.setOnClickListener { menu ->
                    PopupMenu(menu.context, menu).apply {
                        inflate(R.menu.post_options)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    viewModel.removeById(post.id)
                                    findNavController().navigateUp()
                                    true
                                }

                                R.id.edit -> {
                                    viewModel.edit(post)
                                    findNavController().navigate(
                                        R.id.action_postFragment_to_editPostFragment,
                                        Bundle().also { it.text = post.content }
                                    )
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                }

            }

        }

        return binding.root
    }
}
