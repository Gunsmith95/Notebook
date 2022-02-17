package com.example.collection

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collection.databinding.ActivityMainBinding
import com.example.collection.db.MyAdapter
import com.example.collection.db.MyDbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bindingClass: ActivityMainBinding
    private val myDbManager = MyDbManager(this)
    private val myAdapter = MyAdapter(ArrayList(), this)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        init()
        initSearchView()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun onClickNew(view: View) {
        val i = Intent(this, EditActivity::class.java)
        startActivity(i)
    }

    private fun init() {

        bindingClass.recyclerView.layoutManager = LinearLayoutManager(this)
        val swapHelper = getSwapManager()
        swapHelper.attachToRecyclerView(bindingClass.recyclerView)
        bindingClass.recyclerView.adapter = myAdapter

    }

    private fun initSearchView() {
        bindingClass.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fillAdapter(newText!!)
                return true
            }
        })
    }

    private fun fillAdapter(text: String) {

        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {

            val list = myDbManager.readDbData(text)
            myAdapter.updateAdapter(list)
            if (list.size > 0) {
                bindingClass.tvNoElements.visibility = View.GONE
            } else {
                bindingClass.tvNoElements.visibility = View.VISIBLE
            }
        }
    }

    private fun getSwapManager(): ItemTouchHelper {

        return ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManager)
            }
        })
    }

}