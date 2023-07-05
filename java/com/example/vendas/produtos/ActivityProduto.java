package com.example.vendas.produtos;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityProduto extends AppCompatActivity {
    private List<Produto> listProdutos = new ArrayList<>();
    private List<Integer> ids = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AdpterProduto adpterProduto;
    private ProdutoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_padrao);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        iniciarComponentes();
        listar();
    }

    public void iniciarComponentes(){

        recyclerView = findViewById(R.id.recyclerViewPadrao);

        dao = new ProdutoDAO(this);

    }

    public void listar(){
        try {
            listProdutos = dao.listarTodos();
            ids = dao.recuperarId();
            if(listProdutos.size()>=1){
                adpterProduto = new AdpterProduto(listProdutos,false);
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

    @SuppressLint("NonConstantResourceId")
    public boolean onContextItemSelected(MenuItem item){

        int id = ((AdpterProduto) Objects.requireNonNull(recyclerView.getAdapter())).getIdProduto();

        switch (item.getItemId()){
            case R.id.deletar:
                AlertDialog.Builder confirmar = new AlertDialog.Builder(this);
                confirmar.setTitle("Atenção!\n");

                confirmar.setMessage("Deseja deletar este produto ?");
                confirmar.setCancelable(false);
                confirmar.setPositiveButton("Sim", (dialog, which) -> {

                dao.remover(id);
                listar();

                });
                confirmar.setNegativeButton("Não", null);
                confirmar.create().show();
                break;
            case R.id.atualizar:
                update(String.valueOf(id));
                break;
            default:
                Toast.makeText(this, "Opção inválida", Toast.LENGTH_SHORT).show();
                break;        }
        return super.onContextItemSelected(item);
    }

    public void update(String id){
        Intent intent = new Intent(ActivityProduto.this, ActivityAtualizarProduto.class);
        intent.putExtra("id",id);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
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
        MenuItem item = menu.findItem(R.id.cadastrarItem);
        item.setOnMenuItemClickListener(item1 -> {
            Intent intent = new Intent(ActivityProduto.this, ActivityCadastrarProduto.class);
            startActivity(intent);
            finish();
            return true;
        });
        return  super.onCreateOptionsMenu(menu);
    }
}