package com.example.receiptwarehouse

import android.media.ExifInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide


class show_reciepe_fragment : Fragment() {
    private lateinit var  viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_reciepe_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.backButton).setOnClickListener {
            goBack()
        }

        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]


        viewModel.sharedData.observe(viewLifecycleOwner) { data ->
            Glide.with(requireContext())
                .load(data)
                .into(view.findViewById<ImageView>(R.id.image_view))

            val date = getImageTakenDate(data)
            view.findViewById<TextView>(R.id.date_info).text = date
        }
    }

    private fun goBack() {
        val transaction = parentFragmentManager.beginTransaction()
        (activity as MainActivity).getReciepeList()
            ?.let { transaction.replace(R.id.nav_host_fragment_content_main, it) }
        transaction.commit()
    }

    fun getImageTakenDate(path: String): String? {
        val exif = ExifInterface(path)

        // The tag for the date/time the photo was taken
        val dateTimeTag = ExifInterface.TAG_DATETIME

        // Check if the tag is available
        if (exif.getAttribute(dateTimeTag) != null) {
            return exif.getAttribute(dateTimeTag)
        }

        return null
    }
}