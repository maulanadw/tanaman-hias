package com.maulanadw.tanamanhias;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetailsActivity extends AppCompatActivity {

    private ImageView ivToko;
    private TextView tvNamaToko, tvNoHP, tvEmail, tvOpenClose, tvOngkosKirim, tvAlamat, tvProdukDifilter;
    private ImageButton btnCall, btnMap, btnKeranjang, btnKembali, btnFilterProduk;
    private EditText etCariProduk;
    private RecyclerView rvProduk;

    private String uidToko;
    private String myLatitude, myLongitude;
    private String namaToko, emailToko, noHpToko, alamatToko, latitudeToko, longitudeToko;
    public String ongkosKirim;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;

    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        ivToko = findViewById(R.id.ivToko);
        tvNamaToko = findViewById(R.id.tvNamaToko);
        tvNoHP = findViewById(R.id.tvNoHP);
        //tvEmail = findViewById(R.id.tvEmail);
        tvOpenClose = findViewById(R.id.tvOpenClose);
        tvOngkosKirim = findViewById(R.id.tvOngkosKirim);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvProdukDifilter = findViewById(R.id.tvProdukDifilter);
        btnCall = findViewById(R.id.btnCall);
        btnMap = findViewById(R.id.btnMap);
        btnKeranjang = findViewById(R.id.btnKeranjang);
        //btnKembali = findViewById(R.id.btnKembali);
        btnFilterProduk = findViewById(R.id.btnFilterProduk);
        etCariProduk = findViewById(R.id.etCariProduk);
        rvProduk = findViewById(R.id.rvProduk);

        // get uid toko dari intent
        uidToko = getIntent().getStringExtra("uidToko");
        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadDetailToko();
        loadProdukToko();

        hapusDataKeranjang();

        // cari
        etCariProduk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductUser.getFilter().filter(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*
        * btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        * */

        btnKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogKeranjang();
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bukaMap();
            }
        });

        btnFilterProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetailsActivity.this);
                builder.setTitle("Pilih Kategori")
                        .setItems(Constants.kategoriProduk1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // get item dipilih
                                String dipilih = Constants.kategoriProduk1[which];
                                tvProdukDifilter.setText(dipilih);
                                if (dipilih.equals("Semua")) {
                                    // load semua
                                    loadProdukToko();
                                } else {
                                    // load produk yang difilter
                                    adapterProductUser.getFilter().filter(dipilih);
                                }
                            }
                        })
                        .show();
            }
        });
    }

    private void hapusDataKeranjang() {
        EasyDB easyDB = EasyDB.init(this, "db_item")
                .setTableName("tabel_item")
                .addColumn(new Column("idItem", new String[]{"text", "unique"}))
                .addColumn(new Column("PIDitem", new String[]{"text", "not null"}))
                .addColumn(new Column("namaItem", new String[]{"text", "not null"}))
                .addColumn(new Column("hargaUtamaItem", new String[]{"text", "not null"}))
                .addColumn(new Column("totalHargaItem", new String[]{"text", "not null"}))
                .addColumn(new Column("jumlahItem", new String[]{"text", "not null"}))
                .doneTableColumn();

        easyDB.deleteAllDataFromTable();
    }

    public double allTotalHarga = 0.0;
    public TextView tvSubTotal, tvOngkir, tvTotalSemuaHarga;
    private void showDialogKeranjang() {
        cartItemList = new ArrayList<>();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);

        TextView tvNamaToko = view.findViewById(R.id.tvNamaToko);
        RecyclerView rvCartItem = view.findViewById(R.id.rvCartItem);
        tvSubTotal = view.findViewById(R.id.tvSubTotal);
        tvOngkir = view.findViewById(R.id.tvOngkir);
        tvTotalSemuaHarga = view.findViewById(R.id.tvTotalHarga);
        Button btnCheckout = view.findViewById(R.id.btnCheckout);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        tvNamaToko.setText(namaToko);

        EasyDB easyDB = EasyDB.init(this, "db_item")
                .setTableName("tabel_item")
                .addColumn(new Column("idItem", new String[]{"text", "unique"}))
                .addColumn(new Column("PIDitem", new String[]{"text", "not null"}))
                .addColumn(new Column("namaItem", new String[]{"text", "not null"}))
                .addColumn(new Column("hargaUtamaItem", new String[]{"text", "not null"}))
                .addColumn(new Column("totalHargaItem", new String[]{"text", "not null"}))
                .addColumn(new Column("jumlahItem", new String[]{"text", "not null"}))
                .doneTableColumn();

        // get all records from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            String id = res.getString(1);
            String pid = res.getString(2);
            String nama = res.getString(3);
            String harga = res.getString(4);
            String cost = res.getString(5);
            String jumlah = res.getString(6);

            allTotalHarga = allTotalHarga + Double.parseDouble(cost);

            ModelCartItem modelCartItem = new ModelCartItem(
                    ""+id,
                    ""+pid,
                    ""+nama,
                    ""+harga,
                    ""+cost,
                    ""+jumlah
            );

            cartItemList.add(modelCartItem);
        }

        adapterCartItem = new AdapterCartItem(this, cartItemList);
        rvCartItem.setAdapter(adapterCartItem);

        tvOngkosKirim.setText("Rp"+ongkosKirim);
        tvSubTotal.setText("Rp"+String.format("%.2f", allTotalHarga));
        tvTotalSemuaHarga.setText("Rp"+(allTotalHarga + Double.parseDouble(ongkosKirim.replace("Rp", ""))));

        /*
        * tvOngkosKirim.setText("Rp " + ongkosKirim);
        tvSubTotal.setText("Rp " + String.format("%.2f", TotalHarga));
        tvTotalHarga.setText("Rp " + (TotalHarga + Double.parseDouble(ongkosKirim.replace("Rp ", ""))));
        * */

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalHarga = 0.0;
            }
        });
    }

    private void bukaMap() {
        // saddr = source alamat
        // daddr = destinasi alamat
        String alamat = "https://maps.google.com/maps?saddr=" + myLatitude + "," + myLongitude + "&daddr=" + latitudeToko + "," + longitudeToko;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(alamat));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(noHpToko))));
        Toast.makeText(this, "" + noHpToko, Toast.LENGTH_SHORT).show();
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            // get data user
                            String nama = ""+ds.child("nama").getValue();
                            String email = ""+ds.child("email").getValue();
                            String noHp = ""+ds.child("noHp").getValue();
                            String gambarProfil = ""+ds.child("gambarProfil").getValue();
                            String tipeAkun = ""+ds.child("tipeAkun").getValue();
                            String kota = ""+ds.child("kota").getValue();
                            myLatitude = ""+ds.child("latitude").getValue();
                            myLongitude = ""+ds.child("longitude").getValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadDetailToko() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uidToko).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get data toko
                String nama = "" + dataSnapshot.child("nama").getValue();
                namaToko = "" + dataSnapshot.child("namaToko").getValue();
                emailToko = "" + dataSnapshot.child("email").getValue();
                noHpToko = "" + dataSnapshot.child("noHp").getValue();
                latitudeToko = "" + dataSnapshot.child("latitude").getValue();
                longitudeToko = "" + dataSnapshot.child("longitude").getValue();
                alamatToko = "" + dataSnapshot.child("alamat").getValue();
                ongkosKirim = "" + dataSnapshot.child("biayaPengiriman").getValue();
                String fotoProfil = "" + dataSnapshot.child("fotoProfil").getValue();
                String tokoBuka = "" + dataSnapshot.child("tokoBuka").getValue();

                // set data
                tvNamaToko.setText(namaToko);
                //tvEmail.setText(emailToko);
                //tvOngkosKirim.setText("Ongkos Kirim : Rp " + ongkosKirim);
                tvAlamat.setText(alamatToko);
                //tvNoHP.setText(noHpToko);
                if (tokoBuka.equals("true")) {
                    tvOpenClose.setText("Buka");
                } else {
                    tvOpenClose.setText("Sedang libur");
                }
                try {
                    Picasso.get().load(fotoProfil).into(ivToko);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void loadProdukToko() {
        // list
        productsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uidToko).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // hapus list sebelum menambahkan item
                        productsList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }
                        // setup adapter
                        adapterProductUser = new AdapterProductUser(ShopDetailsActivity.this, productsList);
                        // set adapter
                        rvProduk.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}