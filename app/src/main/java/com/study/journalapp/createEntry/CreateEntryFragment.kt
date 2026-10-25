package com.study.journalapp.createEntry

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.study.journalapp.ImageAdapter
import com.study.journalapp.R
import com.study.journalapp.data.EntryDatabase
import com.study.journalapp.data.EntryRepository
import com.study.journalapp.databinding.FragmentCreateEntryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CreateEntryFragment : Fragment() {
    private lateinit var viewModel: CreateEntryViewModel
    private var _binding: FragmentCreateEntryBinding? = null
    private val binding get() = _binding!!

    private val pickImages = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) copyAllToInternalStorage(uris)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateEntryBinding.inflate(inflater, container, false)
        val view = binding.root

        val entryId = CreateEntryFragmentArgs.fromBundle(requireArguments()).entryId

        val application = requireNotNull(this.activity).application
        val dao = EntryDatabase.getInstance(application).entryDao
        val repository = EntryRepository(dao)
        val viewModelFactory = CreateEntryViewModelFactory(entryId, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CreateEntryViewModel::class.java)

        if (viewModel.isEditMode) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit entry"
        }

        binding.createTitle.doOnTextChanged { text, _, _, _ ->
            viewModel.onTitleChanged(text.toString())
        }
        binding.createContent.doOnTextChanged { text, _, _, _ ->
            viewModel.onContentChanged(text.toString())
        }

        val imageAdapter = ImageAdapter(onRemove = { path -> viewModel.removeImage(path) })
        binding.imageList.adapter = imageAdapter
        binding.imageList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.addPhotoButton.setOnClickListener {
            pickImages.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.title.collect { title ->
                        if (binding.createTitle.text.toString() != title) {
                            binding.createTitle.setText(title)
                            binding.createTitle.setSelection(title.length)
                        }
                    }
                }
                launch {
                    viewModel.content.collect { content ->
                        if (binding.createContent.text.toString() != content) {
                            binding.createContent.setText(content)
                            binding.createContent.setSelection(content.length)
                        }
                    }
                }
                launch {
                    viewModel.imagePaths.collect { paths ->
                        imageAdapter.submitList(paths)
                        binding.imageList.isVisible = paths.isNotEmpty()
                    }
                }
                launch {
                    viewModel.canSave.collect { binding.createButton.isEnabled = it }
                }
            }
        }

        binding.createButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (viewModel.saveEntry()) {
                    Toast.makeText(requireContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Snackbar.make(binding.root, R.string.entry_save_failed, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        return view
    }

    private fun copyAllToInternalStorage(uris: List<Uri>) {
        val appContext = requireContext().applicationContext
        viewLifecycleOwner.lifecycleScope.launch {
            val paths = withContext(Dispatchers.IO) {
                uris.mapIndexed { _, uri ->
                    val file = File(appContext.filesDir, "entry_${System.currentTimeMillis()}.jpg")
                    appContext.contentResolver.openInputStream(uri)?.use { input ->
                        file.outputStream().use { output -> input.copyTo(output) }
                    }
                    file.absolutePath
                }
            }
            viewModel.addImages(paths)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}