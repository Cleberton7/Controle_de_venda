package com.example.vendas.clientes;

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

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AdpterCliente extends RecyclerView.Adapter<AdpterCliente.MyViewHolder>{
    private List<Cliente> listaCliente;
    private List<Integer> ids;
    public AdpterCliente(List<Cliente> listaids){
        this.listaCliente = listaids;
    }
    public void setIds(List<Integer> ids){
        this.ids = ids;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapterlistarcliente,parent,false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cliente cliente = listaCliente.get(position);

        holder.nomeCliente.setText(cliente.getNome());
        holder.cpf.setText(cliente.getCpf());
        holder.telefone.setText(cliente.getTelefone());
        holder.rua.setText(cliente.getEndereco().getRua());
        holder.bairro.setText(cliente.getEndereco().getBairro());
        holder.numero.setText(cliente.getEndereco().getNumero());

        holder.layout.setOnClickListener(v -> Log.d("Clique","Clique curto funcionou"));
        holder.layout.setOnLongClickListener(v -> {
            setIdCliente(ids.get(position));
            setposicao(holder.getAdapterPosition());
            setCliente(cliente);
            return false;

        });
       if(!cliente.getImagem().equals("")){
            byte[] img;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                img = Base64.getDecoder().decode(cliente.getImagem());
                Bitmap imagemDecodificada = BitmapFactory.decodeByteArray(img,0,img.length);
                holder.imagemCliente.setImageBitmap(imagemDecodificada);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaCliente.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView nomeCliente,cpf,telefone,rua,bairro,numero;
        ImageView imagemCliente;
        View layout;
        public MyViewHolder( View itemView) {
            super(itemView);

            iniciarComponentes();
            itemView.setOnCreateContextMenuListener(this);
        }
        public void iniciarComponentes(){
            nomeCliente = itemView.findViewById(R.id.textViewNomeCliente);
            cpf = itemView.findViewById(R.id.textViewCpf);
            telefone = itemView.findViewById(R.id.textViewTelefone);
            rua = itemView.findViewById(R.id.textViewRua);
            bairro = itemView.findViewById(R.id.textViewBairro);
            numero = itemView.findViewById(R.id.textViewNumero);

            imagemCliente = itemView.findViewById(R.id.imageViewCliente);

            layout = itemView.findViewById(R.id.btn_opcoes_cliente);


        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE,R.id.deletar,Menu.NONE,"Deletar");
            menu.add(Menu.NONE,R.id.atualizar,Menu.NONE,"Atualizar");
        }
    }
    private int posicao;
    public int getPosicao(){
        return posicao;
    }
    public void setposicao(int posicao){
        this.posicao = posicao;
    }
    private Cliente cliente;

    public Cliente getCliente(){
        return cliente;
    }

    public void setCliente(Cliente cliente){
        this.cliente = cliente;
    }
    private int idCliente;
    public int getIdCliente(){
        return idCliente;
    }
    public void setIdCliente(int idCliente){
        this.idCliente = idCliente;
    }
    public void setFiltro(ArrayList<Cliente> lista){
        listaCliente = new ArrayList<>();
        listaCliente.addAll(lista);
        notifyDataSetChanged();
    }
    public void setFiltroids(ArrayList<Integer> lista){
        ids = new ArrayList<>();
        ids.addAll(lista);
        notifyDataSetChanged();
    }

}
