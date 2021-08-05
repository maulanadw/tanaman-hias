package com.maulanadw.tanamanhias;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList, filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_user, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        // get data
        final ModelProduct modelProduct = productsList.get(position);
        String diskonTersedia = modelProduct.getDiskonTersedia();
        String diskonPersen = modelProduct.getDiskonPersen();
        String hargaDiskon = modelProduct.getHargaDiskon();
        String kategoriProduk = modelProduct.getKategoriProduk();
        String hargaUtama = modelProduct.getHargaUtama();
        String deskripsiProduk = modelProduct.getDeskripsiProduk();
        String namaProduk = modelProduct.getNamaProduk();
        String jumlahProduk = modelProduct.getJumlahProduk();
        String idProduk = modelProduct.getIdProduk();
        String timeStamp = modelProduct.getTimeStamp();
        String iconProduk = modelProduct.getIconProduk();

        // set data
        holder.tvNamaProduk.setText(namaProduk);
        holder.tvDiskonPersen.setText(diskonPersen);
        holder.tvDeskripsiProduk.setText(deskripsiProduk);
        holder.tvHargaUtama.setText("Rp " + hargaUtama);
        holder.tvHargaDiskon.setText("Rp " + hargaDiskon);

        if (diskonTersedia.equals("true")){
            // produk sedang diskon
            holder.tvHargaDiskon.setVisibility(View.VISIBLE);
            holder.tvDiskonPersen.setVisibility(View.VISIBLE);
            holder.tvHargaUtama.setPaintFlags(holder.tvHargaUtama.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // coret pada hargaUtama
        } else {
            // sedang tidak diskon
            holder.tvHargaDiskon.setVisibility(View.GONE);
            holder.tvDiskonPersen.setVisibility(View.GONE);
            holder.tvHargaUtama.setPaintFlags(0);
        } try {
            Picasso.get().load(iconProduk).placeholder(R.drawable.ic_product_gray).into(holder.ivIconProduk);
        } catch (Exception e){
            holder.ivIconProduk.setImageResource(R.drawable.ic_product_gray);
        }

        holder.tvMasukanKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tambah produk ke keranjang
                showDialogJumlah(modelProduct);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tampilkan detail produk
            }
        });
    }

    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;
    private void showDialogJumlah(ModelProduct modelProduct) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.product_quantity, null);

        // init layout
        ImageView ivProduk = view.findViewById(R.id.ivProduk);
        TextView tvDiskonPersen = view.findViewById(R.id.tvDiskonPersen);
        TextView tvNamaProduk = view.findViewById(R.id.tvNamaProduk);
        TextView tvJumlahProduk = view.findViewById(R.id.tvJumlahProduk);
        TextView tvHargaDiskon = view.findViewById(R.id.tvHargaDiskon);
        TextView tvHargaUtama = view.findViewById(R.id.tvHargaUtama);
        TextView tvTotalHarga = view.findViewById(R.id.tvTotalHarga);
        ImageButton btnDecrement = view.findViewById(R.id.btnDecrement);
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        ImageButton btnIncrement = view.findViewById(R.id.btnIncrement);
        Button btnMasukanKeranjang = view.findViewById(R.id.btnMasukanKeranjang);

        // get data dari model
        String idProduk = modelProduct.getIdProduk();
        String namaProduk = modelProduct.getNamaProduk();
        String jumlahProduk = modelProduct.getJumlahProduk();
        String diskonPersen = modelProduct.getDiskonPersen();
        String iconProduk = modelProduct.getIconProduk();

        String harga;
        if (modelProduct.getDiskonTersedia().equals("true")) {
            // produk ada diskon
            harga = modelProduct.getHargaDiskon();
            tvDiskonPersen.setVisibility(View.VISIBLE);
            tvHargaUtama.setPaintFlags(tvHargaUtama.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            // produk tidak sedang diskon
            tvDiskonPersen.setVisibility(View.GONE);
            tvHargaDiskon.setVisibility(View.GONE);
            harga = modelProduct.getHargaUtama();
        }

        cost = Double.parseDouble(harga.replaceAll("Rp ", ""));
        finalCost = Double.parseDouble(harga.replaceAll("Rp ", ""));
        quantity = 1;

        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        // set data
        try {
            Picasso.get().load(iconProduk).placeholder(R.drawable.ic_product_gray).into(ivProduk);
        } catch (Exception e) {
            ivProduk.setImageResource(R.drawable.ic_product_gray);
        }

        tvNamaProduk.setText("" + namaProduk);
        tvJumlahProduk.setText("" + jumlahProduk);
        tvDiskonPersen.setText("" + diskonPersen);
        tvQuantity.setText("" + quantity);
        tvHargaUtama.setText("Rp " + modelProduct.getHargaUtama());
        tvHargaDiskon.setText("Rp " + modelProduct.getHargaDiskon());
        tvTotalHarga.setText("Rp " + finalCost);

        AlertDialog dialog = builder.create();
        dialog.show();

        // klik tambah jumlah produk
        btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;

                tvTotalHarga.setText("Rp " + finalCost);
                tvQuantity.setText("" + quantity);
            }
        });

        // klik kurangi jumlah produk
        btnDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    finalCost = finalCost - cost;
                    quantity--;

                    tvTotalHarga.setText("Rp " + finalCost);
                    tvQuantity.setText("" + quantity);
                }
            }
        });

        btnMasukanKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaProduk = tvNamaProduk.getText().toString().trim();
                String hargaUtama = harga;
                String totalHarga = tvTotalHarga.getText().toString().trim().replace("Rp", "");
                String quantity = tvQuantity.getText().toString().trim();

                // tambahkan ke database(SQLite)
                masukkanKeranjang(idProduk, namaProduk, hargaUtama, totalHarga, quantity);
                dialog.dismiss();
            }
        });
    }

    private int idItem = 1;
    private void masukkanKeranjang(String idProduk, String namaProduk, String hargaUtama, String totalHarga, String quantity) {
        idItem++;

        EasyDB easyDB = EasyDB.init(context, "db_item")
                .setTableName("tabel_item")
                .addColumn(new Column("idItem", new String[]{"text", "unique"}))
                .addColumn(new Column("PIDitem", new String[]{"text", "not null"}))
                .addColumn(new Column("namaItem", new String[]{"text", "not null"}))
                .addColumn(new Column("hargaUtamaItem", new String[]{"text", "not null"}))
                .addColumn(new Column("totalHargaItem", new String[]{"text", "not null"}))
                .addColumn(new Column("jumlahItem", new String[]{"text", "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("idItem", idItem)
                .addData("PIDitem", idProduk)
                .addData("namaItem", namaProduk)
                .addData("hargaUtamaItem", hargaUtama)
                .addData("totalHargaItem", totalHarga)
                .addData("jumlahItem", quantity)
                .doneDataAdding();

        Toast.makeText(context, "Dimasukkan ke Keranjang", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterProductUser(this, filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView ivIconProduk;
        private TextView tvDiskonPersen, tvNamaProduk, tvDeskripsiProduk, tvMasukanKeranjang, tvHargaDiskon, tvHargaUtama;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            ivIconProduk = itemView.findViewById(R.id.ivIconProduk);
            tvDiskonPersen = itemView.findViewById(R.id.tvDiskonPersen);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProduk);
            tvDeskripsiProduk = itemView.findViewById(R.id.tvDeskripsiProduk);
            tvMasukanKeranjang = itemView.findViewById(R.id.tvMasukanKeranjang);
            tvHargaDiskon = itemView.findViewById(R.id.tvHargaDiskon);
            tvHargaUtama = itemView.findViewById(R.id.tvHargaUtama);
        }
    }

}