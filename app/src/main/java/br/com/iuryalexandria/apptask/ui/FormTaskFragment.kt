package br.com.iuryalexandria.apptask.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.iuryalexandria.apptask.R
import br.com.iuryalexandria.apptask.databinding.FragmentFormTaskBinding
import br.com.iuryalexandria.apptask.helper.BaseFragment
import br.com.iuryalexandria.apptask.helper.FirebaseHelper
import br.com.iuryalexandria.apptask.helper.initToolbar
import br.com.iuryalexandria.apptask.helper.showBottonSheet
import br.com.iuryalexandria.apptask.model.Task


class FormTaskFragment : BaseFragment() {

    private val args: FormTaskFragmentArgs by navArgs()

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task
    private var newTask: Boolean = true
    private var statusTask: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        initListener()

        getArgs()
    }

    private fun initListener() {
        //botão salvar
        binding.btnSave.setOnClickListener { validateData() }

        //radio groups
        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            statusTask = when (id) {
                R.id.rbTodo -> 0
                R.id.rbDoing -> 1
                else -> 2
            }
        }
    }

    private fun getArgs() {
        args.taskArgs.let {
            if (it != null) {
                task = it

                configTask()
            }
        }
    }

    private fun configTask() {
        newTask = false
        statusTask = task.status
        binding.textToolbar.text = getText(R.string.edit_task)

        binding.edtDescription.setText(task.description)
        setStatus()
    }

    private fun setStatus() {
        binding.radioGroup.check(
            when (task.status) {
                0 -> {
                    R.id.rbTodo
                }
                1 -> {
                    R.id.rbDoing
                }
                else -> {
                    R.id.rbDone
                }
            }
        )
    }

    private fun validateData() {
        val descritiption = binding.edtDescription.text.toString().trim()

        if (descritiption.isNotEmpty()) {
            //ocultar o teclado
            hideKeyboard()
            binding.progressBar.isVisible = true

            if (newTask) task = Task()

            task.description = descritiption
            task.status = statusTask

            saveTask()
        } else {
            showBottonSheet(message = R.string.insert_description)
        }
    }

    private fun saveTask() {
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (newTask) {// nova tarefa
                        findNavController().popBackStack()

                        showBottonSheet(
                            message = R.string.task_saved_successfully
                        )
                    } else {// editando tarefa
                        binding.progressBar.isVisible = false

                        showBottonSheet(
                            message = R.string.task_successfully_update
                        )
                    }

                } else {
                    showBottonSheet(
                        message = R.string.erro_save_task
                    )
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                showBottonSheet(
                    message = R.string.erro_save_task
                )
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}