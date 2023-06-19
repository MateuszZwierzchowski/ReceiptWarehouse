package com.example.receiptwarehouse

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.receiptwarehouse.databinding.ItemLayoutBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class RecycleViewAdapter(
    private val allItems: MutableList<ReceiptDataClass>
) : RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    var searchQuery: String = ""
        set(value) {
            field = value
            filterItems()
        }

    private var items: MutableList<ReceiptDataClass> = allItems.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(
            ItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(holder.adapterPosition, items[holder.adapterPosition])
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(items[position].filepath)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: ReceiptDataClass)
    }

    class ViewHolder(binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.imageViewXYZ
    }

    private fun filterItems() {
        items = if (searchQuery.isBlank()) {
            allItems.toMutableList()
        } else {
            allItems.filter { it.textContent.contains(searchQuery, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }
}
