package com.virasat.nammaguide.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.virasat.nammaguide.databinding.ItemSiteBinding

class SiteListAdapter(private val onClick: (SiteWithDistance) -> Unit) :
    ListAdapter<SiteWithDistance, SiteListAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemSiteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SiteWithDistance) {
            binding.tvNameEn.text = item.site.nameEn
            binding.tvNameKn.text = item.site.nameKn
            binding.tvDistrict.text = item.site.district
            binding.tvType.text = item.site.siteType.replaceFirstChar { it.uppercase() }
            binding.tvDistance.text = if (item.distanceM == Float.MAX_VALUE) "" else formatDistance(item.distanceM)
            binding.ivVisited.visibility = if (item.site.isVisited) android.view.View.VISIBLE else android.view.View.GONE
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemSiteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<SiteWithDistance>() {
            override fun areItemsTheSame(a: SiteWithDistance, b: SiteWithDistance) = a.site.id == b.site.id
            override fun areContentsTheSame(a: SiteWithDistance, b: SiteWithDistance) = a == b
        }

        fun formatDistance(m: Float) = when {
            m < 1000 -> "${m.toInt()} m"
            else -> "${"%.1f".format(m / 1000)} km"
        }
    }
}
