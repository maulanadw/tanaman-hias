package com.maulanadw.tanamanhias;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class EditProductActivity extends AppCompatActivity {

    private ImageButton btnKembali;
    private ImageView ivIconProduk;
    private EditText etNamaProduk, etDeskripsiProduk;
    private TextView tvKategoriProduk, etJumlahProduk, etHargaUtama, etHargaDiskon, etDiskonPersen;
    private SwitchCompat diskonSwitch;
    private Button btnUpdateProduk;

    private String idProduk;

    // membuat permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    // constanta untuk ambil gambar
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // permission dalam arrays
    private String[] izinKamera;
    private String[] izinPenyimpanan;

    // uri, gambar dipilih
    private Uri image_uri;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        //btnKembali = findViewById(R.id.btnKembali);
        ivIconProduk = findViewById(R.id.ivIconProduk);
        etNamaProduk = findViewById(R.id.etNamaProduk);
        etDeskripsiProduk = findViewById(R.id.etDeskripsiProduk);
        tvKategoriProduk = findViewById(R.id.tvKategoriProduk);
        etJumlahProduk = findViewById(R.id.etJumlahProduk);
        etHargaUtama = findViewById(R.id.etHargaUtama);
        diskonSwitch = findViewById(R.id.diskonSwitch);
        etHargaDiskon = findViewById(R.id.etHargaDiskon);
        etDiskonPersen = findViewById(R.id.etDiskonPersen);
        btnUpdateProduk = findViewById(R.id.btnUpdateProduk);

        // get idProduk dari intent
        idProduk = getIntent().getStringExtra("idProduk");

        // tidak diceklis, jangan tampilkan etHargaDiskon dan etHargaDiskonPersen
        etHargaDiskon.setVisibility(View.GONE);
        etDiskonPersen.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        loadDetailProduk();

        // atur progress dialognya
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar");
        progressDialog.setCanceledOnTouchOutside(false);

        // inisialisasi permission arrays
        izinKamera = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        izinPenyimpanan = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        /* jika discountSwitch diceklis:
        - tampilkan etHargaDiskon dan etHargaDiskonPersen
        jika discountSwitch tidak diceklis:
        - jangan tampilkan etHargaDiskon dan etHargaDiskonPersen
        */
        diskonSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // diceklis, tampilkan etHargaDiskon dan etHargaDiskonPersen
                    etHargaDiskon.setVisibility(View.VISIBLE);
                    etDiskonPersen.setVisibility(View.VISIBLE);
                } else {
                    // tidak diceklis, jangan tampilkan etHargaDiskon dan etHargaDiskonPersen
                    etHargaDiskon.setVisibility(View.GONE);
                    etDiskonPersen.setVisibility(View.GONE);
                }
            }
        });

        /*
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
         */

        ivIconProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tampilkan dialog untuk memilih gambar
                tampilDialogAmbilGambar();
            }
        });

        tvKategoriProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pilih kategori
                dialogKategori();
            }
        });

        btnUpdateProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Alur :
                - Input data
                - Validasi data yang diinputkan
                - Update data tersebut ke database
                 */
                inputData();
            }
        });
    }

    private void loadDetailProduk() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(idProduk)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // get data
                        String idProduk = "" + dataSnapshot.child("idProduk").getValue();
                        String namaProduk = "" + dataSnapshot.child("namaProduk").getValue();
                        String deskripsiProduk = "" + dataSnapshot.child("deskripsiProduk").getValue();
                        String kategoriProduk = "" + dataSnapshot.child("kategoriProduk").getValue();
                        String jumlahProduk = "" + dataSnapshot.child("jumlahProduk").getValue();
                        String iconProduk = "" + dataSnapshot.child("iconProduk").getValue();
                        String hargaUtama = "" + dataSnapshot.child("hargaUtama").getValue();
                        String hargaDiskon = "" + dataSnapshot.child("hargaDiskon").getValue();
                        String diskonPersen = "" + dataSnapshot.child("diskonPersen").getValue();
                        String diskonTersedia = "" + dataSnapshot.child("diskonTersedia").getValue();
                        String timeStamp = "" + dataSnapshot.child("timeStamp").getValue();
                        String uid = "" + dataSnapshot.child("uid").getValue();

                        // set data untuk ditampilkan
                        if (diskonTersedia.equals("true")) {
                            diskonSwitch.setChecked(true);

                            etHargaDiskon.setVisibility(View.VISIBLE);
                            etDiskonPersen.setVisibility(View.VISIBLE);
                        } else {
                            diskonSwitch.setChecked(false);

                            etHargaDiskon.setVisibility(View.GONE);
                            etDiskonPersen.setVisibility(View.GONE);
                        }

                        etNamaProduk.setText(namaProduk);
                        etDeskripsiProduk.setText(deskripsiProduk);
                        tvKategoriProduk.setText(kategoriProduk);
                        etDiskonPersen.setText(diskonPersen);
                        etJumlahProduk.setText(jumlahProduk);
                        etHargaUtama.setText(hargaUtama);
                        etHargaDiskon.setText(hargaDiskon);

                        try {
                            Picasso.get().load(iconProduk).placeholder(R.drawable.ic_add_shopping_cart_white).into(ivIconProduk);
                        } catch (Exception e) {
                            ivIconProduk.setImageResource(R.drawable.ic_add_shopping_cart_white);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private String namaProduk, deskripsiProduk, kategoriProduk, banyakProduk, hargaUtama, hargaDiskon, diskonPersen;
    private boolean diskonTersedia = false;
    private void inputData() {
        // input data
        namaProduk = etNamaProduk.getText().toString().trim();
        deskripsiProduk = etDeskripsiProduk.getText().toString().trim();
        kategoriProduk = tvKategoriProduk.getText().toString().trim();
        banyakProduk = etJumlahProduk.getText().toString().trim();
        hargaUtama = etHargaUtama.getText().toString().trim();
        diskonTersedia = diskonSwitch.isChecked();

        // validasi data
        if (TextUtils.isEmpty(namaProduk)){
            etNamaProduk.setError("Nama produk tidak boleh kosong");

            /*
            Toast.makeText(this, "Nama Produk tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            return;
            */
        }
        if (TextUtils.isEmpty(kategoriProduk)){
            tvKategoriProduk.setError("Kategori produk tidak boleh kosong");
        }
        if (TextUtils.isEmpty(hargaUtama)){
            etHargaUtama.setError("Harga produk tidak boleh kosong");
        }
        if (diskonTersedia){
            // produk dengan diskon
            hargaDiskon = etHargaDiskon.getText().toString().trim();
            diskonPersen = etDiskonPersen.getText().toString().trim();
            if (TextUtils.isEmpty(hargaDiskon)){
                etHargaUtama.setError("Harga produk tidak boleh kosong");
            }
        } else {
            // produk tanpa diskon
            hargaDiskon = "0";
            diskonPersen = "";
        }

        updateProduk();
    }

    private void updateProduk() {
        // tampilkan
        progressDialog.setMessage("Memperbarui...");
        progressDialog.show();

        if (image_uri == null) {
            // update tanpa gambar

            // setup data dalam hashmap ke update
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("namaProduk", "" + namaProduk);
            hashMap.put("deskripsiProduk", "" + deskripsiProduk);
            hashMap.put("kategoriProduk", "" + kategoriProduk);
            hashMap.put("jumlahProduk", "" + banyakProduk);
            hashMap.put("hargaUtama", "" + hargaUtama);
            hashMap.put("hargaDiskon", "" + hargaDiskon);
            hashMap.put("diskonPersen", "" + diskonPersen);
            hashMap.put("diskonTersedia", "" + diskonTersedia);

            // update ke database
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(idProduk)
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // berhasil diupdate
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "Berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // gagal diupdate
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // update dengan gambar

            // upload gambar
            // nama dan path gambar di firebase storage
            String pathNamaFile = "product_images/" + "" + idProduk;
            // gambar diupload
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(pathNamaFile);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // gambar diupload, get url dari gambar yg diupload

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                // setup data untuk update
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("iconProduk", "" + downloadImageUri);
                                hashMap.put("namaProduk", "" + namaProduk);
                                hashMap.put("deskripsiProduk", "" + deskripsiProduk);
                                hashMap.put("kategoriProduk", "" + kategoriProduk);
                                hashMap.put("jumlahProduk", "" + banyakProduk);
                                hashMap.put("hargaUtama", "" + hargaUtama);
                                hashMap.put("hargaDiskon", "" + hargaDiskon);
                                hashMap.put("diskonPersen", "" + diskonPersen);
                                hashMap.put("diskonTersedia", "" + diskonTersedia);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Products").child(idProduk)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // berhasil diupdate
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductActivity.this, "Berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // gagal diupdate
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // gagal upload
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void dialogKategori() {
        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kategori Produk")
                .setItems(Constants.kategoriProduk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // pilih kategori
                        String kategori = Constants.kategoriProduk[which];

                        // set kategori yang dipilih
                        tvKategoriProduk.setText(kategori);
                    }
                })
                .show();
    }

    private void tampilDialogAmbilGambar() {
        // opsi untuk menampilkan dialog
        String[] options = {"Kamera", "Galeri"};
        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih gambar dari")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handle item click
                        if (which==0){
                            // kamera dipilih
                            if (cekIzinKamera()){
                                // izin diberikan
                                ambilDariKamera();
                            } else {
                                // tidak diberi izin lakukan request
                                memintaIzinKamera();
                            }
                        } else {
                            // galeri dipilih
                            if (cekIzinPenyimpanan()){
                                // izin diberikan
                                ambilDariGaleri();
                            } else {
                                // tidak diberi izin lakukan request
                                memintaIzinPenyimpanan();
                            }
                        }
                    }
                })
                .show();
    }

    private void ambilDariGaleri(){
        // intent untuk mengambil gambar dari galeri
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void ambilDariKamera(){
        // intent untuk mengambil gambar dari kamera

        // gunakan media penyimpanan untuk mengambil gambar
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"temp_image_title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"temp_image_description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
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
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void memintaIzinKamera(){
        ActivityCompat.requestPermissions(this, izinKamera, CAMERA_REQUEST_CODE);
    }

    // menangani hasil permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean kameraDiterima = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean penyimpananDiterima = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (kameraDiterima && penyimpananDiterima){
                        // kedua izin diberikan
                        ambilDariKamera();
                    } else {
                        // dua atau satu izin ditolak
                        Toast.makeText(this, "Kamera dan Penyimpanan diperlukan!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean penyimpananDiterima = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (penyimpananDiterima){
                        ambilDariGaleri();
                    } else {
                        // izin ditolak
                        Toast.makeText(this, "Penyimpanan diperlukan!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // menangani hasil pengambilan gambar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                // gambar diambil dari gallery

                // simpan uri gambar
                image_uri = data.getData();

                // set gambar
                ivIconProduk.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                // gambar diambil dari kamera

                ivIconProduk.setImageURI(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}