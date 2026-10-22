package com.example.approomproduto.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.approomproduto.dao.ProductDao
import com.example.approomproduto.databinding.ActivityMainBinding
import com.example.approomproduto.db.AppDatabase
import com.example.approomproduto.model.Product
import com.example.approomproduto.model.ProductAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var db: AppDatabase
    private lateinit var productDao: ProductDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa o ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inicializa o banco de dados Room de forma recomendada (sem usar allowMainThreadQueries)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        )
            .fallbackToDestructiveMigration() // Usado para recriar o DB se o esquema mudar
            .build()
        productDao = db.productDao()
        productAdapter = ProductAdapter(emptyList())

        binding.recyclerProdutos.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)

        binding.recyclerProdutos.adapter = productAdapter
        // Exemplo de inserção de produto
        binding.btnInserirProduto.setOnClickListener {
            val name = binding.editProductName.text.toString()
            val priceText = binding.editProductPrice.text.toString()
            val price = priceText.trim().replace(",", ".").toDoubleOrNull()
            if (price == null || price <= 0) {
                // 🟢 ALTERADO 2: Mostra o erro no próprio campo
                binding.editProductPrice.error = "Informe um preço válido"
                binding.editProductPrice.requestFocus()
                return@setOnClickListener
            } else {
                // 🟢 ALTERADO 3: Remove o erro quando o preço é válido
                binding.editProductPrice.error = null
            }
            val description = binding.editProductDescription.text.toString()
            // Verifica se os campos estão preenchidos
            if (name.isNotEmpty() && priceText.isNotEmpty()) {
                val price = priceText.toDoubleOrNull() ?: 0.0
                val product = Product(name = name, price = price, description =
                description)
                // Insere o produto no banco de dados de forma assíncrona (usando corrotina)
                lifecycleScope.launch {
                    productDao.insertAll(product)
                    Log.i("TAG", "Produto inserido: $name - R$$price")
                    Toast.makeText(this@MainActivity, "Produto inserido!",
                        Toast.LENGTH_SHORT).show()
                }
                binding.editProductName.text.clear()
                binding.editProductPrice.text.clear()
                binding.editProductDescription.text.clear()
                binding.editProductName.requestFocus()
            } else {
                Toast.makeText(this, "Preencha os campos obrigatórios!",
                    Toast.LENGTH_SHORT).show()
            }
        }
        // Exemplo de recuperação de produtos
        binding.btnListarProdutos.setOnClickListener {
            lifecycleScope.launch {
                try {
                    // Busca os produtos no banco
                    val products = productDao.getAll()

                    // Atualiza a lista do RecyclerView
                    productAdapter.atualizarLista(products)

                    // NOVO: calcula a quantidade e a soma dos preços
                    val quantidade = products.size
                    val total = products.sumOf { it.price }

                    // NOVO: exibe o resumo na tela
                    binding.txtResumoProdutos.text = String.format(
                        java.util.Locale("pt", "BR"),
                        "Quantidade de produtos: %d\nTotal dos preços: R$ %.2f",
                        quantidade,
                        total
                    )

                    if (products.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Nenhum produto encontrado.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.i(
                            "TAG",
                            "Quantidade: $quantidade | Total: R$ $total"
                        )
                    }

                } catch (e: Exception) {
                    Log.e("TAG", "Erro ao listar produtos", e)

                    Toast.makeText(
                        this@MainActivity,
                        "Erro ao listar produtos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.btnBuscar.setOnClickListener {

            // Recupera o texto digitado
            val texto = binding.editBuscarProduto.text.toString().trim()

            if (texto.isEmpty()) {
                binding.editBuscarProduto.error = "Digite um nome para buscar"
                binding.editBuscarProduto.requestFocus()
                return@setOnClickListener
            }

            binding.editBuscarProduto.error = null

            lifecycleScope.launch {
                try {
                    // Busca nomes que contenham o texto digitado
                    val products = productDao.buscarPorNome("%$texto%")

                    // Atualiza a lista do RecyclerView
                    productAdapter.atualizarLista(products)

                    // Calcula quantidade e soma dos preços
                    val quantidade = products.size
                    val total = products.sumOf { it.price }

                    // Exibe o resumo dos resultados
                    binding.txtResumoProdutos.text = String.format(
                        java.util.Locale("pt", "BR"),
                        "Resultados: %d produto(s)\nTotal dos preços: R$ %.2f",
                        quantidade,
                        total
                    )

                    if (products.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Nenhum produto encontrado.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    Log.e("TAG", "Erro ao buscar produtos", e)

                    Toast.makeText(
                        this@MainActivity,
                        "Erro ao buscar produtos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
