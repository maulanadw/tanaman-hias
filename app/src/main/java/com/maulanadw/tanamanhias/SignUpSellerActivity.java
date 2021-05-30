package com.maulanadw.tanamanhias;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SignUpSellerActivity extends AppCompatActivity implements LocationListener {

    private ImageButton btnKembali, btnGps;
    private ImageView ivProfil;
    private EditText etNama, etNamaToko, etNoHp, etOngkosKirim, etProvinsi, etKabupaten, etKota, etAlamat, etEmail, etPassword, etKonfirmasiPassword;
    private Button btnDaftar;

    // nilai constants untuk membuat permintaan izin lokasi, kamera dan penyimpanan
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    // nilai constants untuk membuat permintaan pilih gambar
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // Menggunakan array
    private String[] izinLokasi;
    private String[] izinKamera;
    private String[] izinPenyimpanan;
    // Gambar diambil dari URI
    private Uri gambar_uri;

    private double latitude=0.0, longitude=0.0;

    private LocationManager kelolaLokasi;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_seller);

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
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etKonfirmasiPassword = findViewById(R.id.etKonfirmasiPassword);
        btnDaftar = findViewById(R.id.btnDaftar);

        // Inisialisasi izin arrays
        izinLokasi = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        izinKamera = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        izinPenyimpanan = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar");
        progressDialog.setCanceledOnTouchOutside(false);

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mendeteksi lokasi terkini
                if (cekIzinLokasi()){
                    // telah disetujui
                    deteksiLokasi();
                }
                else {
                    // tidak disetujui, lakukan permintaan
                    memintaIzinLokasi();
                }
            }
        });

        ivProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ambil gambar
                tampilDialogMilihGambar();
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // daftar akun baru untuk pengguna
                inputData();
            }
        });
    }

    private String namaLengkap, namaToko, noHp, biayaPengiriman, provinsi, kabupaten, kota, alamat, email, password, konfirmasiPassword;
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
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        konfirmasiPassword = etKonfirmasiPassword.getText().toString().trim();

        // validasi data
        if (TextUtils.isEmpty(namaLengkap)){
            Toast.makeText(this, "Masukkan Nama Lengkap.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(namaToko)){
            Toast.makeText(this, "Masukkan Nama Toko.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(noHp)){
            Toast.makeText(this, "Masukkan Nomor HP.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(biayaPengiriman)){
            Toast.makeText(this, "Masukkan Biaya Pengiriman.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitude == 0.0 || longitude == 0.0){
            Toast.makeText(this, "Isi Alamat anda atau klik tombol GPS di kanan atas.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Email tidak valid.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<8){
            Toast.makeText(this, "Password minimal 8 karakter.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(konfirmasiPassword)){
            Toast.makeText(this, "Password tidak cocok.", Toast.LENGTH_SHORT).show();
            return;
        }

        buatAkun();
    }

    private void buatAkun() {
        progressDialog.setMessage("Membuat Akun Baru . . .");
        progressDialog.show();

        // buat akun, hubungkan ke firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // akun dibuat
                        simpanFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // gagal membuat akun
                        progressDialog.dismiss();
                        Toast.makeText(SignUpSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // verifikasi email
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()){
                        startActivity(new Intent(SignUpSellerActivity.this, LoginActivity.class));
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(SignUpSellerActivity.this, "Cek email Anda untuk memverifikasi akun.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpSellerActivity.this, "Gagal untuk mendaftar!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void simpanFirebaseData() {
        progressDialog.setMessage("Menyimpan Info Akun . . .");

        String timeStamp = ""+System.currentTimeMillis();

        if (gambar_uri==null){
            // simpan info tanpa gambar

            // setup data untuk menyimpan
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+email);
            hashMap.put("nama",""+namaLengkap);
            hashMap.put("namaToko",""+namaToko);
            hashMap.put("noHp",""+noHp);
            hashMap.put("biayaPengiriman",""+biayaPengiriman);
            hashMap.put("provinsi",""+provinsi);
            hashMap.put("kabupaten",""+kabupaten);
            hashMap.put("kota",""+kota);
            hashMap.put("alamat",""+alamat);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longitude",""+longitude);
            hashMap.put("timeStamp",""+timeStamp);
            hashMap.put("tipeAkun","Seller");
            hashMap.put("online","true");
            hashMap.put("bukaToko","true");
            hashMap.put("gambarProfil","");

            // simpan ke database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // database diupdate
                            progressDialog.dismiss();
                            startActivity(new Intent(SignUpSellerActivity.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // gagal mengupdate database
                            progressDialog.dismiss();
                            startActivity(new Intent(SignUpSellerActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
        }
        else {
            // simpan info dengan gambar

            // path dan nama dari gambar
            String filePathdanNama = "profile_images/" + ""+firebaseAuth.getUid();
            // Upload gambar
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathdanNama);
            storageReference.putFile(gambar_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // get url dari gambar yang diupload
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadGambarUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){

                                // setup data untuk menyimpan
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+email);
                                hashMap.put("nama",""+namaLengkap);
                                hashMap.put("namaToko",""+namaToko);
                                hashMap.put("noHp",""+noHp);
                                hashMap.put("biayaPengiriman",""+biayaPengiriman);
                                hashMap.put("provinsi",""+provinsi);
                                hashMap.put("kabupaten",""+kabupaten);
                                hashMap.put("kota",""+kota);
                                hashMap.put("alamat",""+alamat);
                                hashMap.put("latitude",""+latitude);
                                hashMap.put("longitude",""+longitude);
                                hashMap.put("timeStamp",""+timeStamp);
                                hashMap.put("tipeAkun","Seller");
                                hashMap.put("online","true");
                                hashMap.put("bukaToko","true");
                                hashMap.put("gambarProfil",""+downloadGambarUri); // url dari gambar yang diupload

                                // simpan ke database
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // database diupdate
                                                progressDialog.dismiss();
                                                startActivity(new Intent(SignUpSellerActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // gagal mengupdate database
                                                progressDialog.dismiss();
                                                startActivity(new Intent(SignUpSellerActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
                        // izin kamera disetujui
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
                        // izin penyimpanan disetujui
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

    private void ambilGambarGaleri(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void ambilGambarKamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        gambar_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, gambar_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void deteksiLokasi(){
        Toast.makeText(this, "Tunggu sebentar . . .", Toast.LENGTH_LONG).show();

        kelolaLokasi = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        kelolaLokasi.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
    }

    private boolean cekIzinLokasi(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void memintaIzinLokasi(){
        ActivityCompat.requestPermissions(this, izinLokasi, LOCATION_REQUEST_CODE);
    }

    private boolean cekIzinPenyimpanan(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void memintaIzinPenyimpanan(){
        ActivityCompat.requestPermissions(this, izinPenyimpanan, STORAGE_REQUEST_CODE);
    }

    private boolean cekIzinKamera(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void memintaIzinKamera(){
        ActivityCompat.requestPermissions(this, izinKamera, CAMERA_REQUEST_CODE);
    }


    
    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Lokasi terdeteksi
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        cariAlamat();
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
                    boolean lokasiDiizinkan = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (lokasiDiizinkan){
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