package fr.foodlens.fridge

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.foodlens.R
import fr.foodlens.database.AppDatabase
import kotlinx.coroutines.launch

class FridgeActivity : AppCompatActivity() {

    private lateinit var adapter: FridgeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fridge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fridge)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.fridgeRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {

            val products = AppDatabase.getDatabase(this@FridgeActivity).fridgeItemDao().getAll()
                .toMutableList()

            adapter = FridgeAdapter(products) { product, position ->
                products.removeAt(position)
                adapter.notifyItemRemoved(position)

                lifecycleScope.launch {
                    AppDatabase.getDatabase(this@FridgeActivity).fridgeItemDao().delete(product)
                }
            }
            recyclerView.adapter = adapter
        }
    }
}