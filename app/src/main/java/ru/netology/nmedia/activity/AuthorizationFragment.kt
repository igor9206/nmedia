package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthorizationBinding
import ru.netology.nmedia.viewmodel.AuthorizationViewModel


class AuthorizationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        val authorizationViewModel: AuthorizationViewModel by viewModels()


        lifecycleScope.launch {
            authorizationViewModel.data.collect { state ->
                val token = state.token.toString()
                if (state.id != 0L && token.isNotEmpty()) {
                    findNavController().navigateUp()
                }
            }
        }

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonEnter.setOnClickListener {
            val login = binding.login.text.toString().trim()
            val pass = binding.password.text.toString().trim()
            if (login.isNotEmpty() && pass.isNotEmpty()) {
                authorizationViewModel.login(login, pass, requireContext())
            } else {
                Toast.makeText(requireContext(), getText(R.string.all_fields_must_be_filled_in), Toast.LENGTH_LONG).show()
            }
        }


        return binding.root
    }

}