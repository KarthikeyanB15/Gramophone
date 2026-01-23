package com.example.gramotunes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gramotunes.R
import com.example.gramotunes.data.model.MusicListModel
import com.example.gramotunes.utils.MusicUtils

class MusicListAdapter(
    private val onItemClick: (MusicListModel) -> Unit
) : ListAdapter<MusicListModel, MusicListAdapter.MusicViewHolder>(DiffCallback()) {

    class MusicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val artist: TextView = view.findViewById(R.id.tvArtist)
        val image: ImageView = view.findViewById(R.id.ivAlbumArt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music = getItem(position)
        holder.title.text = music.title
        holder.artist.text = music.artist
        holder.itemView.setOnClickListener {
            onItemClick(music)
        }

        Glide.with(holder.image.context)
            .load(MusicUtils.getAlbumArtUri(music.albumId))
            .placeholder(R.drawable.ic_gramatune_placeholder)
            .error(R.drawable.ic_gramatune_placeholder)
            .into(holder.image)
    }

}

class DiffCallback : DiffUtil.ItemCallback<MusicListModel>() {

    override fun areItemsTheSame(
        oldItem: MusicListModel,
        newItem: MusicListModel
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: MusicListModel,
        newItem: MusicListModel
    ): Boolean = oldItem == newItem
}

