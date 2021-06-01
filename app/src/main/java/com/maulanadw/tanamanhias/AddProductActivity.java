package com.maulanadw.tanamanhias;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.net.Uri;
import android.os.Bundle;
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

    // izin dalam arrays
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
    }
}