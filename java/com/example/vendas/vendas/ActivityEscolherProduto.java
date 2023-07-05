package com.example.vendas.vendas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendas.R;
import com.example.vendas.classes.ProdutosEscolhidos;
import com.example.vendas.produtos.AdpterProduto;
import com.example.vendas.produtos.Produto;
import com.example.vendas.produtos.ProdutoDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityEscolherProduto extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Produto> listProdutos = new ArrayList<>();
    private List<Integer> ids = new ArrayList<>();
    private List<String> produtosEscolhidos = new ArrayList<>();

    private RecyclerView.LayoutManager layoutManager;
    private AdpterProduto adpterProduto;
    private ProdutoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_padrao);
        //produtoEsc = getIntent().getStringExtra("produto");

        inicializiarComponentes();
    }
    public void inicializiarComponentes(){

        recyclerView = findViewById(R.id.recyclerViewPadrao);
        dao = new ProdutoDAO(this);
        listar();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_carrinho,menu);
        MenuItem menuItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Buscar Produto");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<Produto> listprodutosFiltro = new ArrayList<>();
                ArrayList<Integer> idsfiltrados = new ArrayList<>();
                for (Produto produto:listProdutos){

                    String nome = produto.getNome().toLowerCase();
                    String cod = produto.getCodigo();
                    String cat = produto.getCategoria().toLowerCase();
                    int idFiltrado=dao.recuperarIDFiltro(nome);

                    if(nome.contains(newText) || cod.contains(newText) || cat.contains(newText)){
                        listprodutosFiltro.add(produto);
                        idsfiltrados.add(idFiltrado);
                    }
                }

                Log.d("tamanho lista filtro", String.valueOf(listprodutosFiltro.size() ));
                adpterProduto.setIds(idsfiltrados);
                adpterProduto.setFiltro(listprodutosFiltro);
                return true;
            }
        }) ;
        MenuItem item = menu.findItem(R.id.carrinho);
        item.setOnMenuItemClickListener(item1 -> {
            carrinho();
            return true;
        });
        return  super.onCreateOptionsMenu(menu);
    }
    @SuppressLint("NonConstantResourceId")
    public boolean onContextItemSelected(MenuItem item){

        int id = ((AdpterProduto) Objects.requireNonNull(recyclerView.getAdapter())).getIdProduto();

        switch (item.getItemId()){
            case R.id.removerDoCarrinho:
                AlertDialog.Builder confirmar = new AlertDialog.Builder(this);
                confirmar.setTitle("Atenção!\n");

                confirmar.setMessage("Deseja remover esse produto do carrinho ?");
                confirmar.setCancelable(false);
                confirmar.setPositiveButton("Sim", (dialog, which) -> {
                    if(produtosEscolhidos.size()>0){
                        for (int i = 0; i < produtosEscolhidos.size(); i++ ){
                            if(produtosEscolhidos.get(i).equals(listProdutos.get(id).getCodigo())){
                                produtosEscolhidos.remove(i);
                            }
                        }
                    }else{
                        Toast.makeText(this, "Produto não está no carrinho", Toast.LENGTH_SHORT).show();
                    }

                    listar();

                });
                confirmar.setNegativeButton("Não", null);
                confirmar.create().show();
                break;
            case R.id.adicionarAoCarrinho:
                AlertDialog.Builder adicionar = new AlertDialog.Builder(this);
                adicionar.setTitle("Atenção!\n");

                adicionar.setMessage("Deseja adicionar esse produto ao carrinho ?");
                adicionar.setCancelable(false);
                adicionar.setPositiveButton("Sim", (dialog, which) -> {
                    ProdutosEscolhidos escolhidos = new ProdutosEscolhidos();
                    produtosEscolhidos = escolhidos.getProdutos();
                    Log.d("id",String.valueOf(id-1));
                    escolhidos.setProdutos(listProdutos.get(id-1).getCodigo());
                    Log.d("enviooooo",listProdutos.get(id-1).getCodigo());
                    /*if(!produtoEsc.isEmpty()){
                        produtoEsc = produtoEsc.concat(listProdutos.get(id-1).getCodigo());
                        produtoEsc = produtoEsc.concat(",");
                        escolhidos.setProdutos(produtoEsc);
                    }else {
                        produtoEsc = listProdutos.get(id-1).getCodigo();
                        produtoEsc = produtoEsc.concat(",");
                        escolhidos.setProdutos(produtoEsc);
                    }*/
                    listar();
                });
                adicionar.setNegativeButton("Não", null);
                adicionar.create().show();

                break;
            default:
                Toast.makeText(this, "Opção inválida", Toast.LENGTH_SHORT).show();
                break;        }
        return super.onContextItemSelected(item);
    }
    public void listar(){
        try {
            listProdutos = dao.listarTodos();
            ids = dao.recuperarId();
            if(listProdutos.size()>=1){
                adpterProduto = new AdpterProduto(listProdutos,true);
                adpterProduto.setIds(ids);
                layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adpterProduto);

            }else{
                Toast.makeText(this, "Sem produtos cadastrados", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.d("error",e.toString());
        }
    }
    public void carrinho(){
        Intent intent = new Intent(ActivityEscolherProduto.this,ActivityRegistrarVendas.class);
       /* ProdutosEscolhidos escolhidos = new ProdutosEscolhidos();
        escolhidos.recuperarProdutoFinal();*/
        startActivity(intent);
        finish();
       /* Intent intent = new Intent(ActivityEscolherProduto.this,ActivityCarrinho.class);
        startActivity(intent);*/
        //passar codigos dos produtos
    }
}