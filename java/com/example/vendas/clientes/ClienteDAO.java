package com.example.vendas.clientes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.vendas.classes.Conexao;

import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private final Conexao conexao;
    private SQLiteDatabase banco;
    private Cursor cursor;

    public ClienteDAO(Context context){
        conexao = new Conexao(context);
        banco = conexao.getWritableDatabase();
    }
    public long inserir(Cliente cliente){
        try {
            if (cliente == null) throw new IllegalArgumentException("Cliente não pode ser nulo");
            long idEndereco;
            ContentValues contentValues = new ContentValues();
            contentValues.put("rua",cliente.getEndereco().getRua());
            contentValues.put("bairro",cliente.getEndereco().getBairro());
            contentValues.put("numero",cliente.getEndereco().getNumero());

            idEndereco = (int)banco.insert("endereco",null,contentValues);
            contentValues.clear();

            contentValues.put("nome",cliente.getNome());
            contentValues.put("cpf",cliente.getCpf());
            contentValues.put("telefone",cliente.getTelefone());
            contentValues.put("imagem",cliente.getImagem());
            contentValues.put("endereco",idEndereco);

            long resultado = banco.insert("clientes",null,contentValues);
            if (resultado == -1) throw new RuntimeException("Erro ao inserir cliente no banco de dados");
            return  resultado;
        } finally {
            fechar();
        }
    }

    public long atualizar(Cliente cliente, int cod){
        try {
            if (cliente == null) throw new IllegalArgumentException("Cliente não pode ser nulo");

            if (cod <= 0) throw new IllegalArgumentException("Código inválido");

            int idEndereco = recuperarEndereco(cod);
            ContentValues contentValues = new ContentValues();
            contentValues.put("rua",cliente.getEndereco().getRua());
            contentValues.put("bairro",cliente.getEndereco().getBairro());
            contentValues.put("numero",cliente.getEndereco().getNumero());
            banco.update("endereco",contentValues,"id=?", new String[]{String.valueOf(idEndereco)});
            contentValues.clear();

            contentValues.put("nome",cliente.getNome());
            contentValues.put("cpf",cliente.getCpf());
            contentValues.put("telefone",cliente.getTelefone());
            contentValues.put("imagem",cliente.getImagem());
            contentValues.put("endereco",idEndereco);

            long resultado = banco.update("clientes",contentValues,"id=?",new String[]{String.valueOf(cod)});
            if (resultado == 0) throw new RuntimeException("Cliente não encontrado");

            return  resultado;
        } finally {
            fechar();
        }
    }
    public void remover(int cod) {
        try {
            banco = conexao.getWritableDatabase();
            banco.beginTransaction();

            // Obtém o ID do endereço do cliente
            int idEndereco = recuperarEndereco(cod);

            // Exclui o endereço
            int linhasExcluidas = banco.delete("endereco", "id = ?", new String[]{String.valueOf(idEndereco)});
            if (linhasExcluidas != 1) throw new RuntimeException("Erro ao excluir endereço do cliente");

            // Exclui o cliente
            linhasExcluidas = banco.delete("clientes", "id = ?", new String[]{String.valueOf(cod)});

            if (linhasExcluidas != 1) throw new RuntimeException("Erro ao excluir cliente");

            // Finaliza a transação
            banco.setTransactionSuccessful();

            Log.d("deletado", "Cliente de id " + cod);

        } catch (Exception e) {
            Log.e("ERROR", e.toString());

        } finally {
            fechar();
        }
    }

    public List<Cliente> listarTodos(){
        String nomeCliente = "",cpf = "", telefone = "", imagem = "", rua = "", bairro = "", numero = "";
        List<Cliente> listaDeCliente = new ArrayList<>();

        try {
            banco = conexao.getWritableDatabase();
            cursor = banco.rawQuery("SELECT clientes.id, nome, cpf, telefone, imagem, endereco.rua, endereco.bairro, endereco.numero " +
                    "FROM clientes " +
                    "JOIN endereco ON clientes.endereco = endereco.id", null);

            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex("nome");
                if (columnIndex >= 0) nomeCliente = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("cpf");
                if (columnIndex >= 0) cpf = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("telefone");
                if (columnIndex >= 0) telefone = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("imagem");
                if (columnIndex >= 0) imagem = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("rua");
                if (columnIndex >= 0) rua = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("bairro");
                if (columnIndex >= 0) bairro = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("numero");
                if (columnIndex >= 0) numero = cursor.getString(columnIndex);

                Endereco endereco = new Endereco(rua, bairro, numero);
                Cliente cliente = new Cliente(nomeCliente, cpf, telefone, imagem, endereco);
                listaDeCliente.add(cliente);
            }
            return listaDeCliente;
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            fechar();
        }
        return  null;
    }

    public List<Integer> recuperarId() {
        List<Integer> ids = new ArrayList<>();
        try {
            banco = conexao.getReadableDatabase();
            cursor = banco.query("clientes",
                    new String[]{"id","nome","cpf","telefone","imagem","endereco"},
                    null,null,null,null,null);
            int indexId = cursor.getColumnIndex("id");
            while (cursor.moveToNext()){
                ids.add(cursor.getInt(indexId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fechar();

        }
        return ids;
    }


    public int recuperarEndereco(int cod){
        try {
            int idEndereco = -1;

            banco = conexao.getReadableDatabase();
            cursor = banco.rawQuery("SELECT * FROM clientes WHERE id = ?", new String[] {String.valueOf(cod)});
            int indexIdEndereco = cursor.getColumnIndex("endereco");

            if(cursor.moveToFirst()) idEndereco = cursor.getInt(indexIdEndereco);

            return idEndereco;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            fechar();
        }
        return -1;
    }



    public int recuperarIDFiltro(String cpf) {
        int id = -1;
        try {
            banco = conexao.getReadableDatabase();
            cursor = banco.rawQuery("SELECT id FROM clientes WHERE nome = ? LIMIT 1", new String[] {cpf});

            int indexId = cursor.getColumnIndex("id");
            if (cursor.moveToFirst()) id = cursor.getInt(indexId);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            fechar();
        }
        return id;
    }

    public Cliente recuperarCliente(int cod) {

        Cursor cursor2 = null;
        try {
            banco = conexao.getReadableDatabase();
            String nomeCliente = "", cpf = "", telefone = "", rua = "", bairro = "", numero = "", imagem = "";
            int idEndereco = recuperarEndereco(cod);
            Cliente cliente = null;

            cursor = banco.rawQuery("SELECT * FROM clientes WHERE id = ?", new String[]{String.valueOf(cod)});
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("nome");
                if (columnIndex >= 0) nomeCliente = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("cpf");
                if (columnIndex >= 0) cpf = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("telefone");
                if (columnIndex >= 0) telefone = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("imagem");
                if (columnIndex >= 0) imagem = cursor.getString(columnIndex);

                Endereco endereco = null;

                cursor2 = banco.rawQuery("SELECT * FROM endereco WHERE id = ?", new String[]{String.valueOf(idEndereco)});
                if (cursor2.moveToFirst()) {
                    columnIndex = cursor2.getColumnIndex("rua");
                    if (columnIndex >= 0) rua = cursor2.getString(columnIndex);

                    columnIndex = cursor2.getColumnIndex("bairro");
                    if (columnIndex >= 0) bairro = cursor2.getString(columnIndex);

                    columnIndex = cursor2.getColumnIndex("numero");
                    if (columnIndex >= 0) numero = cursor2.getString(columnIndex);

                    endereco = new Endereco(rua, bairro, numero);
                }
                cliente = new Cliente(nomeCliente, cpf, telefone, imagem, endereco);
            }
            return cliente;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fechar();
            if (cursor2 != null) cursor2.close();
        }
        return null;
    }

    public Cliente recuperarClienteCpf(String cpfRecebido) {
        try {
            String nomeCliente = "", cpf = "", telefone ="", rua ="", bairro = "", numero ="", imagem = "";

            banco = conexao.getReadableDatabase();

            cursor = banco.rawQuery("SELECT clientes.*, endereco.rua, endereco.bairro, endereco.numero FROM clientes INNER JOIN endereco ON clientes.endereco = endereco.id WHERE clientes.cpf = ? LIMIT 1", new String[]{cpfRecebido});

            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("nome");
                if (columnIndex >= 0) nomeCliente = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("cpf");
                if (columnIndex >= 0) cpf = cursor.getString((columnIndex));

                columnIndex = cursor.getColumnIndex("telefone");
                if (columnIndex >= 0) telefone = cursor.getString((columnIndex));

                columnIndex = cursor.getColumnIndex("imagem");
                if (columnIndex >= 0) imagem = cursor.getString((columnIndex));

                columnIndex = cursor.getColumnIndex("rua");
                if (columnIndex >= 0) rua = cursor.getString((columnIndex));

                columnIndex = cursor.getColumnIndex("bairro");
                if (columnIndex >= 0) bairro = cursor.getString((columnIndex));

                columnIndex = cursor.getColumnIndex("numero");
                if (columnIndex >= 0) numero = cursor.getString((columnIndex));

                Endereco endereco = new Endereco(rua, bairro, numero);

                return new Cliente(nomeCliente, cpf, telefone, imagem, endereco);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            fechar();
        }
        return null;
    }

    public void fechar() {
        if (cursor != null && !cursor.isClosed()) cursor.close();
    }
    public void fecharBanco(){
        if (banco!=null) banco.close();
    }
}

