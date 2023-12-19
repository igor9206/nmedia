package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dialog.AppDialog
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class PostFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    private val gson = Gson()

    companion object {
        var Bundle.text: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(inflater, container, false)

        val authViewModel: AuthViewModel by activityViewModels()

        val arg = arguments?.text
        val post = gson.fromJson(arg, Post::class.java)

        val viewHolder = PostViewHolder(binding.singlePost, object : OnInteractionListener {
            override fun like(post: Post) {
                if (authViewModel.authenticated) {
                    viewModel.likeById(post)
                } else {
                    AppDialog.dialogAuthorization(requireView())
                }
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

        return binding.root
    }
}
