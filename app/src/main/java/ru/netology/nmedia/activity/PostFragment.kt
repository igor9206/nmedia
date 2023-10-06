package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

//        viewModel.data.observe(viewLifecycleOwner) { posts ->
//            val post = posts.firstOrNull { it.id == postId } ?: return@observe
//            val viewHolder = PostViewHolder(binding.singlePost, object : OnInteractionListener {
//                override fun like(post: Post) {
//                    viewModel.likeById(post.id)
//                }
//
//                override fun remove(post: Post) {
//                    viewModel.removeById(post.id)
//                    findNavController().navigateUp()
//                }
//
//                override fun edit(post: Post) {
//                    viewModel.edit(post)
//                    findNavController().navigate(
//                        R.id.action_postFragment_to_editPostFragment,
//                        Bundle().also { it.text = post.content }
//                    )
//                }
//
//                override fun share(post: Post) {
//                    val intent = Intent().apply {
//                        action = Intent.ACTION_SEND
//                        putExtra(Intent.EXTRA_TEXT, post.content)
//                        type = "text/plain"
//                    }
//                    val shareIntent =
//                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
//                    startActivity(shareIntent)
//                    viewModel.share(post.id)
//                }
//
//                override fun openVideo(post: Post) {
//                    val webpage: Uri = Uri.parse(post.video)
//                    val intent = Intent(Intent.ACTION_VIEW, webpage)
//                    if (context?.let { intent.resolveActivity(it.packageManager) } != null) {
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(
//                            this@PostFragment.context,
//                            R.string.not_supported_in_any_application,
//                            Toast.LENGTH_LONG
//                        ).show()
//                        return
//                    }
//                }
//
//                override fun openCardPost(post: Post) {
//                    return
//                }
//            })
//
//            viewHolder.bind(post)
//
//        }

        return binding.root
    }
}
