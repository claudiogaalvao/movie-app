package com.claudiogalvaodev.moviemanager.ui.mylists

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.claudiogalvaodev.moviemanager.R
import com.claudiogalvaodev.moviemanager.data.bd.entity.MyList
import com.claudiogalvaodev.moviemanager.databinding.FragmentMyListsBinding
import com.claudiogalvaodev.moviemanager.ui.adapter.ForwardWithImageAdapter
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.viewmodel.ext.android.viewModel

@SuppressLint("InflateParams")
class MyListsFragment : Fragment() {

    private val viewModel: MyListsViewModel by viewModel()
    private val binding by lazy {
        FragmentMyListsBinding.inflate(layoutInflater)
    }

    private lateinit var myListsAdapter: ForwardWithImageAdapter

    private val alertDialog by lazy {
        context?.let {
            val builder = AlertDialog.Builder(it)

            val dialogView = layoutInflater.inflate(R.layout.custom_dialog_mylists_form, null)
            val myListEditText = dialogView?.findViewById<EditText>(R.id.my_lists_form_edittext)

            builder.setTitle("Create new list")
                .setView(dialogView)
                .setPositiveButton("Create") { _, _ ->
                    val newListName = myListEditText?.text
                    viewModel.createNewList(MyList(id = 0, name = newListName.toString()))
                }
                .setNegativeButton(resources.getString(R.string.filter_alertdialog_negative), null)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MyListsActivity).setToolbarTitle(getString(R.string.my_lists))
        viewModel.getAllMyLists()

        setupAdapter()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupAdapter() {
        myListsAdapter = ForwardWithImageAdapter().apply {
            onItemClick = { itemId ->
                Log.i("itemid", itemId.toString())
            }
        }
    }

    private fun setupRecyclerView() {
        binding.myListsRecyclerview.adapter = myListsAdapter
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.myLists.collectLatest { allMyLists ->
                setMyLists(allMyLists)
            }
        }
    }

    private fun setMyLists(myLists: List<MyList>) {
        myListsAdapter.submitList(myLists)
    }

    private fun setupListeners() {
        binding.myListsFab.setOnClickListener {
            alertDialog?.show()
        }
    }

    private fun showDialog() {
        context?.let {
            val dialog = Dialog(it)
            dialog.setContentView(R.layout.custom_dialog_mylists_form)
            dialog.show()
        }


    }

}