package com.altimetrik.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.altimetrik.itunes.databinding.ListItemAlbumBinding
import com.altimetrik.model.Album
import com.altimetrik.utils.OnCartListener

class AlbumsAdapter(val context: Context, var albumsList: MutableList<Album>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlbumViewHolder(
            ListItemAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = albumsList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AlbumViewHolder).setModel(albumsList[position])
        holder.binding.checkbox.setOnCheckedChangeListener { _, _ ->
            val album = albumsList[position]
            album.selected = !album.selected
            if (album.selected) {
                selectedCount++
            } else {
                selectedCount--
            }
            val onCartListener: OnCartListener = context as OnCartListener
            onCartListener.onCart(selectedCount)
            notifyDataSetChanged()
        }
    }

    class AlbumViewHolder(var binding: ListItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setModel(item: Album) {
            binding.album = item
        }
    }
}