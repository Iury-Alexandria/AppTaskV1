package br.com.iuryalexandria.apptask.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.iuryalexandria.apptask.R
import br.com.iuryalexandria.apptask.databinding.FragmentDoneBinding
import br.com.iuryalexandria.apptask.helper.FirebaseHelper
import br.com.iuryalexandria.apptask.helper.showBottonSheet
import br.com.iuryalexandria.apptask.model.Task
import br.com.iuryalexandria.apptask.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoneFragment : Fragment() {

    private var _binding: FragmentDoneBinding? = null
    private val binding get() = _binding!!

    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoneBinding.inflate(inflater, container, false)
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
                        //  listTask.clear()
                        taskList.clear()
                        for (snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task

                            if (task.status == 2) taskList.add(task)
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
                    Toast.makeText(requireContext(), R.string.erro, Toast.LENGTH_SHORT).show()
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
    //
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
            TaskAdapter.SELECT_BACK ->{
                task.status = 1

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
                    showBottonSheet(message = R.string.task_successfully_update)
                } else {
                    showBottonSheet(message = R.string.erro_save_task)
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                showBottonSheet(message = R.string.erro_save_task)
            }
    }

    private fun deletTask(task: Task){
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