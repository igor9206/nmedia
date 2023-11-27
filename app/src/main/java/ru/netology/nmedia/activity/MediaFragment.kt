package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.EditPostFragment.Companion.text
import ru.netology.nmedia.adapter.URL
import ru.netology.nmedia.databinding.FragmentMediaBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.extension.load
import ru.netology.nmedia.util.AndroidUtils


class MediaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMediaBinding.inflate(inflater, container, false)
        val gson = Gson()

        val media = arguments?.text
        if (media != null) {
            val post = gson.fromJson(media, Post::class.java)
            binding.media.load("$URL/media/${post.attachment?.url}")
            binding.likes.text = post.likes.toString()
        }



        return binding.root
    }

}