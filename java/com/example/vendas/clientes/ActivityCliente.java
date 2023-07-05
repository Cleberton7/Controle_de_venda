package com.example.vendas.clientes;

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

public class ActivityCliente extends AppCompatActivity {

    private List<Cliente> listaCliente = new ArrayList<>();
    private List<Integer> ids = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AdpterCliente adpterCliente;
    private ClienteDAO dao;

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
        dao = new ClienteDAO(this);
    }
    public void listar(){
        try {
            listaCliente = dao.listarTodos();
            ids = dao.recuperarId();
            if(listaCliente.size()>=1){
                adpterCliente = new AdpterCliente(listaCliente);
                adpterCliente.setIds(ids);
                layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adpterCliente);

            }else{
                Toast.makeText(this, "Sem clientes cadastrados", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.d("error",e.toString());
        }
        Log.d("dv",String.valueOf(listaCliente.size()));
    }
    @SuppressLint("NonConstantResourceId")
    public boolean onContextItemSelected(MenuItem item){

        int id = ((AdpterCliente) Objects.requireNonNull(recyclerView.getAdapter())).getIdCliente();

        switch (item.getItemId()){
            case R.id.deletar:
                AlertDialog.Builder confirmar = new AlertDialog.Builder(this);
                confirmar.setTitle("Atenção!\n");

                confirmar.setMessage("Deseja deletar este Cliente ?");
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
        Intent intent = new Intent(ActivityCliente.this, ActivityAtualizarCliente.class);
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
                ArrayList<Cliente> listClienteFiltro = new ArrayList<>();
                ArrayList<Integer> idsfiltrados = new ArrayList<>();
                for (Cliente cliente:listaCliente){

                    String nome = cliente.getNome().toLowerCase();
                    String cpf = cliente.getCpf();
                    String tel = cliente.getTelefone();
                    int idFiltrado=dao.recuperarIDFiltro(cpf);

                    if(nome.contains(newText) || cpf.contains(newText) || tel.contains(newText)){
                        listClienteFiltro.add(cliente);
                        idsfiltrados.add(idFiltrado);
                    }
                }

                Log.d("tamanho lista filtro", String.valueOf(listClienteFiltro.size() ));
                adpterCliente.setIds(idsfiltrados);
                adpterCliente.setFiltro(listClienteFiltro);
                return true;
            }
        }) ;
        MenuItem item = menu.findItem(R.id.cadastrarItem);
        item.setOnMenuItemClickListener(item1 -> {
            Intent intent = new Intent(ActivityCliente.this, ActivityCadastrarCliente.class);
            startActivity(intent);
            finish();
            return true;
        });
        return  super.onCreateOptionsMenu(menu);
    }

}