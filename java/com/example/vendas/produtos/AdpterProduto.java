package com.example.vendas.produtos;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendas.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

public class AdpterProduto extends RecyclerView.Adapter<AdpterProduto.MyViewHolder> {
    private List<Produto> listarProdutos;
    private List<Integer> ids;
    static boolean produtoVenda = false;
    private Locale localeBR = new Locale("pt","BR");

    public AdpterProduto(List<Produto> lista, boolean produtoVenda){
        this.produtoVenda = produtoVenda;
        this.listarProdutos = lista;
    }
    public void setIds(List<Integer> ids){
        this.ids = ids;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapterlistarproduto,parent,false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NumberFormat valorReal = NumberFormat.getCurrencyInstance(localeBR);
        Produto produto = listarProdutos.get(position);
        holder.codigo.setText(produto.getCodigo());
        holder.nome.setText(produto.getNome());
        holder.quantidade.setText(String.valueOf(produto.getQuantidade()));
        holder.valor.setText((valorReal.format(produto.getValor())));
        holder.categoria.setText(produto.getCategoria());
        holder.tamanho.setText(produto.getTamanho());
        if(!produto.getImagem().equals("")){
            byte[] img;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                img = Base64.getDecoder().decode(produto.getImagem());
                Bitmap imagemDecodificada = BitmapFactory.decodeByteArray(img,0,img.length);
                holder.imagemProduto.setImageBitmap(imagemDecodificada);
            }
        }

        holder.layout.setOnClickListener(v -> Log.d("Clique","Clique curto funcionou"));
        holder.layout.setOnLongClickListener(v -> {
            setIdProduto(ids.get(position));
            setposicao(holder.getAdapterPosition());
            setProduto(produto);
            return false;
        });

    }
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView codigo,nome,valor,quantidade,categoria,tamanho;
        ImageView imagemProduto;
        View layout;


        public MyViewHolder( View itemView) {
            super(itemView);

            iniciarComponentes();
            itemView.setOnCreateContextMenuListener(this);

        }
        public void iniciarComponentes(){
            codigo = itemView.findViewById(R.id.textViewCodigo);
            nome = itemView.findViewById(R.id.textViewNomeProduto);
            valor = itemView.findViewById(R.id.textViewValorProduto);
            quantidade = itemView.findViewById(R.id.textViewQuantidade);
            categoria = itemView.findViewById(R.id.textViewCategoria);
            tamanho = itemView.findViewById(R.id.textViewTamanho);
            imagemProduto = itemView.findViewById(R.id.imagemProduto);


            layout = itemView.findViewById(R.id.layoutOpcoes);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
           if (!produtoVenda){
               menu.add(Menu.NONE,R.id.deletar,Menu.NONE,"Deletar");
               menu.add(Menu.NONE,R.id.atualizar,Menu.NONE,"Atualizar");
           }else {
               menu.add(Menu.NONE,R.id.adicionarAoCarrinho,Menu.NONE,"Adionar ao Carrinho");
               menu.add(Menu.NONE,R.id.removerDoCarrinho,Menu.NONE,"Remover do Carrinho");
           }

        }
    }

    public int getItemCount() {
        return listarProdutos.size();

    }

    public void setposicao(int posicao){
    }
    private Produto produto;
    public Produto getProduto(){
        return produto;
    }
    public void setProduto(Produto produto){
        this.produto = produto;
    }
    private int idProduto;
    public int getIdProduto(){
        return idProduto;
    }
    public void setIdProduto(int idProduto){
        this.idProduto = idProduto;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setFiltro(ArrayList<Produto> lista){
        listarProdutos = new ArrayList<>();
        listarProdutos.addAll(lista);
        notifyDataSetChanged();
    }
   /* public void setFiltroids(ArrayList<Integer> lista){
        ids = new ArrayList<>();
        ids.addAll(lista);
        notifyDataSetChanged();
    }*/
}
