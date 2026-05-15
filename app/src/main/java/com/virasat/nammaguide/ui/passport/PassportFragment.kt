package com.virasat.nammaguide.ui.passport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.virasat.nammaguide.databinding.FragmentPassportBinding
import kotlinx.coroutines.launch

class PassportFragment : Fragment() {
    private var _binding: FragmentPassportBinding? = null
    private val binding get() = _binding!!
    private val vm: PassportViewModel by viewModels()
    private lateinit var adapter: StampAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPassportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StampAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.passportEntries.collect { entries ->
                        adapter.submitList(entries)
                        binding.tvEmpty.isVisible = entries.isEmpty()
                        binding.recyclerView.isVisible = entries.isNotEmpty()
                    }
                }
                launch {
                    vm.totalCheckIns.collect { count ->
                        binding.tvStampCount.text = "$count"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
