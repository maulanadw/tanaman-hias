package com.maulanadw.tanamanhias;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProduct> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_seller, parent, false);
        return new HolderProduct(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProduct holder, int position) {
        // get data
        final ModelProduct modelProduct = productList.get(position);
        String id = modelProduct.getIdProduk();
        String uid = modelProduct.getUid();
        String diskonTersedia = modelProduct.getDiskonTersedia();
        String diskonPersen = modelProduct.getDiskonPersen();
        String hargaDiskon = modelProduct.getHargaDiskon();
        String hargaUtama = modelProduct.getHargaUtama();
        String kategoriProduk = modelProduct.getKategoriProduk();
        String deskripsiProduk = modelProduct.getDeskripsiProduk();
        String icon = modelProduct.getIconProduk();
        String jumlahProduk = modelProduct.getJumlahProduk();
        String namaProduk = modelProduct.getNamaProduk();
        String timeStamp = modelProduct.getTimeStamp();

        // set data
        holder.tvNamaProduk.setText(namaProduk);
        holder.tvJumlahProduk.setText(jumlahProduk);
        holder.tvDiskonPersen.setText(diskonPersen);
        holder.tvHargaDiskon.setText("Rp " + hargaDiskon);
        holder.tvHargaUtama.setText("Rp " + hargaUtama);

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
            Picasso.get().load(icon).placeholder(R.drawable.ic_product_gray).into(holder.ivIconProduk);
        } catch (Exception e){
            holder.ivIconProduk.setImageResource(R.drawable.ic_product_gray);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // saat item diklik, tampilkan detail item
                detailProduk(modelProduct); // modelProduct berisi detail produk yg diklik
            }
        });
    }

    private void detailProduk(ModelProduct modelProduct) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.product_details_seller,null);
        bottomSheetDialog.setContentView(view);

        //ImageButton btnKembali = view.findViewById(R.id.btnKembali);
        ImageButton btnHapusProduk = view.findViewById(R.id.btnHapusProduk);
        ImageButton btnEditProduk = view.findViewById(R.id.btnEditProduk);
        ImageView ivIconProduk = view.findViewById(R.id.ivIconProduk);
        TextView tvDiskonPersen = view.findViewById(R.id.tvDiskonPersen);
        TextView tvNamaProduk = view.findViewById(R.id.tvNamaProduk);
        TextView tvDeskripsiProduk = view.findViewById(R.id.tvDeskripsiProduk);
        TextView tvJumlahProduk = view.findViewById(R.id.tvJumlahProduk);
        TextView tvKategoriProduk = view.findViewById(R.id.tvKategoriProduk);
        TextView tvHargaDiskon = view.findViewById(R.id.tvHargaDiskon);
        TextView tvHargaUtama = view.findViewById(R.id.tvHargaUtama);

        // get data
        String id = modelProduct.getIdProduk();
        String uid = modelProduct.getUid();
        String diskonTersedia = modelProduct.getDiskonTersedia();
        String diskonPersen = modelProduct.getDiskonPersen();
        String hargaDiskon = modelProduct.getHargaDiskon();
        String hargaUtama = modelProduct.getHargaUtama();
        String kategoriProduk = modelProduct.getKategoriProduk();
        String deskripsiProduk = modelProduct.getDeskripsiProduk();
        String icon = modelProduct.getIconProduk();
        String jumlahProduk = modelProduct.getJumlahProduk();
        String namaProduk = modelProduct.getNamaProduk();
        String timeStamp = modelProduct.getTimeStamp();

        // set data
        tvNamaProduk.setText(namaProduk);
        tvDeskripsiProduk.setText(deskripsiProduk);
        tvKategoriProduk.setText(kategoriProduk);
        tvJumlahProduk.setText(jumlahProduk);
        tvDiskonPersen.setText(diskonPersen);
        tvHargaDiskon.setText("Rp " + hargaDiskon);
        tvHargaUtama.setText("Rp " + hargaUtama);

        if (diskonTersedia.equals("true")){
            // produk sedang diskon
            tvHargaDiskon.setVisibility(View.VISIBLE);
            tvDiskonPersen.setVisibility(View.VISIBLE);
            tvHargaUtama.setPaintFlags(tvHargaUtama.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // coret pada hargaUtama
        } else {
            // tidak diskon
            tvHargaDiskon.setVisibility(View.GONE);
            tvDiskonPersen.setVisibility(View.GONE);
        } try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_product_gray).into(ivIconProduk);
        } catch (Exception e){
            ivIconProduk.setImageResource(R.drawable.ic_product_gray);
        }

        // show dialog
        bottomSheetDialog.show();

        // edit produk
        btnEditProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                // arahkan ke edit produk activity
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("idProduk", id);
                context.startActivity(intent);
            }
        });

        // delete produk
        btnHapusProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                // konfirmasi hapus produk
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hapus Produk")
                        .setMessage("Yakin ingin menghapus " + namaProduk + " dari katalog ?")
                        .setPositiveButton("HAPUS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete produk
                                hapusProduk(id);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        /*
        * btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        * */

    }

    private void hapusProduk(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // produk dihapus
                        Toast.makeText(context, "Produk berhasil dihapus.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // gagal menghapus produk
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null) {
            filter = new FilterProduct(this, filterList);
        }
        return filter;
    }

    class HolderProduct extends RecyclerView.ViewHolder{
        // Hold views of recyclerview

        private ImageView ivIconProduk;
        private TextView tvDiskonPersen, tvNamaProduk, tvJumlahProduk, tvHargaDiskon, tvHargaUtama;

        public HolderProduct(@NonNull View itemView) {
            super(itemView);

            ivIconProduk = itemView.findViewById(R.id.ivIconProduk);
            tvDiskonPersen = itemView.findViewById(R.id.tvDiskonPersen);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProduk);
            tvJumlahProduk = itemView.findViewById(R.id.tvJumlahProduk);
            tvHargaDiskon = itemView.findViewById(R.id.tvHargaDiskon);
            tvHargaUtama = itemView.findViewById(R.id.tvHargaUtama);
        }
    }
}