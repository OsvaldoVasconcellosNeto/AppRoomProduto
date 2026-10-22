package com.example.approomproduto.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.approomproduto.databinding.ItemProdutoBinding
import java.util.Locale

class ProductAdapter(
    private var products: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(
        val binding: ItemProdutoBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding = ItemProdutoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val product = products[position]

        holder.binding.txtNomeProduto.text = product.name

        holder.binding.txtPrecoProduto.text = String.format(
            Locale("pt", "BR"),
            "Preço: R$ %.2f",
            product.price
        )

        holder.binding.txtDescricaoProduto.text =
            "Descrição: ${product.description}"
    }

    override fun getItemCount(): Int = products.size

    fun atualizarLista(novosProdutos: List<Product>) {
        products = novosProdutos
        notifyDataSetChanged()
    }
}