package com.example.vendas.vendas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.vendas.classes.Conexao;

import java.util.ArrayList;
import java.util.List;

public class VendaDAO {
    private final Conexao conexao;
    private SQLiteDatabase banco;
    private Cursor cursor;

    public VendaDAO(Context context){
        conexao = new Conexao(context);
        banco = conexao.getWritableDatabase();
    }
    public long inserir(Venda venda) {
        if (venda == null) throw new IllegalArgumentException("Produto não pode ser nulo");
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("idVendedor", venda.getIdVendedor());
            contentValues.put("nomeVendedor", venda.getNomeVendedor());
            contentValues.put("cpfCliente", venda.getCpfCliente());
            contentValues.put("valor", venda.getValorTotal());
            contentValues.put("dataVenda", venda.getDataVenda());
            contentValues.put("prazoVencimento", venda.getPrazoParaPagar());
            contentValues.put("desconto", venda.getDesconto());
            contentValues.put("nProdutos", venda.getnProdutos());
            contentValues.put("situacao", venda.isSituacaoDaVenda());
            contentValues.put("av", 0.0);

            long resultado = banco.insert("vendas", null, contentValues);

            if (resultado == -1)
                throw new RuntimeException("Erro ao inserir venda no banco de dados");

            return resultado;
        }finally {
            fechar();
        }
    }

    public long atualizar(Venda venda, int cod) {
        if (venda == null) throw new IllegalArgumentException("Venda não pode ser nulo");

        if (cod <= 0) throw new IllegalArgumentException("Código inválido");
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("idVendedor", venda.getIdVendedor());
            contentValues.put("nomeVendedor", venda.getNomeVendedor());
            contentValues.put("cpfCliente", venda.getCpfCliente());
            contentValues.put("valor", venda.getValorTotal());
            contentValues.put("dataVenda", venda.getDataVenda());
            contentValues.put("prazoVencimento", venda.getPrazoParaPagar());
            contentValues.put("desconto", venda.getDesconto());
            contentValues.put("nProdutos", venda.getnProdutos());
            contentValues.put("situacao", venda.isSituacaoDaVenda());
            contentValues.put("av", 0.0);

            long resultado = banco.update("vendas", contentValues, "id = ?", new String[]{String.valueOf(cod)});

            if (resultado == -1) throw new RuntimeException("Erro ao atualizar venda");

            return resultado;
        }finally {
            fechar();
        }
    }
    public void remover(int cod) {
        try {
            banco.beginTransaction();
            banco.delete("vendas", "id = ?", new String[] { String.valueOf(cod) });
            banco.setTransactionSuccessful();

            Log.d("deletado", "Venda de id " + cod);
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        } finally {
            fechar();
        }
    }

    public List<Venda> listarVendas() {
        String idVendendor = "",nomeVendedor = "",cpfCliente = "",dataVenda = "",nProdutos=",";
        double valorVenda = 0,desconto = 0;
        int prazo = 0;
        boolean situacao = false;
        List<Venda> listaVendas = new ArrayList<>();
        try {
            cursor = banco.query("vendas",
                    new String[]{"id", "idVendedor", "nomeVendedor", "cpfCliente", "valor", "dataVenda", "prazoVencimento", "desconto", "situacao", "nProdutos"},
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex("idVendedor");
                if (columnIndex >= 0) {
                    idVendendor = cursor.getString(columnIndex);
                }
                columnIndex = cursor.getColumnIndex("nomeVendedor");
                if (columnIndex >= 0) {
                    nomeVendedor = cursor.getString(columnIndex);
                }
                columnIndex = cursor.getColumnIndex("cpfCliente");
                if (columnIndex >= 0) {
                    cpfCliente = cursor.getString(columnIndex);
                }
                columnIndex = cursor.getColumnIndex("valor");
                if (columnIndex >= 0) {
                    valorVenda = cursor.getDouble(columnIndex);
                }
                columnIndex = cursor.getColumnIndex("dataVenda");
                if (columnIndex >= 0) {
                    dataVenda = cursor.getString(columnIndex);
                }
                columnIndex = cursor.getColumnIndex("prazoVencimento");
                if (columnIndex >= 0) {
                    prazo = cursor.getInt(columnIndex);
                }
                columnIndex = cursor.getColumnIndex("desconto");
                if (columnIndex >= 0) {
                    desconto = cursor.getDouble(columnIndex);
                }
                columnIndex = cursor.getColumnIndex("situacao");
                if (columnIndex >= 0) {
                    situacao = cursor.getInt(columnIndex) == 1;
                }
                columnIndex = cursor.getColumnIndex("nProdutos");
                if (columnIndex >= 0) {
                    nProdutos = cursor.getString(columnIndex);
                }
                Venda venda = new Venda(idVendendor, nomeVendedor, cpfCliente, dataVenda, nProdutos, prazo, valorVenda, desconto, situacao);
                listaVendas.add(venda);
            }
            return listaVendas;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            fechar();
        }
        return null;
    }
    public List<Integer> recuperarId() {
        List<Integer> ids = new ArrayList<>();
        try {
            banco = conexao.getReadableDatabase();
            cursor = banco.query("vendas",
                     new String[]{"id","idVendedor", "nomeVendedor", "cpfCliente", "valor", "dataVenda", "prazoVencimento", "desconto", "situacao", "nProdutos"},
                     null, null, null, null, null);

            int indexId = cursor.getColumnIndex("id");
            while (cursor.moveToNext()) {
                ids.add(cursor.getInt(indexId));
            }
            return ids;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            fechar();
        }
       return null;
    }

    public Venda recuperaProduto(int cod) {
        try {
            String idVendendor = "",nomeVendedor = "",cpfCliente = "",dataVenda = "",nProdutos=",";
            double valorVenda = 0,desconto = 0;
            int prazo = 0;
            boolean situacao = false;
            Venda venda = null;
            banco = conexao.getReadableDatabase();
            cursor = banco.rawQuery("SELECT * FROM vendas WHERE id = ?", new String[]{String.valueOf(cod)});

            if (cursor.moveToLast()) {
                int columnIndex = cursor.getColumnIndex("idVendedor");
                if (columnIndex >= 0) idVendendor = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("nomeVendedor");
                if (columnIndex >= 0) nomeVendedor = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("cpfCliente");
                if (columnIndex >= 0) cpfCliente = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("valor");
                if (columnIndex >= 0) valorVenda = cursor.getDouble(columnIndex);

                columnIndex = cursor.getColumnIndex("dataVenda");
                if (columnIndex >= 0) dataVenda = cursor.getString(columnIndex);

                columnIndex = cursor.getColumnIndex("prazoVencimento");
                if (columnIndex >= 0) prazo = cursor.getInt(columnIndex);

                columnIndex = cursor.getColumnIndex("desconto");
                if (columnIndex >= 0) desconto = cursor.getInt(columnIndex);

                columnIndex = cursor.getColumnIndex("situacao");
                if (columnIndex >= 0) situacao = cursor.getInt(columnIndex)==1;

                columnIndex = cursor.getColumnIndex("nProdutos");
                if (columnIndex >= 0) nProdutos = cursor.getString(columnIndex);

                venda = new Venda(idVendendor, nomeVendedor,cpfCliente,dataVenda,nProdutos,prazo,valorVenda,desconto,situacao);
            }
            return venda;
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
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

