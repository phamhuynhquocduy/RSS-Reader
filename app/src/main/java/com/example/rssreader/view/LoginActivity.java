package com.example.rssreader.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.rssreader.R;
import com.example.rssreader.adapter.IntroFragmentAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    public static int RC_SIGN_IN = 123;
    private Button buttonGoogle;
    public static GoogleSignInAccount account;
    private ViewPager2 viewPager;
    private IntroFragmentAdapter introFragmentAdapter;
    private ProgressDialog mProgress;
    private DotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ViewPager
        viewPager = findViewById(R.id.pager);
        introFragmentAdapter = new IntroFragmentAdapter(this);
        viewPager.setAdapter(introFragmentAdapter);
        dotsIndicator = findViewById(R.id.dots_indicator);
        dotsIndicator.setViewPager2(viewPager);

        //Progress dialog
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Đang tải...");
        mProgress.setMessage("Xin chờ một chút...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        logOut();

        buttonGoogle = findViewById(R.id.buttonGoogle);
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityIfNeeded(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In success
                mProgress.dismiss();
                account = task.getResult(ApiException.class);
                saveLoginState(String.valueOf(account.getIdToken()));
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("name", account.getFamilyName());
                intent.putExtra("image", account.getPhotoUrl());
                intent.putExtra("email", account.getEmail());
                startActivity(intent);
                finish();
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
            } catch (ApiException e) {
                // Google Sign In fail
                Log.d("FailConnect", e.getMessage());
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void logOut() {
        account = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
        SharedPreferences sharedpreferences = getSharedPreferences("Status login", Context.MODE_PRIVATE);
        String token = sharedpreferences.getString("Google", null);
        //User want logout
        if (token != null) {
            // Keep login
            Log.d("LogOut", "KeepAccount");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("name", account.getFamilyName());
            intent.putExtra("image",account.getPhotoUrl());
            intent.putExtra("email", account.getEmail());
            startActivity(intent);
            finish();
        } else {
            // Log out
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("LogOut", "Logout");
                        }
                    });
        }
    }

    private void saveLoginState(String id_token) {
        SharedPreferences sharedpreferences = getSharedPreferences("Status login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Google", id_token);
        editor.commit();

    }
}