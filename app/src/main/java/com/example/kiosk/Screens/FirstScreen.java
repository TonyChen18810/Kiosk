package com.example.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.kiosk.Helpers.Language;
import com.example.kiosk.R;

public class FirstScreen extends AppCompatActivity {

    private String version;
    private View englishCheckbox, spanishCheckbox, frenchCheckbox;
    private TextView appointmentWarningText, existingAccountText;
    private Button noBtn, yesBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        setup();

        englishCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, frenchCheckbox, englishCheckbox);
            return true;
        });

        findViewById(R.id.EnglishText).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, frenchCheckbox, englishCheckbox);
            return true;
        });

        spanishCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(frenchCheckbox, englishCheckbox, spanishCheckbox);
            return true;
        });

        findViewById(R.id.SpanishText).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(frenchCheckbox, englishCheckbox, spanishCheckbox);
            return true;
        });

        frenchCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, englishCheckbox, frenchCheckbox);
            return true;
        });

        findViewById(R.id.FrenchText).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, englishCheckbox, frenchCheckbox);
            return true;
        });

        yesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FirstScreen.this, MainActivity.class);
            intent.putExtra("accountStatus", "exists");
            startActivity(intent);
            if (Language.getCurrentLanguage() == 0) {
                englishCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
            } else if (Language.getCurrentLanguage() == 1) {
                spanishCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
            } else if (Language.getCurrentLanguage() == 2) {
                frenchCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
            }
        });

        noBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FirstScreen.this, MainActivity.class);
            intent.putExtra("accountStatus", "new");
            startActivity(intent);
            if (Language.getCurrentLanguage() == 0) {
                englishCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
            } else if (Language.getCurrentLanguage() == 1) {
                spanishCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
            } else if (Language.getCurrentLanguage() == 2) {
                frenchCheckbox.setBackgroundResource(R.drawable.checkbox_filler);
            }
        });
    }

    public void screenTapped(View view) {
        System.out.println("tapped");
    }

    private void setChecked(View... checkBox) {
        for (int i = 0; i < checkBox.length; i++) {
            if (i == checkBox.length-1) {
                checkBox[i].setPressed(true);
            } else {
                checkBox[i].setPressed(false);
            }
        }
        if (checkBox[checkBox.length-1] == englishCheckbox) {
            Language.setCurrentLanguage(0);
        } else if (checkBox[checkBox.length-1] == spanishCheckbox) {
            Language.setCurrentLanguage(1);
        } else if (checkBox[checkBox.length-1] == frenchCheckbox) {
            Language.setCurrentLanguage(2);
        }
        changeLanguage(Language.getCurrentLanguage());
    }

    public void setup() {
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        try {
            PackageInfo pInfo = FirstScreen.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView versionText = findViewById(R.id.VersionText);
        versionText.setText("Version: " + version);

        appointmentWarningText = findViewById(R.id.AppointmentText);
        existingAccountText = findViewById(R.id.ExistingAccountText);
        noBtn = findViewById(R.id.NoBtn);
        yesBtn = findViewById(R.id.YesBtn);
        englishCheckbox = findViewById(R.id.EnglishCheckbox);
        spanishCheckbox = findViewById(R.id.SpanishCheckbox);
        frenchCheckbox = findViewById(R.id.FrenchCheckbox);

        setChecked(spanishCheckbox, frenchCheckbox, englishCheckbox);
        yesBtn.setEnabled(true);
        noBtn.setEnabled(true);
    }

    public void changeLanguage(int currentLanguage) {
        if (currentLanguage == 0) {
            appointmentWarningText.setText(R.string.appt_required_eng);
            existingAccountText.setText(R.string.existing_account_eng);
            noBtn.setText(R.string.no_eng);
            yesBtn.setText(R.string.yes_eng);
        } else if (currentLanguage == 1) {
            appointmentWarningText.setText(R.string.appt_required_sp);
            existingAccountText.setText(R.string.existing_account_sp);
            noBtn.setText(R.string.no_sp);
            yesBtn.setText(R.string.yes_sp);

        } else if ((currentLanguage == 2)) {
            appointmentWarningText.setText(R.string.appt_required_fr);
            existingAccountText.setText(R.string.existing_account_fr);
            noBtn.setText(R.string.no_fr);
            yesBtn.setText(R.string.yes_fr);
        }
    }
}