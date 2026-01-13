package com.example.gramotunes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gramotunes.R
import com.example.gramotunes.data.MusicListModel

class MusicListAdapter(
    private val list: List<MusicListModel>,
    private val onItemClick: (MusicListModel) -> Unit
) : RecyclerView.Adapter<MusicListAdapter.MusicViewHolder>() {

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
        val music = list[position]
        holder.title.text = music.title
        holder.artist.text = music.artist
        holder.itemView.setOnClickListener {
            onItemClick(music)
        }

        Glide.with(holder.image.context)
            .load(music.albumArt) // Bitmap or Uri
            .placeholder(R.drawable.ic_gramatune_placeholder)
            .error(R.drawable.ic_gramatune_placeholder)
            .into(holder.image)
    }

    override fun getItemCount() = list.size
}