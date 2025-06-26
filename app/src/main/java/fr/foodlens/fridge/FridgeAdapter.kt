package fr.foodlens.fridge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.foodlens.R
import fr.foodlens.database.FridgeItemEntity

class FridgeAdapter(
    private val items: MutableList<FridgeItemEntity>,
    private val onDeleteClick: (FridgeItemEntity, Int) -> Unit
) : RecyclerView.Adapter<FridgeAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.product_text)
        val deleteButton: ImageButton = itemView.findViewById(R.id.product_delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.text.text = items[position].label
        holder.deleteButton.setOnClickListener {
            onDeleteClick(items[position], position)
        }
    }

    override fun getItemCount() = items.size
}
