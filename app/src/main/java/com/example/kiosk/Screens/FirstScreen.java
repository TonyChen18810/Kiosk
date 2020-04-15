package com.example.kiosk.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiosk.Account;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Order;
import com.example.kiosk.R;
import com.example.kiosk.Settings;
import com.example.kiosk.Webservices.GetOrderDetails;
/**
 * FirstScreen.java
 *
 * Initial screen that is shown when app is opened, restarted, or anytime the "Logout"
 * button is used anywhere else in the app
 *
 * Both the "Yes" and "No" button will start MainActivity.java.
 * The "Yes" and "No" buttons dictate whether the confirm email/phone
 * number fields are shown in MainActivity.java
 *
 * Prompts user asking if they have an existing account or not
 */
public class FirstScreen extends AppCompatActivity {

    private String version;
    private View englishCheckbox, spanishCheckbox, frenchCheckbox;
    private TextView appointmentWarningText, existingAccountText, versionText;
    private Button noBtn, yesBtn;

    public static MutableLiveData<Boolean> settingsListener = null;

    public static int settingsClickCount = 0;
    public static FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        setup();

        final Fragment[] settingsFragment = new Fragment[1];
        fm = getSupportFragmentManager();
        final boolean[] fragmentOpen = {false};
        settingsClickCount = 0;
        versionText.setOnClickListener(v -> {
            if (!fragmentOpen[0]) {
                settingsClickCount++;
                if (settingsClickCount == 3) {
                     settingsFragment[0] = new Settings(FirstScreen.this);
                    settingsClickCount = 0;
                    System.out.println("Backstack count: " + fm.getBackStackEntryCount());
                    if (!fragmentOpen[0]) {
                        fragmentOpen[0] = true;
                        // fm.beginTransaction().setCustomAnimations(R.anim.fragment_close_enter, R.anim.fragment_close_exit).add(R.id.placeholder, settingsFragment, settingsFragment.getClass().getSimpleName()).addToBackStack(null).commit();
                        fm.beginTransaction().setCustomAnimations(R.anim.layout_slide_in, R.anim.fragment_close_exit).replace(R.id.placeholder, settingsFragment[0]).commit();
                    }
                }
            }
            System.out.println("Settings click counter: " + settingsClickCount);
        });

        settingsListener = new MutableLiveData<>();
        settingsListener.observe(FirstScreen.this, savedIsClicked -> {
            if (savedIsClicked) {
                fragmentOpen[0] = false;
                fm.beginTransaction().setCustomAnimations(R.anim.layout_slide_in, R.anim.fragment_close_exit).remove(settingsFragment[0]).commit();
                Toast.makeText(FirstScreen.this, "Settings have been saved", Toast.LENGTH_SHORT).show();
                if (Language.getCurrentLanguage() == 0) {
                    setChecked(spanishCheckbox, frenchCheckbox, englishCheckbox);
                } else if (Language.getCurrentLanguage() == 1) {
                    setChecked(englishCheckbox, frenchCheckbox, spanishCheckbox);
                } else if (Language.getCurrentLanguage() == 2) {
                    setChecked(spanishCheckbox, englishCheckbox, frenchCheckbox);
                }
            } else {
                if (Language.getCurrentLanguage() == 0) {
                    setChecked(spanishCheckbox, frenchCheckbox, englishCheckbox);
                } else if (Language.getCurrentLanguage() == 1) {
                    setChecked(englishCheckbox, frenchCheckbox, spanishCheckbox);
                } else if (Language.getCurrentLanguage() == 2) {
                    setChecked(spanishCheckbox, englishCheckbox, frenchCheckbox);
                }
            }
        });

        // clicked checkbox
        englishCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, frenchCheckbox, englishCheckbox);
            return true;
        });

        // clicked text next to check box
        findViewById(R.id.EnglishText).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, frenchCheckbox, englishCheckbox);
            return true;
        });

        // clicked checkbox
        spanishCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(frenchCheckbox, englishCheckbox, spanishCheckbox);
            return true;
        });

        // clicked text next to check box
        findViewById(R.id.SpanishText).setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(frenchCheckbox, englishCheckbox, spanishCheckbox);
            return true;
        });

        // clicked checkbox
        frenchCheckbox.setOnTouchListener((v, event) -> {
            v.performClick();
            setChecked(spanishCheckbox, englishCheckbox, frenchCheckbox);
            return true;
        });

        // clicked text next to check box
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

    /**
     * @param checkBox
     * use this function to check the custom language checkboxes
     * the last checkbox passed as a parameter is the one to be checked
     * all others are unchecked
     */
    public void setChecked(View... checkBox) {
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

    /**
     * Used to reset values if necessary and initialize all UI variables & version number
     */
    public void setup() {
        System.out.println("Reset values...");
        Account.setCurrentAccount(null);
        Order.reset();
        GetOrderDetails.setNewMasterNumber(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        try {
            PackageInfo pInfo = FirstScreen.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Settings.setError(e.toString(), getClass().toString(), FirstScreen.this);
        }
        versionText = findViewById(R.id.VersionText);
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

    /**
     * @param currentLanguage
     * changes UI text based on current language int
     * 0 = English, 1 = Spanish, 2 = French
     * Called from setChecked()
     */
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