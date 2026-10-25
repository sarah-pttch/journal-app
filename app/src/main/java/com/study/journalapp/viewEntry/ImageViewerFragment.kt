package com.study.journalapp.viewEntry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.study.journalapp.ImagePagerAdapter
import com.study.journalapp.databinding.FragmentImageViewerBinding

class ImageViewerFragment : Fragment() {
    private var _binding: FragmentImageViewerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageViewerBinding.inflate(inflater, container, false)

        val args = ImageViewerFragmentArgs.fromBundle(requireArguments())
        binding.pager.adapter = ImagePagerAdapter(args.imagePaths.toList())
        if (savedInstanceState == null) {
            binding.pager.setCurrentItem(args.startIndex, false)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}