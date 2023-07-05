package com.example.vendas.classes;

import com.example.vendas.produtos.Produto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProdutosEscolhidos implements Serializable {
    private static final List<String> listaDeprodutos = new ArrayList<>();
    private static String produtos = "";

    public ProdutosEscolhidos(){}

    public List<String> getProdutos() {
        return listaDeprodutos;
    }

    public void setProdutos(String produtos) {
        if(produtos!=null){
            ProdutosEscolhidos.listaDeprodutos.add(produtos);
        }
    }
    public String recuperarProdutoFinal(){
        if(listaDeprodutos.size()==1){
            produtos = listaDeprodutos.get(0);
        }else{
            if(listaDeprodutos.size() > 0 ){
                for (int i = 0; i < listaDeprodutos.size(); i++){
                    if (i == 0) {
                        produtos = listaDeprodutos.get(i);
                        produtos = produtos.concat(",");
                    }else{
                        if(i == listaDeprodutos.size()-1){
                            produtos = produtos.concat(listaDeprodutos.get(i));
                        }else{
                            produtos = listaDeprodutos.get(i);
                            produtos = produtos.concat(",");
                        }
                    }
                }
            }
        }

        return produtos;
    }
   public int getSizeLista(){
        return listaDeprodutos.size();
   }

   public String getStringDeProdutos(List<Produto> listaDeProdutos){
        String listaProdutos = "";
        for(int i = 0; i < listaDeProdutos.size(); i++) {
           if(i==0) {
               if(i==listaDeProdutos.size()-1) {
                   listaProdutos = listaProdutos.concat(listaDeProdutos.get(i).getCodigo());
               }else {
                   listaProdutos = listaProdutos.concat(listaDeProdutos.get(i).getCodigo());
                   listaProdutos = listaProdutos.concat(",");
               }
           }else{
               listaProdutos = listaProdutos.concat(listaDeProdutos.get(i).getCodigo());
               listaProdutos = listaProdutos.concat(",");
           }
        }
        return listaProdutos;
   }
   public void reset(){
        ProdutosEscolhidos.produtos = "";
        ProdutosEscolhidos.listaDeprodutos.clear();
   }
}
