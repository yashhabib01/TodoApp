package com.example.mvvmtodo.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mvvmtodo.R
import com.example.mvvmtodo.data.User
import com.example.mvvmtodo.databinding.FragmentRegisterBinding
import com.example.mvvmtodo.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val viewModel: RegisterViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRegisterBinding.bind(view)



        binding.apply {
            btnRegister.setOnClickListener {
                var name = name.text.toString()
                var password = password.text.toString()
                viewModel.registerUser(name = name, password = password)
            }


            if ((activity as MainActivity).supportActionBar != null) {
                val actionsBar = (activity as MainActivity).supportActionBar
                actionsBar!!.setDisplayHomeAsUpEnabled(false)
                actionsBar!!.setHomeButtonEnabled(false)

            }

            binding.tvSingIn.setOnClickListener {
                viewModel.navigateToLoginScree()
            }
            activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        activity?.finish()
                    }

                })
        }




        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.registerTaskEvent.collect { event ->
                when (event) {
                    is RegisterViewModel.RegisterEvent.InputEmptyError -> {
                        Toast.makeText(requireContext(), "Input Invalid", Toast.LENGTH_LONG).show()

                    }
                    is RegisterViewModel.RegisterEvent.SingUpErrorEvent -> {
                        Toast.makeText(requireContext(), "User already exits", Toast.LENGTH_LONG)
                            .show()

                    }
                    is RegisterViewModel.RegisterEvent.RegisterSuccessfully -> {
                        val action =
                            RegisterFragmentDirections.actionRegisterFragmentToTaskFragment(event.username)
                        findNavController().navigate(action)
                    }
                    is RegisterViewModel.RegisterEvent.NavigateToLogin -> {
                        val action =
                            RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }


}