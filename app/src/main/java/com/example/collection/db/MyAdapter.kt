package com.example.collection.db

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collection.EditActivity
import com.example.collection.R

class MyAdapter(listMain: ArrayList<ListItem>,contextM: Context) : RecyclerView.Adapter<MyAdapter.MyHolder>() {

    private var listArray = listMain
    var context = contextM

    class MyHolder(itemView: View,contextV: Context) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val context = contextV

        fun setData(item: ListItem) {

            tvTitle.text = item.title
            tvTime.text = item.time

            itemView.setOnClickListener {

                val intent = Intent(context,EditActivity::class.java).apply {

                    putExtra(MyItemConstants.I_TITLE_KEY,item.title)
                    putExtra(MyItemConstants.I_DESC_KEY,item.desc)
                    putExtra(MyItemConstants.I_URI_KEY,item.uri)
                    putExtra(MyItemConstants.I_ID_KEY,item.id)

                }
                context.startActivity(intent)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {

        val inflater = LayoutInflater.from(parent.context)

        return MyHolder(inflater.inflate(R.layout.rc_view, parent, false),context)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray[position])
    }

    override fun getItemCount(): Int {

        return listArray.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(listItems: List<ListItem>) {

        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int, dbManager: MyDbManager) {

        dbManager.removeItemFromDb(listArray[position].id.toString())
        listArray.removeAt(position)
        notifyItemRangeChanged(0,listArray.size)
        notifyItemRemoved(position)
    }

}