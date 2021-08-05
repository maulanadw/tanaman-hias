package com.maulanadw.tanamanhias;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {

    private TextView tvNama, tvNamaToko, tvEmail, tvTabProduk, tvTabOrder, tvProdukDifilter;
    private ImageButton btnLogout, btnEditProfil, btnTambahProduk, btnFilterProduk;
    private ImageView ivProfil;
    private RelativeLayout rlProduk, rlOrder;
    private EditText etCariProduk;
    private RecyclerView rvProduk;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        tvNama = findViewById(R.id.tvNama);
        tvNamaToko = findViewById(R.id.tvNamaToko);
        //tvEmail = findViewById(R.id.tvEmail);
        tvTabProduk = findViewById(R.id.tvTabProduk);
        tvTabOrder = findViewById(R.id.tvTabOrder);
        tvProdukDifilter = findViewById(R.id.tvProdukDifilter);
        etCariProduk = findViewById(R.id.etCariProduk);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfil = findViewById(R.id.btnEditProfil);
        btnTambahProduk = findViewById(R.id.btnTambahProduk);
        btnFilterProduk = findViewById(R.id.btnFilterProduk);
        ivProfil = findViewById(R.id.ivProfil);
        rlProduk = findViewById(R.id.rlProduk);
        rlOrder = findViewById(R.id.rlOrder);
        rvProduk = findViewById(R.id.rvProduk);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        cekUser();
        loadSemuaProduk();

        tampilkanProduk();

        // cari
        etCariProduk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductSeller.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // buat jadi offline jika keluar dari aplikasi
                // dan arahkan ke login activity
                buatSayaOffline();
            }
        });

        btnEditProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open edit activity
                startActivity(new Intent(MainSellerActivity.this, EditProfileSellerActivity.class));
            }
        });

        btnTambahProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open tambah produk activity
                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));
            }
        });

        tvTabProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load produk
                tampilkanProduk();
            }
        });

        tvTabOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load order
                tampilkanOrder();
            }
        });

        btnFilterProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Pilih Kategori")
                        .setItems(Constants.kategoriProduk1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // item dipilih
                                String dipilih = Constants.kategoriProduk1[which];
                                tvProdukDifilter.setText(dipilih);
                                if (dipilih.equals("Semua")) {
                                    // load semua
                                    loadSemuaProduk();
                                } else {
                                    // load produk yang difilter
                                    loadProdukdiFilter(dipilih);
                                }
                            }
                        })
                .show();
            }
        });

    }

    private void loadProdukdiFilter(String dipilih) {
        productList = new ArrayList<>();

        // get semua produk
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // reset list
                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {

                            String kategoriProduk = "" + ds.child("productCategory").getValue();
                            // jika kategori yang dipilih cocok dengan kategori produk, maka tambahkan kedalam daftar
                            if (dipilih.equals(kategoriProduk)) {
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }
                        }
                        // setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        // set adapter
                        rvProduk.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadSemuaProduk() {
        productList = new ArrayList<>();

        // get semua produk
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // reset list
                        productList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        // setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        // set adapter
                        rvProduk.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void tampilkanProduk() {
        // tampilkan produk dan sembunyikan tampilan order
        rlProduk.setVisibility(View.VISIBLE);
        rlOrder.setVisibility(View.GONE);

        tvTabProduk.setTextColor(getResources().getColor(R.color.black));
        tvTabProduk.setBackgroundResource(R.drawable.shape_rectangle3);

        tvTabOrder.setTextColor(getResources().getColor(R.color.white));
        tvTabOrder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
    private void tampilkanOrder() {
        // tampilkan order dan sembunyikan tampilan produk
        rlProduk.setVisibility(View.GONE);
        rlOrder.setVisibility(View.VISIBLE);

        tvTabProduk.setTextColor(getResources().getColor(R.color.white));
        tvTabProduk.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tvTabOrder.setTextColor(getResources().getColor(R.color.black));
        tvTabOrder.setBackgroundResource(R.drawable.shape_rectangle3);
    }

    private void buatSayaOffline() {
        // setelah login berhasil, buat user menjadi online
        progressDialog.setMessage("Logout");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        // update value ke database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // berhasil mengupdate
                        firebaseAuth.signOut();
                        cekUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // gagal mengupdate
                        progressDialog.dismiss();
                        Toast.makeText(MainSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cekUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
            finish();
        }
        else {
            loadInfoKu();
        }
    }

    private void loadInfoKu() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            // get data dari database
                            String nama = ""+ds.child("nama").getValue();
                            String tipeAkun = ""+ds.child("tipeAkun").getValue();
                            String email = ""+ds.child("email").getValue();
                            String namaToko = ""+ds.child("namaToko").getValue();
                            String gambarProfil = ""+ds.child("gambarProfil").getValue();

                            // set data ke tampilan
                            tvNama.setText(nama);
                            tvNamaToko.setText(namaToko);
                            //tvEmail.setText(email);
                            try {
                                Picasso.get().load(gambarProfil).placeholder(R.drawable.ic_store_gray).into(ivProfil);
                            } catch (Exception e){
                                ivProfil.setImageResource(R.drawable.ic_store_gray);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}