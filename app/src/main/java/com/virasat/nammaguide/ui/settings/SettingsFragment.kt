package com.virasat.nammaguide.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.virasat.nammaguide.BuildConfig
import com.virasat.nammaguide.R
import com.virasat.nammaguide.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("virasat_prefs", 0)
        val currentLang = prefs.getString("language", "en") ?: "en"
        binding.switchLang.isChecked = currentLang == "kn"
        binding.tvLangLabel.text = if (currentLang == "kn") "ಕನ್ನಡ" else "English"

        binding.switchLang.setOnCheckedChangeListener { _, isKannada ->
            val lang = if (isKannada) "kn" else "en"
            prefs.edit().putString("language", lang).apply()
            binding.tvLangLabel.text = if (isKannada) "ಕನ್ನಡ" else "English"
            setLocale(lang)
        }

        binding.tvVersion.text = "Virasat – Namma Guide v${BuildConfig.VERSION_NAME}"

        binding.rowAbout.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_about)
        }
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        requireActivity().createConfigurationContext(config)
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
