package com.maulanadw.tanamanhias;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

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