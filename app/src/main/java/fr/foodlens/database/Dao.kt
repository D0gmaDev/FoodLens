package fr.foodlens.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_lists")
    suspend fun getAll(): List<ShoppingListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: ShoppingListEntity): Long

    @Delete
    suspend fun delete(list: ShoppingListEntity)

    @Query("DELETE FROM shopping_lists WHERE id = :listId")
    suspend fun deleteShoppingListById(listId: Long)
}

@Dao
interface ShoppingListItemDao {
    @Query("SELECT * FROM shopping_list_items WHERE listId = :listId")
    suspend fun getItemsForList(listId: Long): List<ShoppingListItemEntity>

    @Query("SELECT * FROM shopping_list_items WHERE id = :itemId AND listId = :listId")
    suspend fun getItemById(itemId: String, listId: Long): ShoppingListItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingListItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingListItemEntity>)

    @Delete
    suspend fun delete(item: ShoppingListItemEntity)

    @Query("UPDATE shopping_list_items SET checked = :isChecked WHERE id = :itemId AND listId = :listId")
    suspend fun updateItemCheckStatus(listId: Long, itemId: String, isChecked: Boolean)
}

@Dao
interface FridgeItemDao {
    @Query("SELECT * FROM fridge_items")
    suspend fun getAll(): List<FridgeItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FridgeItemEntity): Long

    @Delete
    suspend fun delete(item: FridgeItemEntity)

    @Query("DELETE FROM fridge_items WHERE id = :itemId")
    suspend fun deleteFridgeItemById(itemId: Long)
}