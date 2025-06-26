package fr.foodlens.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
)

@Entity(
    tableName = "shopping_list_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["listId"])],
    primaryKeys = ["id", "listId"]
)
data class ShoppingListItemEntity(
    val id: String,
    val label: String,
    val listId: Long,
    val quantity: String,
    val checked: Boolean,
    val nutriscoreGrade: String
)

@Entity(tableName = "fridge_items")
data class FridgeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val label: String,
    val quantity: String,
    val nutriscoreGrade: String
)