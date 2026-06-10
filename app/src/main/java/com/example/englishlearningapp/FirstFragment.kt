package com.example.englishlearningapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.englishlearningapp.databinding.FragmentFirstBinding
import kotlinx.coroutines.launch

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(AppDatabase.getDatabase(requireContext()).wordDao())
    }
    private val wordAdapter = WordAdapter { word ->
        wordViewModel.removeWord(word)
    }

    private var isFabMenuOpen = false
    
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_open) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_close) }
    private val fabOpen: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.fab_open) }
    private val fabClose: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.fab_close) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.wordList.adapter = wordAdapter
        setupSwipeToHide()
        observeWords()
        observeAddWordEvents()
        setupAddWordButton()
        setupSpeedDialFab()
    }

    private fun setupSpeedDialFab() {
        binding.mainFab.setOnClickListener {
            toggleFabMenu()
        }

        binding.fabHideEnglish.setOnClickListener {
            wordAdapter.setEnglishMask(true)
            toggleFabMenu()
        }

        binding.fabShowEnglish.setOnClickListener {
            wordAdapter.setEnglishMask(false)
            toggleFabMenu()
        }

        binding.fabHideJapanese.setOnClickListener {
            wordAdapter.setJapaneseMask(true)
            toggleFabMenu()
        }

        binding.fabShowJapanese.setOnClickListener {
            wordAdapter.setJapaneseMask(false)
            toggleFabMenu()
        }
    }

    private fun toggleFabMenu() {
        setVisibility(isFabMenuOpen)
        setAnimation(isFabMenuOpen)
        setClickable(isFabMenuOpen)
        isFabMenuOpen = !isFabMenuOpen
    }

    private fun setVisibility(isOpen: Boolean) {
        if (!isOpen) {
            binding.subFabHideEnglishContainer.isVisible = true
            binding.subFabShowEnglishContainer.isVisible = true
            binding.subFabHideJapaneseContainer.isVisible = true
            binding.subFabShowJapaneseContainer.isVisible = true
        } else {
            // アニメーション後に消すための処理が必要な場合もあるが、
            // ここでは簡易的に即座に非表示にする（アニメーション自体が透明度を下げるので違和感は少ない）
            binding.subFabHideEnglishContainer.isVisible = false
            binding.subFabShowEnglishContainer.isVisible = false
            binding.subFabHideJapaneseContainer.isVisible = false
            binding.subFabShowJapaneseContainer.isVisible = false
        }
    }

    private fun setAnimation(isOpen: Boolean) {
        if (!isOpen) {
            binding.subFabHideEnglishContainer.startAnimation(fabOpen)
            binding.subFabShowEnglishContainer.startAnimation(fabOpen)
            binding.subFabHideJapaneseContainer.startAnimation(fabOpen)
            binding.subFabShowJapaneseContainer.startAnimation(fabOpen)
            binding.mainFab.startAnimation(rotateOpen)
        } else {
            binding.subFabHideEnglishContainer.startAnimation(fabClose)
            binding.subFabShowEnglishContainer.startAnimation(fabClose)
            binding.subFabHideJapaneseContainer.startAnimation(fabClose)
            binding.subFabShowJapaneseContainer.startAnimation(fabClose)
            binding.mainFab.startAnimation(rotateClose)
        }
    }

    private fun setClickable(isOpen: Boolean) {
        if (!isOpen) {
            binding.fabHideEnglish.isClickable = true
            binding.fabShowEnglish.isClickable = true
            binding.fabHideJapanese.isClickable = true
            binding.fabShowJapanese.isClickable = true
        } else {
            binding.fabHideEnglish.isClickable = false
            binding.fabShowEnglish.isClickable = false
            binding.fabHideJapanese.isClickable = false
            binding.fabShowJapanese.isClickable = false
        }
    }

    private fun setupSwipeToHide() {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val word = wordAdapter.currentList[viewHolder.adapterPosition]
                wordViewModel.hideWord(word)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.wordList)
    }

    private fun observeWords() {
        wordViewModel.words.observe(viewLifecycleOwner) { words ->
            wordAdapter.submitList(words)
            binding.emptyState.isVisible = words.isEmpty()
            binding.wordList.isVisible = words.isNotEmpty()
        }
    }

    private fun observeAddWordEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    wordViewModel.errorMessage.collect { message ->
                        if (message != null) {
                            binding.englishWordInputLayout.error = message
                            wordViewModel.clearError()
                        }
                    }
                }
                launch {
                    wordViewModel.insertSuccess.collect {
                        binding.englishWordInput.setText("")
                        binding.japaneseMeaningInput.setText("")
                        binding.englishWordInput.requestFocus()
                    }
                }
            }
        }
    }

    private fun setupAddWordButton() {
        binding.addWordButton.setOnClickListener {
            val english = binding.englishWordInput.text?.toString().orEmpty()
            val japanese = binding.japaneseMeaningInput.text?.toString().orEmpty()
            clearInputErrors()

            var hasError = false
            if (english.isBlank()) {
                binding.englishWordInputLayout.error = getString(R.string.required_field)
                hasError = true
            }
            if (japanese.isBlank()) {
                binding.japaneseMeaningInputLayout.error = getString(R.string.required_field)
                hasError = true
            }
            if (hasError) return@setOnClickListener

            wordViewModel.addWord(english, japanese)
        }
    }

    private fun clearInputErrors() {
        binding.englishWordInputLayout.error = null
        binding.japaneseMeaningInputLayout.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
