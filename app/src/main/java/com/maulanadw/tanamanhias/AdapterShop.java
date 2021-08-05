package com.maulanadw.tanamanhias;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop> {

    private Context context;
    public ArrayList<ModelShop> shopList;

    public AdapterShop(Context context, ArrayList<ModelShop> shopList) {
        this.context = context;
        this.shopList = shopList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop, parent, false);
        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
        // get data
        ModelShop modelShop = shopList.get(position);
        String tipeAkun = modelShop.getTipeAkun();
        String uid = modelShop.getUid();
        String nama = modelShop.getNama();
        String noHp = modelShop.getNoHp();
        String gambarProfil = modelShop.getGambarProfil();
        String alamat = modelShop.getAlamat();
        String kota = modelShop.getKota();
        String kabupaten = modelShop.getKabupaten();
        String provinsi = modelShop.getProvinsi();
        String biayaPengiriman = modelShop.getBiayaPengiriman();
        String email = modelShop.getEmail();
        String online = modelShop.getOnline();
        String latitude = modelShop.getLatitude();
        String longitude = modelShop.getLongitude();
        String timeStamp = modelShop.getTimeStamp();
        String bukaToko = modelShop.getBukaToko();
        String namaToko = modelShop.getNamaToko();

        // set data
        holder.tvNamaToko.setText(namaToko);
        holder.tvNoHP.setText(noHp);
        holder.tvAlamat.setText(alamat);

        // jika online
        if (online.equals("true")) {
            // pemilik toko sedang online
            holder.ivOnline.setVisibility(View.VISIBLE);
        } else {
            // pemilik toko sedang offline
            holder.ivOnline.setVisibility(View.GONE);
        }

        // jika toko buka
        if (bukaToko.equals("true")) {
            // toko buka
            holder.tvTokoTutup.setVisibility(View.GONE);
        } else {
            holder.tvTokoTutup.setVisibility(View.VISIBLE);
        }

        try {
            Picasso.get().load(gambarProfil).placeholder(R.drawable.ic_store_gray).into(holder.ivToko);
        } catch (Exception e) {
            holder.ivToko.setImageResource(R.drawable.ic_store_gray);
        }

        // menangani click listener, kemudian tampilkan detail si toko
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopDetailsActivity.class);
                intent.putExtra("uidToko", uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    class HolderShop extends RecyclerView.ViewHolder {

        private ImageView ivToko, ivOnline;
        private TextView tvTokoTutup, tvNamaToko, tvNoHP, tvAlamat;
        //private RatingBar ratingBar;

        public HolderShop(@NonNull View itemView) {
            super(itemView);

            ivToko = itemView.findViewById(R.id.ivToko);
            ivOnline = itemView.findViewById(R.id.ivOnline);
            tvTokoTutup = itemView.findViewById(R.id.tvTokoTutup);
            tvNamaToko = itemView.findViewById(R.id.tvNamaToko);
            tvNoHP = itemView.findViewById(R.id.tvNoHP);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            //ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
