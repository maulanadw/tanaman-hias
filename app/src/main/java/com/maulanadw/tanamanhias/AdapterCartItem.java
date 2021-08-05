package com.maulanadw.tanamanhias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem> {

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate item_cart.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, int position) {
        // get data
        ModelCartItem modelCartItem = cartItems.get(position);
        String id = modelCartItem.getId();
        String pid = modelCartItem.getPid();
        String nama = modelCartItem.getNama();
        final String cost = modelCartItem.getCost();
        String harga = modelCartItem.getHarga();
        String jumlah = modelCartItem.getJumlah();

        // set data
        holder.tvNamaItem.setText("" + nama);
        holder.tvHargaItem.setText("" + cost);
        holder.tvJumlahItem.setText("x" + jumlah);
        holder.tvHargaPerItem.setText("" + harga);

        // handle hapus item dari keranjang
        holder.tvHapusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyDB easyDB = EasyDB.init(context, "db_item")
                        .setTableName("tabel_item")
                        .addColumn(new Column("idItem", new String[]{"text", "unique"}))
                        .addColumn(new Column("PIDitem", new String[]{"text", "not null"}))
                        .addColumn(new Column("namaItem", new String[]{"text", "not null"}))
                        .addColumn(new Column("hargaUtamaItem", new String[]{"text", "not null"}))
                        .addColumn(new Column("totalHargaItem", new String[]{"text", "not null"}))
                        .addColumn(new Column("jumlahItem", new String[]{"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1, id);

                // refresh list
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                double tx = Double.parseDouble((((ShopDetailsActivity)context).tvTotalSemuaHarga.getText().toString().trim().replace("Rp", "")));
                double totalHarga = tx - Double.parseDouble(cost.replace("Rp", ""));
                double ongkosKirim = Double.parseDouble((((ShopDetailsActivity)context).ongkosKirim.replace("Rp", "")));
                double subTotalHarga = Double.parseDouble(String.format("%.2f", totalHarga)) - Double.parseDouble(String.format("%.2f", ongkosKirim));
                ((ShopDetailsActivity)context).allTotalHarga=0.0;
                ((ShopDetailsActivity)context).tvSubTotal.setText("Rp" + String.format("%.2f", subTotalHarga));
                ((ShopDetailsActivity)context).tvTotalSemuaHarga.setText("Rp" + String.format("%.2f", Double.parseDouble(String.format("%.2f", totalHarga))));

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder {

        // item_cart.xml
        private TextView tvNamaItem, tvHargaItem, tvHargaPerItem, tvJumlahItem, tvHapusItem;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            tvNamaItem = itemView.findViewById(R.id.tvNamaItem);
            tvHargaItem = itemView.findViewById(R.id.tvHargaItem);
            tvHargaPerItem = itemView.findViewById(R.id.tvHargaPerItem);
            tvJumlahItem = itemView.findViewById(R.id.tvJumlahItem);
            tvHapusItem = itemView.findViewById(R.id.tvHapusItem);

        }
    }

}
