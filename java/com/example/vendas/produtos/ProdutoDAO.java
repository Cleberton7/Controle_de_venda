package com.example.vendas.produtos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.vendas.classes.Conexao;

import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private final Conexao conexao;
    private SQLiteDatabase banco;
    private Cursor cursor;

    public ProdutoDAO(Context context){
        conexao = new Conexao(context);
        banco = conexao.getWritableDatabase();
    }
    public long inserir(Produto produto) {
        banco = conexao.getWritableDatabase();

        try {
            if (produto == null) throw new IllegalArgumentException("Produto não pode ser nulo");

            ContentValues contentValues = new ContentValues();
            contentValues.put("codigoProduto", produto.getCodigo());
            contentValues.put("nome", produto.getNome());
            contentValues.put("quantidade", produto.getQuantidade());
            contentValues.put("valor", produto.getValor());
            contentValues.put("categoria", produto.getCategoria());
            contentValues.put("tamanho", produto.getTamanho());
            contentValues.put("imagem", produto.getImagem());

            long resultado = banco.insert("produtos", null, contentValues);

            if (resultado == -1) throw new RuntimeException("Erro ao inserir produto no banco de dados");

            return resultado;
        } finally {
            fechar();
        }
    }

    public long atualizar(Produto produto, int cod) {
        banco = conexao.getWritableDatabase();
        try {

            if (produto == null) throw new IllegalArgumentException("Produto não pode ser nulo");

            if (cod <= 0) throw new IllegalArgumentException("Código inválido");


            ContentValues contentValues = new ContentValues();
            contentValues.put("codigoProduto", produto.getCodigo());
            contentValues.put("nome", produto.getNome());
            contentValues.put("quantidade", produto.getQuantidade());
            contentValues.put("valor", produto.getValor());
            contentValues.put("categoria", produto.getCategoria());
            contentValues.put("tamanho", produto.getTamanho());
            contentValues.put("imagem", produto.getImagem());

            String whereClause = "id = ?";
            String[] whereArgs = {String.valueOf(cod)};

            long resultado = banco.update("produtos", contentValues, whereClause, whereArgs);

            if (resultado == 0) throw new RuntimeException("Produto não encontrado");

            return resultado;
        }finally {
           fechar();
        }
    }
    public void remover(int cod) {
        try {
            banco = conexao.getWritableDatabase();
            banco.beginTransaction();
            banco.delete("produtos", "id = ?", new String[] { String.valueOf(cod) });
            banco.setTransactionSuccessful();
            Log.d("deletado", "Produto de id " + cod);
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        } finally {
            fechar();
        }
    }

    public List<Produto> listarTodos() {
        String codigo = "",nome="",categoria="",tamanho="",imagem="";
        double valor=0;
        int quantidade=0;
        List<Produto> listaDeProdutos = new ArrayList<>();
        try {
            banco = conexao.getReadableDatabase();
            cursor = banco.query("produtos",
                new String[]{"codigoProduto", "nome", "valor", "quantidade", "categoria", "tamanho", "imagem"},
                null, null, null, null, null);

            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex("codigoProduto");
                if (columnIndex >= 0) codigo = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("nome");
                if (columnIndex >= 0) nome = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("valor");
                if (columnIndex >= 0) valor = cursor.getDouble(columnIndex);

                columnIndex = cursor.getColumnIndex("quantidade");
                if (columnIndex >= 0) quantidade = cursor.getInt(columnIndex);

                columnIndex = cursor.getColumnIndex("categoria");
                if (columnIndex >= 0) categoria = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("tamanho");
                if (columnIndex >= 0) tamanho = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("imagem");
                if (columnIndex >= 0) imagem = cursor.getString(columnIndex);

                Produto produto = new Produto(codigo, nome, categoria, tamanho, imagem, valor, quantidade);
                listaDeProdutos.add(produto);
            }
            return listaDeProdutos;

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            fechar();
        }
        return  listaDeProdutos;
    }

    public List<Integer> recuperarId() {
        List<Integer> ids = new ArrayList<>();
        try {
                banco = conexao.getReadableDatabase();
                cursor = banco.query("produtos",
                     new String[]{"id", "codigoProduto", "nome", "valor", "quantidade", "categoria", "tamanho", "imagem"},
                     null, null, null, null, null);

                int indexId = cursor.getColumnIndex("id");
                while (cursor.moveToNext()) {
                    ids.add(cursor.getInt(indexId));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            fechar();
        }
        return ids;
    }

  public Produto recuperaProduto(int cod) {
      try {
          String codigo ="", nome = "", categoria = "", tamanho = "", imagem = "";
          int quantidade = 0;
          double valor = 0;
          Produto produto = null;
          banco = conexao.getReadableDatabase();
          cursor = banco.rawQuery("SELECT * FROM produtos WHERE id = ?", new String[]{String.valueOf(cod)});

          if (cursor.moveToLast()) {
              int columnIndex = cursor.getColumnIndex("codigoProduto");
              if (columnIndex >= 0) codigo = cursor.getString(columnIndex);

              columnIndex = cursor.getColumnIndex("nome");
              if (columnIndex >= 0) nome = cursor.getString(columnIndex);

              columnIndex = cursor.getColumnIndex("valor");
              if (columnIndex >= 0) valor = cursor.getDouble(columnIndex);

              columnIndex = cursor.getColumnIndex("quantidade");
              if (columnIndex >= 0)quantidade = cursor.getInt(columnIndex);

              columnIndex = cursor.getColumnIndex("categoria");
              if (columnIndex >= 0) categoria = cursor.getString(columnIndex);

              columnIndex = cursor.getColumnIndex("tamanho");

              if (columnIndex >= 0) tamanho = cursor.getString(columnIndex);

              columnIndex = cursor.getColumnIndex("imagem");
              if (columnIndex >= 0) imagem = cursor.getString(columnIndex);

              produto = new Produto(codigo, nome, categoria, tamanho, imagem, valor, quantidade);
          }
          return produto;
      } finally {
          fechar();
      }
  }

    public int recuperarIDFiltro(String nome) {
        int id = 0;
        banco = conexao.getReadableDatabase();
        cursor = banco.rawQuery("SELECT * FROM produtos WHERE nome = ?", new String[]{nome});

        try {
            if (cursor.moveToLast()){
                int indexId = cursor.getColumnIndex("id");
                if (indexId >= 0) id = cursor.getInt(indexId);
            }
        } finally {
            fechar();
        }
        return id;
    }

    public Produto recuperaProdutoPorCodigo(String cod){
        try {
            String codigo = "",nome = "",categoria = "",tamanho = "",imagem = "";
            int quantidade = 0;
            double valor = 0;
            Produto produto = null;
            banco = conexao.getReadableDatabase();
            cursor = banco.rawQuery("SELECT codigoProduto, nome, valor, quantidade, categoria, tamanho, imagem FROM produtos WHERE codigoProduto = ?", new String[]{cod});

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("codigoProduto");
                if (columnIndex >= 0) codigo = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("nome");
                if (columnIndex >= 0) nome = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("valor");
                if (columnIndex >= 0) valor = cursor.getDouble(columnIndex);

                columnIndex = cursor.getColumnIndex("quantidade");
                if (columnIndex >= 0) quantidade = cursor.getInt(columnIndex);

                columnIndex = cursor.getColumnIndex("categoria");
                if (columnIndex >= 0) categoria = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("tamanho");
                if (columnIndex >= 0) tamanho = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("imagem");
                if (columnIndex >= 0) imagem = cursor.getString(columnIndex);

                produto = new Produto(codigo,nome,categoria,tamanho,imagem,valor,quantidade );
            }
            return produto;
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            fechar();
        }
        return null;
    }
    public void fechar(){
        if (cursor != null && !cursor.isClosed()) cursor.close();
    }
    public void fecharBanco(){
        if (banco!=null) banco.close();
    }
}
