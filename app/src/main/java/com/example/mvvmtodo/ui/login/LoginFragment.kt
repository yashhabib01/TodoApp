package com.example.mvvmtodo.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mvvmtodo.R
import com.example.mvvmtodo.databinding.FragmentLoginBinding
import com.example.mvvmtodo.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginBinding.bind(view)
        binding.apply {
            tvCreateAccount.setOnClickListener {
                viewModel.navigateToRegisterScree()
            }

            btnLogin.setOnClickListener {
                val name = binding.tvName.text.toString()
                val password = binding.tvPassword.text.toString()
                viewModel.LoginUser(name, password)
            }

        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loginTaskEvent.collect { event ->
                when (event) {
                    is LoginViewModel.LoginEvent.NavigateToRegisterScreen -> {
                        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                        findNavController().navigate(action)
                    }
                    is LoginViewModel.LoginEvent.InputEmptyError -> {
                        Toast.makeText(requireContext(), "Input Invalid", Toast.LENGTH_LONG).show()
                    }
                    is LoginViewModel.LoginEvent.LoginErrorEvent -> {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_LONG).show()
                    }
                    is LoginViewModel.LoginEvent.LoginSuccessfulNavigateToTask -> {
                        val action =
                            LoginFragmentDirections.actionLoginFragmentToTaskFragment(username = event.username)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }
}