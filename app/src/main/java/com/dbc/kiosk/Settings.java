package com.dbc.kiosk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.dbc.kiosk.Screens.FirstScreen;
import java.lang.ref.WeakReference;
import java.util.Date;
import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;
/**
 * Settings.java
 *
 * @params Activity activity
 *
 * Called from FirstScreen.java when the version number is clicked 3 times.
 *
 * Invokes a fragment to act as a small settings menu, allowing user to
 * change values of cooler location and kiosk number (for future use)
 *
 * Requires password to be entered before allowing any change in settings.
 */
public class Settings extends Fragment {

    private static String coolerLocation;
    private static String kioskNumber;
    private static final String password = "4535";

    private static String errorMsg;
    private static String errorClass;

    private static String errorDate;

    private static SharedPreferences settings;

    private static WeakReference<Activity> mWeakActivity;

    public static String getCoolerLocation() {
        return coolerLocation;
    }

    public static String getKioskNumber() {
        return kioskNumber;
    }

    public static void setCoolerLocation(String location) {
        coolerLocation = location;
    }

    public static void setKioskNumber(String num) {
        kioskNumber = num;
    }

    public static void setError(String error, String errorC, String date, Context context) {
        errorMsg = error;
        errorClass = errorC;
        errorDate = date;
        saveSettings(context);
    }

    private static String getErrorMsg() {
        return errorMsg;
    }

    private static String getErrorClass() {
        return errorClass;
    }

    private static String getErrorDate() {
        return errorDate;
    }

    public Settings(Activity activity) {
        mWeakActivity = new WeakReference<>(activity);
    }

    private static void saveSettings(Context context) {
        if (context == null) {
            context = mWeakActivity.get();
        }
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("cooler_location", coolerLocation);
        editor.putString("kiosk_number", kioskNumber);
        editor.putString("error_msg", getErrorMsg());
        editor.putString("error_class", getErrorClass());
        editor.putString("date", new Date().toString());
        editor.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        getPrefs(getContext());

        TextView errorText = view.findViewById(R.id.ErrorLog);
        TextView errorTitle = view.findViewById(R.id.ErrorTitle);
        TextView settingsText = view.findViewById(R.id.SettingsText);
        TextView versionUpdate = view.findViewById(R.id.VersionUpdate);
        String version = "";
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionUpdate.setText("Version " + version + " updates:");
        errorText.setText(getErrorMsg());
        // errorTitle.setText(errorTitle.getText().toString() + " " + getErrorDate());
        ImageButton exitBtn = view.findViewById(R.id.ExitBtn);
        Button saveBtn = view.findViewById(R.id.SaveBtn);
        Spinner locationCoolerSpinner = view.findViewById(R.id.CoolerLocationSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mWeakActivity.get(), R.array.cooler_locations, android.R.layout.select_dialog_item);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        locationCoolerSpinner.setAdapter(adapter);
        exitBtn.setVisibility(View.INVISIBLE);
        settingsText.setVisibility(View.GONE);
        versionUpdate.setVisibility(View.GONE);

        saveBtn.setOnClickListener(v -> {
            settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("cooler_location", coolerLocation);
            editor.putString("kiosk_number", kioskNumber);
            editor.putString("error_msg", getErrorMsg());
            editor.putString("error_class", getErrorClass());
            editor.putString("error_date", getErrorDate());
            editor.commit();
            FirstScreen.settingsListener.setValue(true);
        });
/*
        exitBtn.setOnClickListener(v -> {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            manager.getBackStackEntryCount();
            transaction.remove(current);
            transaction.commit();
        });
*/
        String[] coolerArray = getResources().getStringArray(R.array.cooler_locations);

        locationCoolerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                coolerLocation = coolerArray[position].trim();
                System.out.println("Cooler location set to: " + getCoolerLocation());
                FirstScreen.settingsListener.setValue(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner kioskNumberSpinner = view.findViewById(R.id.KioskNumberSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.kiosk_numbers, android.R.layout.select_dialog_item);
        adapter2.setDropDownViewResource(android.R.layout.select_dialog_item);
        kioskNumberSpinner.setAdapter(adapter2);

        String[] kioskNumberArray = getResources().getStringArray(R.array.kiosk_numbers);

        kioskNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kioskNumber = kioskNumberArray[position].trim();
                System.out.println("Kiosk number set to: " + getKioskNumber());
                FirstScreen.settingsListener.setValue(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i = 0; i < coolerArray.length; i++) {
            if (coolerArray[i].trim().equals(Settings.getCoolerLocation().trim())) {
                locationCoolerSpinner.setSelection(i);
            }
        }
        for (int i = 0; i < kioskNumberArray.length; i++) {
            if (kioskNumberArray[i].trim().equals(Settings.getKioskNumber().trim())) {
                kioskNumberSpinner.setSelection(i);
            }
        }

        CardView cardView1 = view.findViewById(R.id.cardView1), cardView2 = view.findViewById(R.id.cardView2);
        EditText adminPW = view.findViewById(R.id.AdminPW);
        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
        errorTitle.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        adminPW.setVisibility(View.VISIBLE);
        adminPW.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(adminPW, SHOW_IMPLICIT);
        }
        adminPW.requestFocus();

        adminPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adminPW.getText().toString().equals(password)) {
                    adminPW.setVisibility(View.GONE);
                    cardView1.setVisibility(View.VISIBLE);
                    cardView2.setVisibility(View.VISIBLE);
                    errorText.setVisibility(View.VISIBLE);
                    errorTitle.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.VISIBLE);
                    settingsText.setVisibility(View.VISIBLE);
                    versionUpdate.setVisibility(View.VISIBLE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static void getPrefs(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        coolerLocation = settings.getString("cooler_location", "01");
        kioskNumber = settings.getString("kiosk_number", "01");
    }
}
