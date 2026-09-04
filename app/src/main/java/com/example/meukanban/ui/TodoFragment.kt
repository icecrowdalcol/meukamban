package com.example.meukanban.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meukanban.R
import com.example.meukanban.data.TaskRepository
import com.example.meukanban.data.model.Status
import com.example.meukanban.data.model.Task
import com.example.meukanban.databinding.FragmentTodoBinding
import com.example.meukanban.ui.adapter.TaskAdapter
import com.example.meukanban.util.showBottomSheet

class TodoFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initRecyclerViewTask()
        refreshTasks()
    }

    override fun onResume() {
        super.onResume()
        refreshTasks()
    }

    private fun initListener() {
        binding.floatingActionButton2.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_formTaskFragment)
        }
    }

    private fun initRecyclerViewTask() {
        taskAdapter = TaskAdapter(requireContext()) { task, option -> optionSelected(task, option) }

        with(binding.recyclerViewTask) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = taskAdapter
        }
    }

    private fun optionSelected(task: Task, option: Int) {
        when (option) {
            TaskAdapter.SELECT_REMOVER -> confirmRemove(task)
            TaskAdapter.SELECT_EDIT -> editTask(task)
            TaskAdapter.SELECT_DETAILS -> showDetails(task)
            TaskAdapter.SELECT_NEXT -> {
                TaskRepository.moveForward(task)
                refreshTasks()
            }
        }
    }

    private fun confirmRemove(task: Task) {
        showBottomSheet(
            titleDialog = R.string.confirm_delete_title,
            titleButton = R.string.confirm_delete_button,
            message = getString(R.string.confirm_delete_message, task.description)
        ) {
            TaskRepository.remove(task)
            refreshTasks()
        }
    }

    private fun editTask(task: Task) {
        findNavController().navigate(
            R.id.action_homeFragment_to_formTaskFragment,
            FormTaskFragment.newBundle(task)
        )
    }

    private fun showDetails(task: Task) {
        showBottomSheet(
            titleDialog = R.string.task_details_title,
            message = task.description
        )
    }

    private fun refreshTasks() {
        taskAdapter.submitList(TaskRepository.getByStatus(Status.TODO))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
