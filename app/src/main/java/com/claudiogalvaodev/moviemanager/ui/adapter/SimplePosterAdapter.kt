package com.claudiogalvaodev.moviemanager.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.claudiogalvaodev.moviemanager.data.bd.entity.MovieSavedEntity
import com.claudiogalvaodev.moviemanager.databinding.ItemSimplePosterBinding
import com.claudiogalvaodev.moviemanager.ui.model.MovieModel
import com.claudiogalvaodev.moviemanager.ui.adapter.SimplePosterAdapter.SimplePosterViewHolder
import com.squareup.picasso.Picasso

class SimplePosterAdapter: ListAdapter<MovieModel, SimplePosterViewHolder>(DIFF_CALLBACK) {

    var onItemClick: ((movieId: Int) -> Unit)? = null
    var onFullyViewedListener: (() -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimplePosterViewHolder {
        lastVisibleItemOnSreen(parent as RecyclerView)
        return SimplePosterViewHolder.create(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: SimplePosterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun lastVisibleItemOnSreen(
        parent: RecyclerView
    ) {
        parent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if(lastVisiblePosition == itemCount - 1) {
                    onFullyViewedListener?.invoke()
                }
            }
        })
    }

    class SimplePosterViewHolder(
        private val binding: ItemSimplePosterBinding,
        private val clickListener: ((movieId: Int) -> Unit)?
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieModel) {
            with(binding) {
                Picasso.with(root.context).load(movie.getPosterUrl()).into(itemSimplePosterImage)

                binding.root.setOnClickListener {
                    clickListener?.invoke(movie.id)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, clickListener: ((movieId: Int) -> Unit)?): SimplePosterViewHolder {
                val binding = ItemSimplePosterBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return SimplePosterViewHolder(binding, clickListener)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<MovieModel>() {
            override fun areItemsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}