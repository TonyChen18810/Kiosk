package com.dbc.kiosk.Screens;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.dbc.kiosk.Account;
import com.dbc.kiosk.Helpers.Language;
import com.dbc.kiosk.Helpers.Time;
import com.dbc.kiosk.Order;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Report;
import com.dbc.kiosk.Settings;
import com.dbc.kiosk.Webservices.GetOrderDetails;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

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
    private CheckBox englishCheckbox, spanishCheckbox, frenchCheckbox;
    private TextView appointmentWarningText, existingAccountText, versionText;
    private Button noBtn, yesBtn;

    public static MutableLiveData<Boolean> settingsListener = null;

    public static int settingsClickCount = 0;
    public static FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
        setContentView(R.layout.activity_first_screen);
        // Initialize Firebase Crashlytics, set tags
        Report report = new Report(FirstScreen.this);
        System.out.println(FirebaseInstanceId.getInstance().getInstanceId());

        setup();

        // load settings (kiosk name/number and cooler location)
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FirstScreen.this);
        Settings.setKioskNumber(settings.getString("kiosk_number", "01"));
        Settings.setCoolerLocation(settings.getString("cooler_location", "01"));
        Settings.setDbcUrl(settings.getString("DBC_URL", "http://vmiis/DBCWebService/DBCWebService.asmx")); // def value is production
        System.out.println("Kiosk number: " + Settings.getKioskNumber());
        System.out.println("Cooler location: " + Settings.getCoolerLocation());
        System.out.println("System mode: " + Settings.getDbcUrl());

        // settings page - opens if version number is clicked 3 times
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
                        fm.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.placeholder, settingsFragment[0]).commit();
                    }
                }
            }
            System.out.println("Settings click counter: " + settingsClickCount);
        });

        // listens for "save" button click in settings menu
        settingsListener = new MutableLiveData<>();
        settingsListener.observe(FirstScreen.this, savedIsClicked -> {
            if (savedIsClicked) {
                fragmentOpen[0] = false;
                fm.beginTransaction().setCustomAnimations(R.anim.layout_slide_in, R.anim.exit_to_left).remove(settingsFragment[0]).commit();
                Toast.makeText(FirstScreen.this, "Settings have been saved", Toast.LENGTH_SHORT).show();
                Toast.makeText(FirstScreen.this, "Cooler location set to: " + Settings.getCoolerLocation(), Toast.LENGTH_SHORT).show();
                Toast.makeText(FirstScreen.this, "Kiosk number set to: " + Settings.getKioskNumber(), Toast.LENGTH_SHORT).show();
                if (Settings.getDbcUrl().equals("http://vmiis/DBCWebService/DBCWebService.asmx")) {
                    Toast.makeText(FirstScreen.this, "App environment set to: PRODUCTION", Toast.LENGTH_SHORT).show();
                    System.out.println("production");
                } else if (Settings.getDbcUrl().equals("http://VMSQLTEST/DBCWebService/DBCWebService.asmx")) {
                    Toast.makeText(FirstScreen.this, "App environment set to: TEST", Toast.LENGTH_SHORT).show();
                    System.out.println("test");
                }
            }
        });

        englishCheckbox.setOnClickListener(v -> handleChecks(englishCheckbox));

        spanishCheckbox.setOnClickListener(v -> handleChecks(spanishCheckbox));

        frenchCheckbox.setOnClickListener(v -> handleChecks(frenchCheckbox));

        yesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FirstScreen.this, MainActivity.class);
            intent.putExtra("accountStatus", "exists");
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        });

        noBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FirstScreen.this, MainActivity.class);
            intent.putExtra("accountStatus", "new");
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        });
    }

    int b = 0;
    public void handleChecks(CheckBox cb) {
        if ((Language.getCurrentLanguage() == 1) && (cb.getId() == R.id.EnglishCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if (Language.getCurrentLanguage() == 2 && (cb.getId() == R.id.SpanishCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        } else if (Language.getCurrentLanguage() == 3 && (cb.getId() == R.id.FrenchCheckbox)) {
            if (++b == 1) {
                cb.performClick();
            }
        }
        if (cb.getId() == R.id.EnglishCheckbox) {
            textFadeStart();
            englishCheckbox.setClickable(false);
            changeLanguage(1);
            if (spanishCheckbox.isChecked()) {
                spanishCheckbox.toggle();
                spanishCheckbox.setClickable(true);
            }
            if (frenchCheckbox.isChecked()) {
                frenchCheckbox.toggle();
                frenchCheckbox.setClickable(true);
            }
            textFadeEnd();
        }
        if (cb.getId() == R.id.SpanishCheckbox) {
            textFadeStart();
            spanishCheckbox.setClickable(false);
            changeLanguage(2);
            if (englishCheckbox.isChecked()) {
                englishCheckbox.toggle();
                englishCheckbox.setClickable(true);
            }
            if (frenchCheckbox.isChecked()) {
                frenchCheckbox.toggle();
                frenchCheckbox.setClickable(true);
            }
            textFadeEnd();
        }
        if (cb.getId() == R.id.FrenchCheckbox) {
            textFadeStart();
            frenchCheckbox.setClickable(false);
            changeLanguage(3);
            if (englishCheckbox.isChecked()) {
                englishCheckbox.toggle();
                englishCheckbox.setClickable(true);
            }
            if (spanishCheckbox.isChecked()) {
                spanishCheckbox.toggle();
                spanishCheckbox.setClickable(true);
            }
            textFadeEnd();
        }
    }

    public void textFadeStart() {
        TextView[] textArray = {appointmentWarningText, existingAccountText, noBtn, yesBtn};
        AlphaAnimation ani = new AlphaAnimation(1.0f, 0.2f);
        ani.setDuration(500);
        for (TextView text : textArray) {
            text.startAnimation(ani);
        }
    }

    public void textFadeEnd() {
        TextView[] textArray = {appointmentWarningText, existingAccountText, noBtn, yesBtn};
        AlphaAnimation ani = new AlphaAnimation(0.2f, 1.0f);
        ani.setDuration(500);
        for (TextView text : textArray) {
            text.startAnimation(ani);
        }
    }

    /**
     * Used to reset values if necessary and initialize all UI variables & version number
     */
    public void setup() {
        System.out.println("Reset values...");
        Account.setCurrentAccount(null);
        Order.reset();
        GetOrderDetails.setNewMasterNumber(null);
        Time.resetTimeAndDate();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        try {
            PackageInfo pInfo = FirstScreen.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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

        if (Language.getCurrentLanguage() == 1) {
            englishCheckbox.performClick();
        } else if (Language.getCurrentLanguage() == 2) {
            spanishCheckbox.performClick();
        } else if (Language.getCurrentLanguage() == 3) {
            frenchCheckbox.performClick();
        }

        yesBtn.setEnabled(true);
        noBtn.setEnabled(true);
        changeLanguage(Language.getCurrentLanguage());
    }

    /**
     * @param currentLanguage
     * changes UI text based on current language int
     * 0 = English, 1 = Spanish, 2 = French
     * Called from setChecked()
     */
    public void changeLanguage(int currentLanguage) {
        Language.setCurrentLanguage(currentLanguage);
        if (currentLanguage == 1) {
            appointmentWarningText.setText(R.string.appt_required_eng);
            existingAccountText.setText(R.string.existing_account_eng);
            noBtn.setText(R.string.no_eng);
            yesBtn.setText(R.string.yes_eng);
        } else if (currentLanguage == 2) {
            appointmentWarningText.setText(R.string.appt_required_sp);
            existingAccountText.setText(R.string.existing_account_sp);
            noBtn.setText(R.string.no_sp);
            yesBtn.setText(R.string.yes_sp);

        } else if ((currentLanguage == 3)) {
            appointmentWarningText.setText(R.string.appt_required_fr);
            existingAccountText.setText(R.string.existing_account_fr);
            noBtn.setText(R.string.no_fr);
            yesBtn.setText(R.string.yes_fr);
        }
    }
}