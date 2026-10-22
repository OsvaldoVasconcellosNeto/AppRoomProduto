package com.example.approomproduto.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.approomproduto.model.Product

@Dao
interface ProductDao {
    // suspend: o Room não permite consultas na main thread
    @Query("SELECT * FROM product")
    suspend fun getAll(): List<Product>
    @Insert
    suspend fun insertAll(vararg products: Product)
}