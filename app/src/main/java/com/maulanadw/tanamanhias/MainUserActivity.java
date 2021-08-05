package com.maulanadw.tanamanhias;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class MainUserActivity extends AppCompatActivity {

    private RewardedAd mRewardedAd;
    private final String TAG = "--->AdMob<---";

    private TextView tvNama, tvEmail, tvNoHP, tvTabToko, tvTabOrder, tvAdMob;
    private RelativeLayout rlToko, rlOrder;
    private ImageButton btnLogout, btnEditProfil, btnAds;
    private ImageView ivProfil;
    private RecyclerView rvToko;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelShop> shopList;
    private AdapterShop adapterShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        tvNama = findViewById(R.id.tvNama);
        //tvEmail = findViewById(R.id.tvEmail);
        tvNoHP = findViewById(R.id.tvNoHP);
        tvTabToko = findViewById(R.id.tvTabToko);
        tvTabOrder = findViewById(R.id.tvTabOrder);
        //tvAdMob = findViewById(R.id.tvAdMob);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfil = findViewById(R.id.btnEditProfil);
        btnAds = findViewById(R.id.btnAds);
        ivProfil = findViewById(R.id.ivProfil);
        rlToko = findViewById(R.id.rlToko);
        rlOrder = findViewById(R.id.rlOrder);
        rvToko = findViewById(R.id.rvToko);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        cekUser();

        tampilanToko();

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
                startActivity(new Intent(MainUserActivity.this, EditProfileUserActivity.class));
            }
        });

        tvTabToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilanToko();
            }
        });

        tvTabOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilanOrder();
            }
        });

        // Inisialisasi Mobile Ads SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadRewardedAds();
            }
        });

        btnAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedAd();
            }
        });

    }

    // Load a rewarded ad object
    private void loadRewardedAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                        Log.d(TAG, "onAdFailedToLoad");
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");

                        // Set the FullScreenContentCallback
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad was shown.");
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.d(TAG, "Ad failed to show.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad was dismissed.");
                                // mRewardedAd = null;
                                loadRewardedAds();
                            }
                        });
                    }
                });
    }

    // Show the ad
    private void showRewardedAd() {
        if (mRewardedAd != null) {
            Activity activityContext = MainUserActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();

                    int reward = Integer.parseInt(tvAdMob.getText().toString().trim());

                    tvAdMob.setText(String.valueOf(reward + rewardAmount));
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }

    private void tampilanToko() {
        rlToko.setVisibility(View.VISIBLE);
        rlOrder.setVisibility(View.GONE);

        tvTabToko.setTextColor(getResources().getColor(R.color.black));
        tvTabToko.setBackgroundResource(R.drawable.shape_rectangle3);

        tvTabOrder.setTextColor(getResources().getColor(R.color.white));
        tvTabOrder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void tampilanOrder() {
        rlToko.setVisibility(View.GONE);
        rlOrder.setVisibility(View.VISIBLE);

        tvTabToko.setTextColor(getResources().getColor(R.color.white));
        tvTabToko.setBackgroundColor(getResources().getColor(android.R.color.transparent));

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
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cekUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
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
                            // get data user
                            String nama = ""+ds.child("nama").getValue();
                            String email = ""+ds.child("email").getValue();
                            String noHp = ""+ds.child("noHp").getValue();
                            String gambarProfil = ""+ds.child("gambarProfil").getValue();
                            String tipeAkun = ""+ds.child("tipeAkun").getValue();
                            String kota = ""+ds.child("kota").getValue();

                            // set data user
                            tvNama.setText(nama);
                            //tvEmail.setText(email);
                            //tvNoHP.setText(noHp);
                            try {
                                Picasso.get().load(gambarProfil).placeholder(R.drawable.ic_person_gray).into(ivProfil);
                            } catch (Exception e) {
                                ivProfil.setImageResource(R.drawable.ic_person_gray);
                            }

                            // load toko sesuai kota si user
                            loadToko(kota);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadToko(String kotaSaya) {
        // list
        shopList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("tipeAkun").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // clear list sebelum menambahkan
                        shopList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            String kotaToko = "" + ds.child("kota").getValue();

                            // tampilkan toko
                            if (kotaToko.equals(kotaSaya)) {
                                shopList.add(modelShop);
                            }
                            // jika ingin menampilkan semua toko, skip if statement namun tambahkan =
                            //shopList.add(modelShop);
                        }
                        // set up adapter
                        adapterShop = new AdapterShop(MainUserActivity.this, shopList);
                        // set adapter ke recyclerview
                        rvToko.setAdapter(adapterShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}