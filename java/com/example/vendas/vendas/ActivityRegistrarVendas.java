package com.example.vendas.vendas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vendas.R;
import com.example.vendas.classes.CheckBoxSalvar;
import com.example.vendas.classes.MaskEditUtil;
import com.example.vendas.classes.ProdutosEscolhidos;
import com.example.vendas.clientes.ActivityCadastrarCliente;
import com.example.vendas.clientes.Cliente;
import com.example.vendas.clientes.ClienteDAO;
import com.example.vendas.produtos.Produto;
import com.example.vendas.produtos.ProdutoDAO;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ActivityRegistrarVendas extends AppCompatActivity {

    private ImageView btn_escolher_data,btn_buscar_produtos;
    private EditText editData,editValor,editDesconto,editPrazo,editCpf,edit_produtos_escolhidos;
    private Button btn_cadastrar_venda;
    private CheckBox checkBoxDesconto,checkBoxPrazo;
    private final String[] mensagem = {"Venda resgistrada","Campos vazios!"};
    private String usuarioId,nomeVendedor;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Cliente clienteRecebido = null;
    private String produtosEscolhidos;
    private double desconto;
    double valorVendaFinal = 0.0;
    String produtosEsc;
    CheckBoxSalvar checkBoxSalvar = new CheckBoxSalvar();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_venda);
        Objects.requireNonNull(getSupportActionBar()).hide();

        edit_produtos_escolhidos = findViewById(R.id.edit_produtos_escolhidos);
        editValor = findViewById(R.id.edit_valor_venda);

        ProdutosEscolhidos escolhidos = new ProdutosEscolhidos();
        produtosEsc = escolhidos.recuperarProdutoFinal();


        edit_produtos_escolhidos.setText(produtosEsc);

        if(produtosEsc != null){
            Log.d("vindo do carrinho","ok");
            verificarProdutosEscolhidos(true);
            Log.d("sdasda",produtosEsc);
        }else{
            Log.d("dada","fas");
        }
        inicialziarComponentes();//iniciando componentes da pagina de registrar vendas

        checkBoxDesconto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                editDesconto.setVisibility(View.VISIBLE);

            }else{
                editDesconto.setVisibility(View.INVISIBLE);
            }

        });
        edit_produtos_escolhidos.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                verificarProdutosEscolhidos(false);
            }
        });
        editCpf.setOnFocusChangeListener((v,hasFocus)-> {
            if (!hasFocus){
                if (!(editCpf.getText().toString().isEmpty())) {
                    if(!verificarCpf(editCpf.getText().toString())){
                        AlertDialog.Builder confirmar = new AlertDialog.Builder(this);
                        confirmar.setTitle("Atenção!\n");

                        confirmar.setMessage("CPF não cadastrado\nDeseja cadastrar? ");
                        confirmar.setCancelable(false);
                        confirmar.setPositiveButton("Sim", (dialog, which) -> {
                            Intent intent = new Intent(ActivityRegistrarVendas.this, ActivityCadastrarCliente.class);
                            intent.putExtra("cpfNovo",editCpf.getText().toString());
                            startActivity(intent);

                        });
                        confirmar.setNegativeButton("Não", null);
                        confirmar.create().show();
                    }
                }
            }
        });
        checkBoxPrazo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                editPrazo.setVisibility(View.VISIBLE);
            }else{
                editPrazo.setVisibility(View.INVISIBLE);
            }
        });

        editDesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (produtosEsc != null ) {
                   aplicarDesconto(true);
                }else{
                    Log.d("dsds","vaziooo");
                }
            }
        });


        btn_buscar_produtos.setOnClickListener(v -> escolherProdutdos());

        Calendar calendar = Calendar.getInstance();//instaciando o calendario
        final int ano = calendar.get(Calendar.YEAR);//capturando o ano atual
        final int mes = calendar.get(Calendar.MONTH);//capturando o mes atual
        final int dia = calendar.get(Calendar.DAY_OF_MONTH);//capturando o dia atual
        btn_escolher_data.setOnClickListener(v -> escolherData(ano,mes,dia));// botão de data escutando(atividado quando for acionado)

        btn_cadastrar_venda.setOnClickListener(this::iniciarRegistroDeVendas);
    }
    public void inicialziarComponentes(){
        editCpf = findViewById(R.id.edit_cpf_cliente);
        editCpf.addTextChangedListener(MaskEditUtil.mask(editCpf, MaskEditUtil.FORMAT_CPF));

        editData = findViewById(R.id.edit_data_venda);

        editValor.setEnabled(false);

        checkBoxDesconto =  findViewById(R.id.checkBoxDesconto);
        checkBoxPrazo = findViewById(R.id.checkBoxPrazo);

        btn_escolher_data = findViewById(R.id.bt_escolherData);
        btn_cadastrar_venda = findViewById(R.id.btn_cadastrar_venda);
        btn_buscar_produtos = findViewById(R.id.btn_escolherProdutos);


        editDesconto = findViewById(R.id.edit_desconto);

        if(checkBoxSalvar.isCheckBoxDesconto()){
            editValor.setText(String.valueOf(checkBoxSalvar.getValorFinal()));
            checkBoxDesconto.setChecked(true);
            if(checkBoxDesconto.isChecked()){
                editDesconto.setVisibility(View.VISIBLE);
                editDesconto.setText(checkBoxSalvar.getDesconto());
                aplicarDesconto(true);
            }else{
                editDesconto.setVisibility(View.INVISIBLE);
            }
        }else {
            editDesconto.setText("0");
        }

        editPrazo = findViewById(R.id.edit_prazo);

        if (checkBoxSalvar.isCheckBoxPrazo()){
            editPrazo.setText(checkBoxSalvar.getPrazo());
            checkBoxPrazo.setChecked(true);
            if(checkBoxPrazo.isChecked()){
                editPrazo.setVisibility(View.VISIBLE);
                editPrazo.setText(checkBoxSalvar.getPrazo());
            }else{
                editPrazo.setVisibility(View.INVISIBLE);
            }
            checkBoxSalvar.setPrazo(editPrazo.getText().toString());
        }else {
            editPrazo.setText("0");
        }
    }
    public void resetarComponentes(){
        editCpf.setText("");
        edit_produtos_escolhidos.setText("");
        editData.setText("");
        editValor.setText("");
        checkBoxDesconto.setChecked(false);
        checkBoxPrazo.setChecked(false);
        editDesconto.setText("");
        ProdutosEscolhidos escolhidos = new ProdutosEscolhidos();
        escolhidos.reset();
        checkBoxSalvar.reset();
    }
    public void escolherProdutdos(){
        if((edit_produtos_escolhidos.getText().toString()).isEmpty()){
            ProdutosEscolhidos escolhidos = new ProdutosEscolhidos();
            escolhidos.reset();
        }
        Intent intent = new Intent(ActivityRegistrarVendas.this,ActivityEscolherProduto.class);
        startActivity(intent);
    }
    public void iniciarRegistroDeVendas(View v){
        String cpfCliente,valorVenda,dataVenda,prazo,desconto,produtosEscolhidos;
        boolean situacao = false;
        List<Produto> listaDeProdutos;
        cpfCliente = editCpf.getText().toString();
        dataVenda = editData.getText().toString();
        valorVenda = editValor.getText().toString();
        prazo = editPrazo.getText().toString();
        desconto = editDesconto.getText().toString();

        produtosEscolhidos = edit_produtos_escolhidos.getText().toString();
        if(!produtosEscolhidos.isEmpty()){
            listaDeProdutos = listaDeProdutosEscolhidos(produtosEscolhidos);
            ProdutosEscolhidos escolhidos = new ProdutosEscolhidos();
            produtosEscolhidos = escolhidos.getStringDeProdutos(listaDeProdutos);
        }else{
            Toast.makeText(this, "Escolha algum produto", Toast.LENGTH_SHORT).show();
        }
        if (cpfCliente.isEmpty() || dataVenda.isEmpty() || valorVenda.isEmpty() || produtosEscolhidos.isEmpty()){
            Snackbar snackbar;
            snackbar = Snackbar.make(v,mensagem[1],Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
        }else{
            if(verificarCpf(cpfCliente)){
                Venda venda = new Venda(usuarioId,nomeVendedor,cpfCliente,dataVenda,produtosEscolhidos,
                        Integer.parseInt(prazo),valorVendaFinal,Double.parseDouble(desconto),situacao);
                cadastrarVenda(venda);
            }else{
                Toast.makeText(this, "Cpf não cadastrado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void cadastrarVenda(Venda vendaRecebida){
        VendaDAO vendaDAO = new VendaDAO(this);
        long id = vendaDAO.inserir(vendaRecebida);
        Toast.makeText(this, "Venda salva com id "+id, Toast.LENGTH_SHORT).show();
        resetarComponentes();
    }
    private void escolherData(int ano,int mes,int dia){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ActivityRegistrarVendas.this, (view, year, month, day) -> {
                    month = month +1;
                    String date = day +"/"+ month +"/"+ year;
                    editData.setText(date);
                },ano,mes,dia);
        datePickerDialog.show();
    }


    @Override
    protected void onStart() {//recurperando o usuario atual
        super.onStart();
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();// pegando o usuario atual pelo id
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);// coletado do banco de dados o usuario atual
        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            if(documentSnapshot != null){//verifica se há um usuario
                nomeVendedor=documentSnapshot.getString("nome");
            }
        });
    }
   public boolean verificarCpf(String cpfRecebido){
        ClienteDAO clienteDAO = new ClienteDAO(this);
        clienteRecebido = clienteDAO.recuperarClienteCpf(cpfRecebido);
        return clienteRecebido != null;

   }

    public void verificarProdutosEscolhidos( boolean carrinho ){

        ProdutosEscolhidos escolhidos = new ProdutosEscolhidos();
        valorVendaFinal = 0.0;

        if(!carrinho){
            produtosEscolhidos = edit_produtos_escolhidos.getText().toString();
            escolhidos.setProdutos(null);
        }else{
            produtosEscolhidos = produtosEsc;
        }

        checkBoxSalvar.setValorFinal(valorVendaFinal);

        List<Produto> listaDeProdutos = new ArrayList<>();

        if (!produtosEscolhidos.isEmpty()) {
            listaDeProdutos = listaDeProdutosEscolhidos(produtosEscolhidos);
        }

        for(Produto produto:listaDeProdutos){
            if(!carrinho){
                escolhidos.setProdutos(produto.getCodigo());
            }
            if(listaDeProdutos.size()>1){
                valorVendaFinal += produto.getValor();
            }else{
                valorVendaFinal = produto.getValor();
            }
        }
        produtosEsc = escolhidos.recuperarProdutoFinal();
        checkBoxSalvar.setValorFinal(valorVendaFinal);
        aplicarDesconto(false);
       // editValor.setText(String.valueOf(valorVendaFinal));

    }
    public List<Produto> listaDeProdutosEscolhidos(String produtos){

        produtos = produtos.concat(",");
        ProdutoDAO dao = new ProdutoDAO(this);
        List<String> lista = new ArrayList<>();
        List<Produto> listaProduto = new ArrayList<>();

        String nome = "" ;
        for (int i = 0; i < produtos.length(); i++){
            if(produtos.charAt(i) == ','){
                lista.add(nome);
                nome = "";
            }else{
                if(i!=0){
                    nome = nome.concat(String.valueOf(produtos.charAt(i)));
                }else{
                    nome = String.valueOf(produtos.charAt(i));
                }
            }
        }
        for (int i = 0; i < lista.size(); i++){
            Produto produto;
            produto = dao.recuperaProdutoPorCodigo(lista.get(i));
            if(produto!=null){
                listaProduto.add(produto);
            }else{
                Toast.makeText(this, "Nenhum produto encontrado para o códgio "+lista.get(i), Toast.LENGTH_SHORT).show();
            }
        }
        return listaProduto;
    }
    public void aplicarDesconto(boolean checkDesconto){
        double valor = valorVendaFinal;
        if(checkBoxSalvar.isCheckBoxDesconto() || checkDesconto){
            editValor.setText(String.valueOf(checkBoxSalvar.getValorFinal()));
            String desc = editDesconto.getText().toString();
            try {
                if(!desc.isEmpty()){
                    desconto = Double.parseDouble(desc);
                    valorVendaFinal = Double.parseDouble(editValor.getText().toString());
                    editValor.setText(String.valueOf(valor-desconto));
                    checkBoxSalvar.setDesconto(desc);
                    checkBoxSalvar.setValorFinal(valorVendaFinal);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            editValor.setText(String.valueOf(valor));
        }
    }
}