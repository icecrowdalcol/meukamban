package com.example.meukanban.ui

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.meukanban.R
import com.example.meukanban.data.TaskRepository
import com.example.meukanban.data.model.Status
import com.example.meukanban.data.model.Task
import com.example.meukanban.databinding.FragmentFormTaskBinding
import com.example.meukanban.util.initToolbar
import com.example.meukanban.util.showBottomSheet

class FormTaskFragment : Fragment() {

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private var editingTask: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        editingTask = arguments?.getParcelableCompat(ARG_TASK)
        fillFieldsIfEditing()
        initListener()
    }

    private fun fillFieldsIfEditing() {
        val task = editingTask ?: return
        binding.editTextDescricao.setText(task.description)
        when (task.status) {
            Status.TODO -> binding.rbTodo.isChecked = true
            Status.DOING -> binding.rbDoing.isChecked = true
            Status.DONE -> binding.rbDone.isChecked = true
        }
    }

    private fun initListener() {
        binding.buttonSave.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val description = binding.editTextDescricao.text.toString().trim()
        if (description.isNotBlank()) {
            saveTask(description)
        } else {
            showBottomSheet(message = getString(R.string.description_empty_form_task_fragment))
        }
    }

    private fun selectedStatus(): Status {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.rbDoing -> Status.DOING
            R.id.rbDone -> Status.DONE
            else -> Status.TODO
        }
    }

    private fun saveTask(description: String) {
        val status = selectedStatus()
        val current = editingTask
        if (current != null) {
            TaskRepository.update(current.copy(description = description, status = status))
        } else {
            TaskRepository.add(description, status)
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TASK = "arg_task"

        fun newBundle(task: Task? = null): Bundle {
            val bundle = Bundle()
            if (task != null) {
                bundle.putParcelable(ARG_TASK, task)
            }
            return bundle
        }
    }
}

private inline fun <reified T : android.os.Parcelable> Bundle.getParcelableCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key)
    }
}
