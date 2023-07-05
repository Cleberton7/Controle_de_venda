package com.example.vendas.classes;

import java.io.Serializable;

public class CheckBoxSalvar implements Serializable {

    private static boolean checkBoxDesconto ;
    private static boolean checkBoxPrazo ;
    private static String desconto = "0";
    private static String prazo = "0" ;
    private static double valorFinal = 0.0;

    public CheckBoxSalvar(){};

    public boolean isCheckBoxDesconto() {
        return checkBoxDesconto;
    }

//    public void setCheckBoxDesconto(boolean checkBoxDesconto) {
//        CheckBoxSalvar.checkBoxDesconto = checkBoxDesconto;
//    }

    public boolean isCheckBoxPrazo() {
        return checkBoxPrazo;
    }

  /*  public void setCheckBoxPrazo(boolean checkBoxPrazo) {
        CheckBoxSalvar.checkBoxPrazo = checkBoxPrazo;
    }*/

    public String getDesconto() {
        return desconto;
    }

    public void setDesconto(String desconto) {
        CheckBoxSalvar.desconto = desconto;
        CheckBoxSalvar.checkBoxDesconto = true;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        CheckBoxSalvar.prazo = prazo;
        CheckBoxSalvar.checkBoxPrazo = true;
    }

    public double getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(double valorFinal) {
        CheckBoxSalvar.valorFinal = valorFinal;
    }
    public void reset(){
        CheckBoxSalvar.checkBoxDesconto = false;
        CheckBoxSalvar.checkBoxPrazo = false;
        CheckBoxSalvar.desconto = "0";
        CheckBoxSalvar.prazo = "0";
    }
}
