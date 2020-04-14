package com.example.kiosk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.example.kiosk.Helpers.Time;
import com.example.kiosk.Screens.FirstScreen;

import java.lang.ref.WeakReference;
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
    private static final String password = "0000";

    public static SharedPreferences settings;

    private static WeakReference<Activity> mWeakActivity;

    private static String getCoolerLocation() {
        return coolerLocation;
    }

    private static String getKioskNumber() {
        return kioskNumber;
    }

    public Settings(Activity activity) {
        mWeakActivity = new WeakReference<>(activity);
    }

    public static void saveSettings(Context context) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("cooler_location", coolerLocation);
        editor.putString("kiosk_number", kioskNumber);
        editor.putString("error_msg", Time.getErrorMsg());
        editor.putString("error_class", Time.getErrorClass());
        editor.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        getPrefs();

        TextView errorText = view.findViewById(R.id.ErrorLog);
        TextView errorTitle = view.findViewById(R.id.ErrorTitle);
        TextView errorClass = view.findViewById(R.id.ErrorClass);
        errorText.setText(Time.getErrorMsg());
        errorClass.setText(Time.getErrorClass());
        ImageButton exitBtn = view.findViewById(R.id.ExitBtn);
        Button saveBtn = view.findViewById(R.id.SaveBtn);
        Spinner locationCoolerSpinner = view.findViewById(R.id.CoolerLocationSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.cooler_locations, android.R.layout.select_dialog_item);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        locationCoolerSpinner.setAdapter(adapter);
        Fragment current = this;
        exitBtn.setVisibility(View.INVISIBLE);

        saveBtn.setOnClickListener(v -> {
            FirstScreen.settingsClickCount = 0;
            System.out.println("settings click count: " + FirstScreen.settingsClickCount);
            settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("cooler_location", coolerLocation);
            editor.putString("kiosk_number", kioskNumber);
            editor.putString("error_msg", Time.getErrorMsg());
            editor.putString("error_class", Time.getErrorClass());
            editor.commit();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_close_enter, R.anim.fragment_close_exit).remove(current);
            transaction.commit();
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
                coolerLocation = coolerArray[position];
                System.out.println("Cooler location set to: " + getCoolerLocation());
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
                kioskNumber = kioskNumberArray[position];
                System.out.println("Kiosk number set to: " + getKioskNumber());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i = 0; i < coolerArray.length; i++) {
            if (coolerArray[i].equals(Settings.getCoolerLocation())) {
                locationCoolerSpinner.setSelection(i);
            }
        }
        for (int i = 0; i < kioskNumberArray.length; i++) {
            if (kioskNumberArray[i].equals(Settings.getKioskNumber())) {
                kioskNumberSpinner.setSelection(i);
            }
        }

        CardView cardView1 = view.findViewById(R.id.cardView1), cardView2 = view.findViewById(R.id.cardView2);
        EditText adminPW = view.findViewById(R.id.AdminPW);
        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
        errorTitle.setVisibility(View.GONE);
        errorClass.setVisibility(View.GONE);
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
                    errorClass.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.VISIBLE);
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

    void getPrefs() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        coolerLocation = settings.getString("cooler_location", "0");
        kioskNumber = settings.getString("kiosk_number", "0");
        Time.setError(settings.getString("error_msg", "0"), settings.getString("error_class", "0"));
    }
}
