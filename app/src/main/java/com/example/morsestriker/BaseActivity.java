package com.example.morsestriker;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Keep the setContentView call in each Activity as it is.
    }

    protected void setupActionBarButtons() {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;

        // 기존 액션바 타이틀 숨김
        actionBar.setDisplayShowTitleEnabled(false);

        // reate a custom view using a RelativeLayout.
        RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Morse Striker title
        TextView title = new TextView(this);
        title.setText("Morse Striker");
        title.setTextColor(getResources().getColor(android.R.color.white));
        title.setTextSize(18f);

        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        titleParams.addRule(RelativeLayout.CENTER_VERTICAL);
        title.setLayoutParams(titleParams);

        layout.addView(title);

        // Setting button  (Immediately to the right of the title)
        ImageButton settingsBtn = new ImageButton(this);
        settingsBtn.setImageResource(R.drawable.settings);
        settingsBtn.setBackground(null);
        settingsBtn.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        RelativeLayout.LayoutParams settingsParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        settingsParams.setMarginStart((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics()));
        settingsBtn.setLayoutParams(settingsParams);


        layout.addView(settingsBtn);

        // Back button (far right end of the ActionBar)
        ImageButton backBtn = new ImageButton(this);
        backBtn.setImageResource(R.drawable.arrowback);
        backBtn.setBackground(null);
        backBtn.setOnClickListener(v -> finish());

        RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        backParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        backParams.addRule(RelativeLayout.CENTER_VERTICAL);
        backBtn.setLayoutParams(backParams);

        layout.addView(backBtn);

        // Apply a custom view to the ActionBar.
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(layout);
    }
}
