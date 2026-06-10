package com.example.englishlearningapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.englishlearningapp.databinding.ItemWordBinding

class WordAdapter(
    private val onDelete: (Word) -> Unit
) : ListAdapter<Word, WordAdapter.WordViewHolder>(DiffCallback) {

    private var isEnglishMaskedGlobal = false
    private var isJapaneseMaskedGlobal = false
    
    // 特定のIDのカードが個別にマスク解除されているかを保持
    private val temporarilyRevealedEnglish = mutableSetOf<Int>()
    private val temporarilyRevealedJapanese = mutableSetOf<Int>()

    fun setEnglishMask(masked: Boolean) {
        isEnglishMaskedGlobal = masked
        temporarilyRevealedEnglish.clear()
        notifyDataSetChanged()
    }

    fun setJapaneseMask(masked: Boolean) {
        isJapaneseMaskedGlobal = masked
        temporarilyRevealedJapanese.clear()
        notifyDataSetChanged()
    }

    inner class WordViewHolder(
        private val binding: ItemWordBinding,
        private val onDelete: (Word) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.englishWord.text = word.englishWord
            binding.japaneseMeaning.text = word.japaneseMeaning
            binding.deleteButton.setOnClickListener { onDelete(word) }

            val showEnglishMask = isEnglishMaskedGlobal && !temporarilyRevealedEnglish.contains(word.id)
            binding.englishMask.isVisible = showEnglishMask
            binding.englishMask.setOnClickListener {
                temporarilyRevealedEnglish.add(word.id)
                binding.englishMask.isVisible = false
            }

            val showJapaneseMask = isJapaneseMaskedGlobal && !temporarilyRevealedJapanese.contains(word.id)
            binding.japaneseMask.isVisible = showJapaneseMask
            binding.japaneseMask.setOnClickListener {
                temporarilyRevealedJapanese.add(word.id)
                binding.japaneseMask.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WordViewHolder(binding, onDelete)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean =
            oldItem == newItem
    }
}
