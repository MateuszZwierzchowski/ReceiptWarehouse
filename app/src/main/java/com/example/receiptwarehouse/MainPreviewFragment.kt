package com.example.receiptwarehouse

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.receiptwarehouse.databinding.FragmentMainPreviewBinding

class MainPreviewFragment : Fragment() {

    private var _binding: FragmentMainPreviewBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPreviewBinding.inflate(inflater, container, false)

        val reciepes = getReciepes()
        val adapter = RecycleViewAdapter(reciepes)

        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        adapter.setOnClickListener(object : RecycleViewAdapter.OnClickListener {
            override fun onClick(position: Int, model: ReceiptDataClass) {
                val transaction = parentFragmentManager.beginTransaction()
                (activity as MainActivity).getReciepeDetails()
                    ?.let { transaction.replace(R.id.nav_host_fragment_content_main, it) }
                transaction.commit()

                val toSend = model.filepath

                viewModel.sharedData.value = toSend
            }
        })

        binding.searchViewXYZ.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.searchQuery = newText.orEmpty()
                return true
            }
        })

        binding.recyclerView.layoutManager = GridLayoutManager(activity, 1)
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun getReciepes(): MutableList<ReceiptDataClass> {
        val reciepes = Database.getAll()
        return reciepes
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}