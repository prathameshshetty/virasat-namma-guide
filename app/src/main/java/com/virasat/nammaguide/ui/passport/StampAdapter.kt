package com.virasat.nammaguide.ui.passport

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.virasat.nammaguide.databinding.ItemStampBinding
import com.virasat.nammaguide.ui.detail.SiteDetailActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StampAdapter : ListAdapter<PassportEntry, StampAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemStampBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PassportEntry) {
            binding.tvSiteName.text = item.site?.nameEn ?: "Unknown Site"
            binding.tvSiteKn.text = item.site?.nameKn ?: ""
            binding.tvDistrict.text = item.site?.district ?: ""
            binding.tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(item.checkIn.timestamp))
            binding.tvMethod.text = item.checkIn.method
            binding.root.setOnClickListener {
                item.site?.let { site ->
                    binding.root.context.startActivity(
                        Intent(binding.root.context, SiteDetailActivity::class.java).putExtra("site_id", site.id)
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemStampBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<PassportEntry>() {
            override fun areItemsTheSame(a: PassportEntry, b: PassportEntry) = a.checkIn.id == b.checkIn.id
            override fun areContentsTheSame(a: PassportEntry, b: PassportEntry) = a == b
        }
    }
}
