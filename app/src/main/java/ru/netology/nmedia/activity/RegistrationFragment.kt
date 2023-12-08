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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.viewmodel.RegistrationViewModel

@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        val regViewModel: RegistrationViewModel by viewModels()

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonRegistration.setOnClickListener {
            if (checkField()) {
                val name = binding.name.text.toString().trim()
                val login = binding.login.text.toString().trim()
                val pass = binding.pass.text.toString()

                regViewModel.registration(name, login, pass, requireContext())
            }
        }


        regViewModel.data.observe(viewLifecycleOwner) { state ->
            val token = state.token.toString()
            if (state.id != 0L && token.isNotEmpty()) {
                println(state.id)
                println(token)
                findNavController().navigateUp()
            }
        }


        return binding.root
    }


    private fun checkField(): Boolean {
        var count = 0
        val fields = listOf(
            binding.name,
            binding.login,
            binding.pass,
            binding.repeatPassword
        )

        for (field in fields) {
            if (field.text.isNullOrEmpty()) {
                field.error = getString(R.string.error_empty_content)
                count++
            } else {
                field.error = null
            }
        }

        return if (count == 0) {
            checkPass()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.all_fields_must_be_filled_in),
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }

    private fun checkPass(): Boolean {
        val check = binding.pass.text.toString() == binding.repeatPassword.text.toString()
        return if (check) {
            true
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.passwords_dont_match),
                Toast.LENGTH_LONG
            ).show()
            binding.pass.error = getString(R.string.check_pass)
            binding.repeatPassword.error = getString(R.string.check_pass)
            false
        }
    }

}