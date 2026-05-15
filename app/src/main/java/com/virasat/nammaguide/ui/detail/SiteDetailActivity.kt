package com.virasat.nammaguide.ui.detail

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.virasat.nammaguide.databinding.ActivitySiteDetailBinding
import com.virasat.nammaguide.service.AudioPlaybackService
import kotlinx.coroutines.launch

class SiteDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySiteDetailBinding
    private val vm: SiteDetailViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private var audioService: AudioPlaybackService? = null
    private var audioBound = false
    private var fromQR = false

    private val audioConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            audioService = (binder as AudioPlaybackService.AudioBinder).getService()
            audioBound = true
        }
        override fun onServiceDisconnected(name: ComponentName) { audioBound = false }
    }

    private val locationPerm = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) doGPSCheckIn() else Toast.makeText(this, "Location permission needed for GPS check-in", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val siteId = intent.getStringExtra("site_id") ?: run { finish(); return }
        fromQR = intent.getBooleanExtra("from_qr", false)
        val prefs = getSharedPreferences("virasat_prefs", 0)
        val lang = prefs.getString("language", "en") ?: "en"

        chatAdapter = ChatAdapter()
        binding.rvChat.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.rvChat.adapter = chatAdapter

        vm.loadSite(siteId, lang)
        if (fromQR) vm.checkInQR()

        setupObservers(lang)
        setupAudioControls()
        setupCheckIn()
        setupAIGuide(lang)

        bindService(Intent(this, AudioPlaybackService::class.java), audioConnection, Context.BIND_AUTO_CREATE)
    }

    private fun setupObservers(lang: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.site.collect { state ->
                        when (state) {
                            is UiState.Loading -> binding.progressBar.isVisible = true
                            is UiState.Success -> {
                                binding.progressBar.isVisible = false
                                val site = state.data
                                supportActionBar?.title = if (lang == "kn") site.nameKn else site.nameEn
                                binding.tvNameEn.text = site.nameEn
                                binding.tvNameKn.text = site.nameKn
                                binding.tvDynasty.text = site.dynasty
                                binding.tvPeriod.text = site.period
                                binding.tvDistrict.text = site.district
                                binding.tvType.text = site.siteType.replaceFirstChar { it.uppercase() }
                                binding.tvVisited.isVisible = site.isVisited
                                binding.btnAudio.isEnabled = site.audioUrl.isNotEmpty()
                            }
                            is UiState.Error -> {
                                binding.progressBar.isVisible = false
                                Toast.makeText(this@SiteDetailActivity, state.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                launch {
                    vm.description.collect { desc ->
                        if (desc.isNotEmpty()) {
                            binding.tvDescription.text = desc
                            binding.tvDescription.isVisible = true
                            binding.progressDesc.isVisible = false
                        } else {
                            binding.progressDesc.isVisible = true
                        }
                    }
                }
                launch {
                    vm.checkInState.collect { state ->
                        when {
                            state == "checked_in" -> {
                                Toast.makeText(this@SiteDetailActivity, "Checked in! Stamp added to your passport.", Toast.LENGTH_LONG).show()
                                binding.btnCheckin.text = "Visited ✓"
                                binding.btnCheckin.isEnabled = false
                            }
                            state == "already_checked_in" -> Toast.makeText(this@SiteDetailActivity, "You've already visited this site!", Toast.LENGTH_SHORT).show()
                            state.startsWith("too_far") -> {
                                val dist = state.split(":").getOrNull(1) ?: "?"
                                Toast.makeText(this@SiteDetailActivity, "You are ${dist}m away. Come within 200m to check in.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                launch {
                    vm.aiAnswer.collect { answer ->
                        if (answer.isNotEmpty()) {
                            chatAdapter.addMessage(ChatMessage(answer, false))
                            binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
                            binding.progressAi.isVisible = false
                        }
                    }
                }
                launch {
                    vm.isLoadingAI.collect { loading ->
                        binding.progressAi.isVisible = loading
                        binding.btnSend.isEnabled = !loading
                    }
                }
            }
        }
    }

    private fun setupAIGuide(lang: String) {
        binding.btnSend.setOnClickListener {
            val q = binding.etQuestion.text.toString().trim()
            if (q.isEmpty()) return@setOnClickListener
            chatAdapter.addMessage(ChatMessage(q, true))
            binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
            binding.etQuestion.setText("")
            vm.askGuide(q, lang)
        }
    }

    private fun setupAudioControls() {
        binding.btnAudio.setOnClickListener {
            val site = (vm.site.value as? UiState.Success)?.data ?: return@setOnClickListener
            if (site.audioUrl.isEmpty()) {
                Toast.makeText(this, "Audio not available for this site yet.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (audioBound) {
                if (audioService?.isPlaying() == true) audioService?.pause()
                else audioService?.playAudio(site.audioUrl, site.nameEn)
            }
        }
        binding.seekbarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                val speed = 0.75f + (progress * 0.25f)
                binding.tvSpeed.text = "${speed}x"
                if (audioBound) audioService?.setSpeed(speed)
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })
    }

    private fun setupCheckIn() {
        binding.btnCheckin.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                doGPSCheckIn()
            } else {
                locationPerm.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun doGPSCheckIn() {
        try {
            LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { loc ->
                if (loc != null) vm.checkInGPS(loc.latitude, loc.longitude)
                else Toast.makeText(this, "Could not get your location. Try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) { /* handled */ }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }

    override fun onDestroy() {
        super.onDestroy()
        if (audioBound) unbindService(audioConnection)
    }
}
