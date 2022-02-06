package com.claudiogalvaodev.moviemanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.claudiogalvaodev.moviemanager.data.bd.entity.MyList
import com.claudiogalvaodev.moviemanager.databinding.ItemForwardWithImageBinding

class ForwardWithImageAdapter: ListAdapter<MyList, ForwardWithImageAdapter.ForwardWithImageViewHolder>(
    DIFF_CALLBACK) {

    var onItemClick: ((myListId: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForwardWithImageViewHolder {
        return ForwardWithImageViewHolder.create(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: ForwardWithImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ForwardWithImageViewHolder(
        private val binding: ItemForwardWithImageBinding,
        private val clickListener: ((myListId: Int) -> Unit)?
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(myList: MyList) {
            binding.itemForwardTitle.text = myList.name
        }

        companion object {
            fun create(parent: ViewGroup,
                       clickListener: ((myListId: Int) -> Unit)?
            ):ForwardWithImageViewHolder {
                val binding = ItemForwardWithImageBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return ForwardWithImageViewHolder(binding, clickListener)
            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<MyList>() {
            override fun areItemsTheSame(oldItem: MyList, newItem: MyList): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: MyList,
                newItem: MyList
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

}