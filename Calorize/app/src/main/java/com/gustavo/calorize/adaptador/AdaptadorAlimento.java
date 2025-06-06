package com.gustavo.calorize.adaptador;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.gustavo.calorize.R;
import com.gustavo.calorize.modelo.Alimento;

import java.util.List;

public class AdaptadorAlimento extends RecyclerView.Adapter<AdaptadorAlimento.AlimentoViewHolder> {

    private final List<Alimento> listaAlimentos;
    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;

    // Interface para clique longo
    public interface OnItemLongClickListener {
        void onItemLongClick(Alimento alimento);
    }

    // Construtor para receber os listeners
    public AdaptadorAlimento(List<Alimento> listaAlimentos, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        this.listaAlimentos = listaAlimentos; // A lista será atualizada por DiffUtil
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        Log.d("AdaptadorAlimento", "AdaptadorAlimento instanciado com " + listaAlimentos.size() + " alimentos.");
    }

    @NonNull
    @Override
    public AlimentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("AdaptadorAlimento", "onCreateViewHolder: Inflating item_alimento_botao.xml");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alimento_botao, parent, false);
        return new AlimentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlimentoViewHolder holder, int position) {
        Alimento alimento = listaAlimentos.get(position);
        Log.d("AdaptadorAlimento", "onBindViewHolder: Binding alimento: " + alimento.getNome() + " at position " + position);

        holder.btnAlimento.setText(alimento.getNome());

        // Listener de clique normal
        holder.btnAlimento.setOnClickListener(v -> {
            Log.d("AdaptadorAlimento", "Botão clicado (curto) para: " + alimento.getNome());
            if (clickListener != null) {
                clickListener.onItemClick(alimento);
                Log.d("AdaptadorAlimento", "onItemClick callback disparado para: " + alimento.getNome());
            } else {
                Log.w("AdaptadorAlimento", "ClickListener é NULL ao clicar no botão para: " + alimento.getNome());
            }
        });

        // Listener de clique longo
        holder.btnAlimento.setOnLongClickListener(v -> {
            Log.d("AdaptadorAlimento", "Botão clicado (longo) para: " + alimento.getNome());
            if (longClickListener != null) {
                longClickListener.onItemLongClick(alimento);
                Log.d("AdaptadorAlimento", "onItemLongClick callback disparado para: " + alimento.getNome());
                return true; // impede o clique normal
            } else {
                Log.w("AdaptadorAlimento", "LongClickListener é NULL ao clicar longamente no botão para: " + alimento.getNome());
                return false; // permite que o clique normal seja disparado se não houver longClickListener
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaAlimentos.size();
    }

    //  Usando DiffUtil
    public void atualizarDados(List<Alimento> novosAlimentos) {
        // Cria um Callback para DiffUtil que compara as duas listas
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AlimentoDiffCallback(this.listaAlimentos, novosAlimentos));

        // Atualiza a lista interna do adaptador
        this.listaAlimentos.clear();
        this.listaAlimentos.addAll(novosAlimentos);

        // Dispara as notificações de mudança específicas para o RecyclerView
        diffResult.dispatchUpdatesTo(this);
        Log.d("AdaptadorAlimento", "Dados atualizados via DiffUtil. Total de alimentos: " + listaAlimentos.size());
    }

    // Interface para a resposta do clique normal
    public interface OnItemClickListener {
        void onItemClick(Alimento alimento);
    }


    public static class AlimentoViewHolder extends RecyclerView.ViewHolder {
        Button btnAlimento;

        AlimentoViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAlimento = itemView.findViewById(R.id.btn_alimento_item);
            Log.d("AlimentoViewHolder", "ViewHolder inicializado. btnAlimento is null? " + (btnAlimento == null));
        }
    }

    // CLASSE AUXILIAR PARA O DIFFUTIL
    private static class AlimentoDiffCallback extends DiffUtil.Callback {

        private final List<Alimento> oldList;
        private final List<Alimento> newList;

        public AlimentoDiffCallback(List<Alimento> oldList, List<Alimento> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        // Verifica se os itens são os mesmos pelo ID
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        // Verifica se o conteúdo dos itens são os mesmos
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Alimento oldAlimento = oldList.get(oldItemPosition);
            Alimento newAlimento = newList.get(newItemPosition);

            // Comparar todos os campos relevantes para saber se o item mudou
            // Se algum campo importante para a exibição mudou, retorne false
            // Caso contrário, retorne true
            return oldAlimento.getNome().equals(newAlimento.getNome()) &&
                    oldAlimento.getCalorias() == newAlimento.getCalorias() &&
                    oldAlimento.getProteinas() == newAlimento.getProteinas() &&
                    oldAlimento.getCarboidratos() == newAlimento.getCarboidratos() &&
                    oldAlimento.getGorduras() == newAlimento.getGorduras() &&
                    oldAlimento.isEhPersonalizado() == newAlimento.isEhPersonalizado() &&
                    // Comparar idUsuario
                    // Use Objects.equals para comparar
                    java.util.Objects.equals(oldAlimento.getIdUsuario(), newAlimento.getIdUsuario());
        }

    }
}