package br.com.iuryalexandria.apptask.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import br.com.iuryalexandria.apptask.R
import br.com.iuryalexandria.apptask.databinding.FragmentRegisterBinding
import br.com.iuryalexandria.apptask.helper.BaseFragment
import br.com.iuryalexandria.apptask.helper.FirebaseHelper
import br.com.iuryalexandria.apptask.helper.initToolbar
import br.com.iuryalexandria.apptask.helper.showBottonSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegisterFragment : BaseFragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        auth = Firebase.auth

        initClicks()
    }

    private fun initClicks() {
        binding.btnCreateAccount.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editSenha.text.toString().trim()

        if (email.isNotEmpty()) {
            if (password.isNotEmpty()) {
                //ocultar o teclado
                hideKeyboard()

                binding.progressBar.isVisible = true

                registerUser(email, password)

            } else {
                showBottonSheet(message = R.string.text_password_empty)
            }
        } else {
            showBottonSheet(message = R.string.text_email_empty)
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_global_homeFragment2)
                } else {
                    //caso o registro falhar
                    showBottonSheet(
                        message = FirebaseHelper.validError(task.exception?.message ?: "")
                    )
                    binding.progressBar.isVisible = false
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}