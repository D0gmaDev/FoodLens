package fr.foodlens.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_lists")
    suspend fun getAllShoppingLists(): List<ShoppingListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(list: ShoppingListEntity)

    @Delete
    suspend fun deleteShoppingList(list: ShoppingListEntity)

    @Query("DELETE FROM shopping_lists WHERE id = :listId")
    suspend fun deleteShoppingListById(listId: Int)
}

@Dao
interface ShoppingListItemDao {
    @Query("SELECT * FROM shopping_list_items WHERE listId = :listId")
    suspend fun getItemsForList(listId: Int): List<ShoppingListItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingListItem(item: ShoppingListItemEntity)

    @Delete
    suspend fun deleteShoppingListItem(item: ShoppingListItemEntity)

    @Query("UPDATE shopping_list_items SET checked = :isChecked WHERE id = :itemId AND listId = :listId")
    suspend fun updateItemCheckStatus(listId: Int, itemId: Int, isChecked: Boolean)
}