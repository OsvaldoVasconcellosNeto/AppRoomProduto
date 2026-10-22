package com.example.approomproduto.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.approomproduto.model.Product

@Dao
interface ProductDao {

    // Recupera todos os produtos
    @Query("SELECT * FROM product")
    suspend fun getAll(): List<Product>

    // Insere produtos no banco
    @Insert
    suspend fun insertAll(vararg products: Product)

    // NOVO: busca produtos pelo nome
    @Query("SELECT * FROM product WHERE name LIKE :nome")
    suspend fun buscarPorNome(nome: String): List<Product>
}