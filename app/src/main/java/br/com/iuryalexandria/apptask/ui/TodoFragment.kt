package br.com.iuryalexandria.apptask.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.iuryalexandria.apptask.R
import br.com.iuryalexandria.apptask.databinding.FragmentTodoBinding
import br.com.iuryalexandria.apptask.helper.BaseFragment
import br.com.iuryalexandria.apptask.helper.FirebaseHelper
import br.com.iuryalexandria.apptask.model.Task
import br.com.iuryalexandria.apptask.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class TodoFragment : BaseFragment() {

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private val taskList = mutableListOf<Task>()

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //eventos de cliques
        initClicked()
        //recuperando as tarefas do firebase
        getTasks()
    }

    //eventos de cliques
    private fun initClicked() {
        //botão de adicionar tarefa
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_formTaskFragment)
        }
    }

    //recuperando as tarefas do firebase
    private fun getTasks() {
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        //  listTask.clear()
                        taskList.clear()
                        for (snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task

                            if (task.status == 0) taskList.add(task)
                        }
                        taskList.reverse()
                        //limpando o texto
                        binding.textInfo.text = ""
                        //iniciando o Adapter
                        initAdapter()

                    }

                    tasksEmpty()

                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro.", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun tasksEmpty(){
        binding.textInfo.text = if (taskList.isEmpty()){
             "Nenhuma tarefa cadastrada."
        }else{
             ""
        }
    }

    //iniciando o adapter
    private fun initAdapter() {
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        taskAdapter = TaskAdapter(requireContext(), taskList) { task, selected ->
            optionSelected(task, selected)
        }
        binding.rvTask.adapter = taskAdapter

    }

    //logica para os botoes da tela
    private fun optionSelected(task: Task, selected: Int) {
        when (selected) {
            TaskAdapter.SELECT_REMOVE -> {
                deletTask(task)
            }
            TaskAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_NEXT ->{
                task.status = 1

                updateTask(task)
            }
            TaskAdapter.SELECT_FINALIZE ->{
                task.status = 2

                updateTask(task)
            }
        }
    }

    //salva as mudanças
    private fun updateTask(task: Task) {
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Tarefa atualizada com sucesso.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar a tarefa.", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), "Erro ao salvar a tarefa.", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    //deleta as tarefas do firebase
    private fun deletTask(task: Task) {
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .removeValue()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}