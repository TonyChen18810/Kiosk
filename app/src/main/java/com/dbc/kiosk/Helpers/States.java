package com.dbc.kiosk.Helpers;

import android.content.Context;
import com.dbc.kiosk.R;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * States.java
 *
 * Keeps track of states and provinces used for the state selection buttons on
 * LoggedIn.java nd CreateAccount.java. The states appear with their full name
 * in the drop-downs, but are submitted through the UpdateShippingTruckDriver.java
 * web service with just their abbreviations.
 */
public class States {

    private static Map<String, String> stateMap;

    public static void setSates(Context context) {
        List<String> states = Arrays.asList(context.getResources().getStringArray(R.array.states));
        List<String> states_abbreviated = Arrays.asList(context.getResources().getStringArray(R.array.states_abbreviated));
        stateMap = new HashMap<>();

        // System.out.println(states.size());
        // System.out.println(states_abbreviated.size());

        for (int i = 0; i < states.size(); i++) {
            stateMap.put(states.get(i), states_abbreviated.get(i));
        }
    }

    public static String getAbbreviatedState(String state) {
        return stateMap.get(state);
    }
}
