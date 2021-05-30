package com.maulanadw.tanamanhias;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditProfileSellerActivity extends AppCompatActivity implements LocationListener {

    private ImageButton btnKembali, btnGps;
    private ImageView ivProfil;
    private EditText etNama, etNamaToko, etNoHp, etOngkosKirim, etProvinsi, etKabupaten, etKota, etAlamat;
    private AppCompatCheckBox checkboxBukaToko;
    private Button btnUpdate;

    // nilai constants untuk membuat permintaan izin lokasi, kamera dan penyimpanan
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    // nilai constants untuk membuat permintaan pilih gambar
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // Menggunakan array untuk permissions
    private String[] izinLokasi;
    private String[] izinKamera;
    private String[] izinPenyimpanan;

    // Gambar diambil dari URI
    private Uri gambar_uri;

    private double latitude=0.0, longitude=0.0;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private LocationManager kelolaLokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_seller);

        // inisialisasi tampilan
        btnKembali = findViewById(R.id.btnKembali);
        btnGps = findViewById(R.id.btnGps);
        ivProfil = findViewById(R.id.ivProfil);
        etNama = findViewById(R.id.etNama);
        etNamaToko = findViewById(R.id.etNamaToko);
        etNoHp = findViewById(R.id.etNoHp);
        etOngkosKirim = findViewById(R.id.etOngkosKirim);
        etProvinsi = findViewById(R.id.etProvinsi);
        etKabupaten = findViewById(R.id.etKabupaten);
        etKota = findViewById(R.id.etKota);
        etAlamat = findViewById(R.id.etAlamat);
        checkboxBukaToko = findViewById(R.id.checkboxBukaToko);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Inisialisasi izin arrays
        izinLokasi = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        izinKamera = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        izinPenyimpanan = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        cekUser();

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ambil gambar
                tampilDialogMilihGambar();
            }
        });

        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mendeteksi lokasi terkini
                if (cekIzinLokasi()){
                    // sudah disetujui
                    deteksiLokasi();
                }
                else {
                    // tidak disetujui, maka lakukan permintaan
                    memintaIzinLokasi();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private String namaLengkap, namaToko, noHp, biayaPengiriman, provinsi, kabupaten, kota, alamat;
    private boolean bukaToko;

    private void inputData() {
        // input data
        namaLengkap = etNama.getText().toString().trim();
        namaToko = etNamaToko.getText().toString().trim();
        noHp = etNoHp.getText().toString().trim();
        biayaPengiriman = etOngkosKirim.getText().toString().trim();
        provinsi = etProvinsi.getText().toString().trim();
        kabupaten = etKabupaten.getText().toString().trim();
        kota = etKota.getText().toString().trim();
        alamat = etAlamat.getText().toString().trim();
        bukaToko = checkboxBukaToko.isChecked(); // true atau false

        updateProfil();
    }

    private void updateProfil() {
        progressDialog.setMessage("Mengupdate profil. . .");
        progressDialog.show();

        if (gambar_uri == null) {
            // update tanpa gambar

            // setup data
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("nama", "" + namaLengkap);
            hashMap.put("namaToko", "" + namaToko);
            hashMap.put("noHp", "" + noHp);
            hashMap.put("biayaPengiriman", "" + biayaPengiriman);
            hashMap.put("provinsi", "" + provinsi);
            hashMap.put("kabupaten", "" + kabupaten);
            hashMap.put("kota", "" + kota);
            hashMap.put("alamat", "" + alamat);
            hashMap.put("latitude", "" + latitude);
            hashMap.put("longitude", "" + longitude);
            hashMap.put("bukaToko", "" + bukaToko);

            // update ke database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // database diupdate
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileSellerActivity.this, "Data berhasil diubah.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // gagal mengupdate ke database
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // update dengan gambar

            /*-----UPLOAD GAMBAR-----*/
            String filePathdanNama = "profile_images/" + "" + firebaseAuth.getUid();
            // storage reference
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathdanNama);
            storageReference.putFile(gambar_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // gambar diupload, dapatkan url dari gambar yg diupload
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadGambarUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                // gambar url diterima, kemudian update ke database
                                // setup data untuk diupdate
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("nama", "" + namaLengkap);
                                hashMap.put("namaToko", "" + namaToko);
                                hashMap.put("noHp", "" + noHp);
                                hashMap.put("biayaPengiriman", "" + biayaPengiriman);
                                hashMap.put("provinsi", "" + provinsi);
                                hashMap.put("kabupaten", "" + kabupaten);
                                hashMap.put("kota", "" + kota);
                                hashMap.put("alamat", "" + alamat);
                                hashMap.put("latitude", "" + latitude);
                                hashMap.put("longitude", "" + longitude);
                                hashMap.put("bukaToko", "" + bukaToko);
                                hashMap.put("gambarProfil", "" + downloadGambarUri);

                                // update ke database
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // database diupdate
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProfileSellerActivity.this, "Data berhasil diubah.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // gagal mengupdate ke database
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProfileSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void cekUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        else {
            loadInfoKu();
        }
    }

    private void loadInfoKu() {
        // load user info, dan tampilkan
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String tipeAkun = ""+ds.child("tipeAkun").getValue();
                            String alamat = ""+ds.child("alamat").getValue();
                            String provinsi = ""+ds.child("provinsi").getValue();
                            String kabupaten = ""+ds.child("kabupaten").getValue();
                            String kota = ""+ds.child("kota").getValue();
                            String biayaPengiriman = ""+ds.child("biayaPengiriman").getValue();
                            String email = ""+ds.child("email").getValue();
                            latitude = Double.parseDouble(""+ds.child("latitude").getValue());
                            longitude = Double.parseDouble(""+ds.child("longitude").getValue());
                            String nama = ""+ds.child("nama").getValue();
                            String online = ""+ds.child("online").getValue();
                            String noHp = ""+ds.child("noHp").getValue();
                            String gambarProfil = ""+ds.child("gambarProfil").getValue();
                            String timeStamp = ""+ds.child("timeStamp").getValue();
                            String namaToko = ""+ds.child("namaToko").getValue();
                            String bukaToko = ""+ds.child("bukaToko").getValue();
                            String uid = ""+ds.child("uid").getValue();

                            etNama.setText(nama);
                            etNoHp.setText(noHp);
                            etProvinsi.setText(provinsi);
                            etKabupaten.setText(kabupaten);
                            etKota.setText(kota);
                            etAlamat.setText(alamat);
                            etNamaToko.setText(namaToko);
                            etOngkosKirim.setText(biayaPengiriman);

                            if (bukaToko.equals("true")){
                                checkboxBukaToko.setChecked(true);
                                Toast.makeText(EditProfileSellerActivity.this, "Toko Anda sedang buka.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                checkboxBukaToko.setChecked(false);
                                Toast.makeText(EditProfileSellerActivity.this, "Toko Anda sedang tutup", Toast.LENGTH_SHORT).show();
                            }

                            try {
                                Picasso.get().load(gambarProfil).placeholder(R.drawable.ic_store_gray).into(ivProfil);
                            }
                            catch (Exception e){
                                ivProfil.setImageResource(R.drawable.ic_person_gray);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void tampilDialogMilihGambar() {
        // Pilihan untuk menampilkan dalam dialog
        String[] opsi = {"Kamera", "Galeri"};
        // Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih gambar dari").setItems(opsi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Menangani klik
                if (which == 0){
                    // kamera diklik
                    if (cekIzinKamera()){
                        // izin kamera disetujui, buka kamera
                        ambilGambarKamera();
                    }
                    else {
                        // tidak disetujui, lakukan permintaan
                        memintaIzinKamera();
                    }
                }
                else {
                    // galeri diklik
                    if (cekIzinPenyimpanan()){
                        // izin penyimpanan disetujui, buka gallery
                        ambilGambarGaleri();
                    }
                    else {
                        // tidak disetujui, lakukan permintaan
                        memintaIzinPenyimpanan();
                    }
                }
            }
        }).show();
    }

    private void memintaIzinPenyimpanan() {
        ActivityCompat.requestPermissions(this, izinPenyimpanan, STORAGE_REQUEST_CODE);
    }

    private void memintaIzinKamera() {
        ActivityCompat.requestPermissions(this, izinKamera, CAMERA_REQUEST_CODE);
    }

    private void memintaIzinLokasi() {
        ActivityCompat.requestPermissions(this, izinLokasi, LOCATION_REQUEST_CODE);
    }

    private boolean cekIzinPenyimpanan() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private boolean cekIzinKamera() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private boolean cekIzinLokasi() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void ambilGambarGaleri() {
        // intent untuk memilih gambar dari galeri
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void ambilGambarKamera() {
        // intent untuk memilih gambar dari kamera
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");

        gambar_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, gambar_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void deteksiLokasi() {
        Toast.makeText(this, "Tunggu sebentar . . .", Toast.LENGTH_LONG).show();

        kelolaLokasi = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        kelolaLokasi.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
    }

    private void cariAlamat() {
        // Cari alamat, provinsi, kabupaten, kota
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String alamat = addresses.get(0).getAddressLine(0);    // alamat lengkap
            String provinsi = addresses.get(0).getAdminArea();
            String kabupaten = addresses.get(0).getSubAdminArea();
            String kota = addresses.get(0).getLocality();

            // atur alamat
            etAlamat.setText(alamat);
            etProvinsi.setText(provinsi);
            etKabupaten.setText(kabupaten);
            etKota.setText(kota);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Lokasi terdeteksi
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        cariAlamat();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // GPS/ location dinonaktifkan
        Toast.makeText(this, "Untuk melanjutkan, aktifkan lokasi perangkat.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean lokasiDiterima = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (lokasiDiterima){
                        // izin diizinkan
                        deteksiLokasi();
                    }
                    else {
                        // izin ditolak
                        Toast.makeText(this,"Izin lokasi dibutuhkan . . .", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean kameraDiizinkan = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean penyimpananDiizinkan = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (kameraDiizinkan && penyimpananDiizinkan){
                        // jika diizinkan
                        ambilGambarKamera();
                    }
                    else {
                        // jika ditolak
                        Toast.makeText(this,"Izin Kamera dibutuhkan . . .", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean penyimpananDiizinkan = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (penyimpananDiizinkan){
                        // jika diizinkan
                        ambilGambarGaleri();
                    }
                    else {
                        // jika ditolak
                        Toast.makeText(this,"Izin Gallery dibutuhkan . . .", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // menangani hasil tangkapan gambar
        if (resultCode==RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                // dapatkan gambar yang dipilih
                gambar_uri = data.getData();
                // setel ke ImageView
                ivProfil.setImageURI(gambar_uri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                // setel ke ImageView
                ivProfil.setImageURI(gambar_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}