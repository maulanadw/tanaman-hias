package com.maulanadw.tanamanhias;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AddProductActivity extends AppCompatActivity {

    private ImageButton btnKembali;
    private ImageView ivIconProduk;
    private EditText etJudulProduk, etDeskripsiProduk;
    private TextView tvKategoriProduk, etBerat, etHarga, etHargaDiskon, etHargaDiskonPersen;
    private SwitchCompat diskonSwitch;
    private Button btnTambah;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        btnKembali = findViewById(R.id.btnKembali);
        ivIconProduk = findViewById(R.id.ivIconProduk);
        etJudulProduk = findViewById(R.id.etJudulProduk);
        etDeskripsiProduk = findViewById(R.id.etDeskripsiProduk);
        tvKategoriProduk = findViewById(R.id.tvKategoriProduk);
        etBerat = findViewById(R.id.etBerat);
        etHarga = findViewById(R.id.etHarga);
        diskonSwitch = findViewById(R.id.diskonSwitch);
        etHargaDiskon = findViewById(R.id.etHargaDiskon);
        etHargaDiskonPersen = findViewById(R.id.etHargaDiskonPersen);
        btnTambah = findViewById(R.id.btnTambah);

        // inisialisasi permission arrays
        izinKamera = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        izinPenyimpanan = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ivIconProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tampilkan dialog untuk memilih gambar
                tampilDialogPilihGambar();
            }
        });
    }

    private void tampilDialogPilihGambar() {
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
}
