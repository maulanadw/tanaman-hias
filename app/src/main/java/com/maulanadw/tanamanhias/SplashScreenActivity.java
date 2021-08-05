package com.maulanadw.tanamanhias;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Buat menjadi fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();

        //
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user==null){
                    // user tidak/ belum login pada login activity
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    // user login, cek jenis pengguna(seller atau user)
                    cekTipeUser();
                }
            }
        }, 2000);
    }

    private void cekTipeUser() {
        // jika pengguna merupakan seller, arahkan tampilan seller
        // jika pengguna merupakan user/ pengguna/ pembeli, arahkan tampilan user

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String tipeAkun = "" + dataSnapshot.child("tipeAkun").getValue();
                        if (tipeAkun.equals("Seller")){
                            // user merupakan seller, arahkan ke MainSellerActivity.class
                            startActivity(new Intent(SplashScreenActivity.this, MainSellerActivity.class));
                            finish();
                        }
                        else {
                            // user merupakan user/ pengguna/ pembeli, arahkan ke MainUserActivity.class
                            startActivity(new Intent(SplashScreenActivity.this, MainUserActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}