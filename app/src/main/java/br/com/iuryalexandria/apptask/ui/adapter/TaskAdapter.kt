package br.com.iuryalexandria.apptask.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.com.iuryalexandria.apptask.R
import br.com.iuryalexandria.apptask.databinding.ItemAdapterBinding
import br.com.iuryalexandria.apptask.model.Task


class TaskAdapter(
    private val context: Context,
    private val taskList: List<Task>,
    val taskSelected: (Task, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {

    companion object {
        val SELECT_BACK: Int = 1
        val SELECT_REMOVE: Int = 2
        val SELECT_EDIT: Int = 3
        val SELECT_FINALIZE: Int = 4
        val SELECT_NEXT: Int = 5
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.textDescription.text = task.description

        //status dos botões
        holder.binding.btnRemove.setOnClickListener { taskSelected(task, SELECT_REMOVE) }
        holder.binding.btnEdit.setOnClickListener { taskSelected(task, SELECT_EDIT) }
        holder.binding.btnFinalize.setOnClickListener { taskSelected(task, SELECT_FINALIZE) }

        //status das setas
        when (task.status) {
            0 -> {
                //deixa a seta esquerda invisivel
                holder.binding.btnBack.isVisible = false

                //altera a cor da seta
                holder.binding.btnNext.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_doing)
                )
                //passa o evento de clique btnNext
                holder.binding.btnNext.setOnClickListener { taskSelected(task, SELECT_NEXT) }
            }
            1 -> {
                //altera a cor da seta
                holder.binding.btnBack.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_todo)
                )//altera a cor da seta
                holder.binding.btnNext.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_done)
                )

                //passa o evento de clique btnBack
                holder.binding.btnBack.setOnClickListener { taskSelected(task, SELECT_BACK) }
                //passa o evento de clique btnNext
                holder.binding.btnNext.setOnClickListener { taskSelected(task, SELECT_NEXT) }
            }
            else -> {
                //deixa a seta direita invisivel
                holder.binding.btnNext.isVisible = false
                //altera a cor da seta
                holder.binding.btnBack.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_todo)
                )

                //passa o evento de clique btnBack
                holder.binding.btnBack.setOnClickListener { taskSelected(task, SELECT_BACK) }
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    // viewHolder
    inner class MyViewHolder(val binding: ItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

}