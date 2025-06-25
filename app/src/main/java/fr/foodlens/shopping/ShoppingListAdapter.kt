package fr.foodlens.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.foodlens.R
import fr.foodlens.database.ShoppingListEntity

class ShoppingListAdapter(
    private val onClick: (ShoppingListEntity) -> Unit
): ListAdapter<ShoppingListEntity, ShoppingListViewHolder>(ShoppingListDiffCallback) {

    companion object {
        val ShoppingListDiffCallback = object : DiffUtil.ItemCallback<ShoppingListEntity>() {
            override fun areItemsTheSame(oldItem: ShoppingListEntity, newItem: ShoppingListEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ShoppingListEntity, newItem: ShoppingListEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_shopping_list, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.card.setOnClickListener {
            onClick.invoke(getItem(position))
        }
    }
}

class ShoppingListViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val listLabel: TextView = view.findViewById(R.id.list_label)
    val card: CardView = view.findViewById(R.id.shopping_list_card)

    fun bind(list: ShoppingListEntity) {
        listLabel.text = list.label
    }
}
