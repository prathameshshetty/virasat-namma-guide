package com.virasat.nammaguide.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.virasat.nammaguide.databinding.FragmentHomeBinding
import com.virasat.nammaguide.ui.detail.SiteDetailActivity
import com.virasat.nammaguide.ui.scanner.QRScannerActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val vm: HomeViewModel by viewModels()
    private lateinit var adapter: SiteListAdapter

    private val locationPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) requestLocation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SiteListAdapter { sd -> startActivity(Intent(requireContext(), SiteDetailActivity::class.java).putExtra("site_id", sd.site.id)) }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.etSearch.doOnTextChanged { t, _, _, _ -> vm.search(t?.toString() ?: "") }
        binding.fabQr.setOnClickListener { startActivity(Intent(requireContext(), QRScannerActivity::class.java)) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.filteredSites.collect { sites ->
                    adapter.submitList(sites)
                    binding.tvEmpty.isVisible = sites.isEmpty()
                }
            }
        }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocation()
        } else {
            locationPermLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestLocation() {
        try {
            LocationServices.getFusedLocationProviderClient(requireActivity())
                .lastLocation.addOnSuccessListener { loc -> loc?.let { vm.updateLocation(it) } }
        } catch (e: SecurityException) { /* handled */ }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
