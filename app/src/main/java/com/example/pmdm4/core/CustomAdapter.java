package com.example.pmdm4.core;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmdm4.DDBB.PostsLab;
import com.example.pmdm4.R;
import com.example.pmdm4.UI.MainActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> implements Filterable {
    private ItemClickListener clickListener;
    private Context context;
    private ArrayList<Post> listaPosts;
    private ArrayList<User> listaUsers;
    private PostsLab myLab;
    ArrayList<Post> listaPostAll;
    private final LayoutInflater myInflater;
    ;

    public CustomAdapter(Context context, ArrayList<Post> listaPosts) {

        this.context = context;
        this.listaPosts = listaPosts;
        this.myInflater = LayoutInflater.from(context);
        this.listaPostAll = listaPosts;
        this.myLab = PostsLab.get(context);
        this.listaUsers = (ArrayList<User>) myLab.getUsers();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = myInflater.inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Obtener la lista y colocarla en la CardView
        //La posicion 1 es el usuario y la 2 el titulo
        holder.titulo.setText(MainActivity.capitalizarPrimeraletra(listaPosts.get(position).getTitle()));
        holder.autor.setText(listaUsers.get(listaPosts.get(position).getUserId() - 1).getName());
    }


    @Override
    public int getItemCount() {
        return listaPosts.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Post> listaFiltrada = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                listaFiltrada.addAll(listaPostAll);
            } else {
                for (Post post : listaPostAll) {
                    if (post.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        if (!listaFiltrada.contains(post)) {
                            listaFiltrada.add(post);
                        }
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = listaFiltrada;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaPosts.clear();
            listaPosts.addAll((Collection<? extends Post>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        private TextView titulo, autor;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo_post_cardview);
            autor = itemView.findViewById(R.id.nombre_autor_cardview);
            itemView.setOnClickListener(this::onClick);
            itemView.setOnCreateContextMenuListener(this::onCreateContextMenu);

        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemCLick(v, getAdapterPosition());
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 111, 0, "Info. Autor");
            menu.add(this.getAdapterPosition(), 112, 1, "Modificar");
            menu.add(this.getAdapterPosition(), 113, 2, "Eliminar");
        }

        MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                return false;
            }
        };
    }

    public interface ItemClickListener {
        void onItemCLick(View view, int position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
