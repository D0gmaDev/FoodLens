package fr.foodlens.shopping

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.foodlens.DefaultActivity
import fr.foodlens.R
import fr.foodlens.database.AppDatabase
import kotlinx.coroutines.launch

class ChooseListActivity : DefaultActivity() {
    private val db = AppDatabase.getDatabase(this)
    private val shoppingListDao = db.shoppingListDao()

    private val adapter = ShoppingListAdapter(onClick = { list ->
        startActivity(Intent(this, ScanActivity::class.java).putExtra("listId", list.id))
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_list)

        lifecycleScope.launch {
            val shoppingLists = shoppingListDao.getAll()
            adapter.submitList(shoppingLists)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}