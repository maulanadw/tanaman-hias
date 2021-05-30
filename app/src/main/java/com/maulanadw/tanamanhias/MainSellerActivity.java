package com.maulanadw.tanamanhias;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {

    private TextView tvNama, tvNamaToko, tvEmail;
    private ImageButton btnLogout, btnEditProfil, btnTambahProduk;
    private ImageView ivProfil;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        tvNama = findViewById(R.id.tvNama);
        tvNamaToko = findViewById(R.id.tvNamaToko);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfil = findViewById(R.id.btnEditProfil);
        btnTambahProduk = findViewById(R.id.btnTambahProduk);
        ivProfil = findViewById(R.id.ivProfil);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        cekUser();

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

    }

    private void buatSayaOffline() {
        // setelah login berhasil, buat user menjadi online
        progressDialog.setMessage("Keluar . . .");

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
                            tvEmail.setText(email);
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