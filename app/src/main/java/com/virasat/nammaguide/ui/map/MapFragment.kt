package com.virasat.nammaguide.ui.map

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.virasat.nammaguide.R
import com.virasat.nammaguide.databinding.FragmentMapBinding
import com.virasat.nammaguide.ui.detail.SiteDetailActivity
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val vm: MapViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(15.0, 75.7), 6.5f))
        map.setOnInfoWindowClickListener { marker ->
            val siteId = marker.tag as? String ?: return@setOnInfoWindowClickListener
            startActivity(Intent(requireContext(), SiteDetailActivity::class.java).putExtra("site_id", siteId))
        }
        observeSites()
    }

    private fun observeSites() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.allSites.collect { sites ->
                    googleMap?.clear()
                    sites.forEach { site ->
                        val color = if (site.isVisited) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_RED
                        val marker = googleMap?.addMarker(
                            MarkerOptions()
                                .position(LatLng(site.latitude, site.longitude))
                                .title(site.nameEn)
                                .snippet("${site.dynasty} | ${site.district}")
                                .icon(BitmapDescriptorFactory.defaultMarker(color))
                        )
                        marker?.tag = site.id
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
