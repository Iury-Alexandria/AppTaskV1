package br.com.iuryalexandria.apptask.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.iuryalexandria.apptask.R
import br.com.iuryalexandria.apptask.databinding.FragmentDoingBinding
import br.com.iuryalexandria.apptask.helper.FirebaseHelper
import br.com.iuryalexandria.apptask.helper.showBottonSheet
import br.com.iuryalexandria.apptask.model.Task
import br.com.iuryalexandria.apptask.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class DoingFragment : Fragment() {

    private var _binding: FragmentDoingBinding? = null
    private val binding get() = _binding!!

    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //recuperando as tarefas do firebase
        getTasks()
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

                        taskList.clear()
                        for (snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task

                            if (task.status == 1) taskList.add(task)
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
                    showBottonSheet(
                        message = R.string.erro
                    )
                }

            })
    }

    private fun tasksEmpty(){
        binding.textInfo.text = if (taskList.isEmpty()){
            getText(R.string.no_task_registered)
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

    private fun optionSelected(task: Task, selected: Int) {
        when (selected) {
            TaskAdapter.SELECT_REMOVE -> {
                deleteTask(task)
            }
            TaskAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_BACK ->{
                task.status = 0

                updateTask(task)
            }
            TaskAdapter.SELECT_NEXT ->{
                task.status = 2

                updateTask(task)
            }
            TaskAdapter.SELECT_FINALIZE ->{
                task.status = 2

                updateTask(task)
            }
        }
    }

    private fun updateTask(task: Task) {
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showBottonSheet(
                        message = R.string.task_successfully_update
                    )
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

    private fun deleteTask(task: Task){
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