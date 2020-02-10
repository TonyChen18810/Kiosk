package com.example.kiosk;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

public class MessageSender {
    private static String phoneNum;
    private ArrayList<String> multiMsg;

    public void sendSMS(Context context) {
        multiMsg = new ArrayList<>();
        multiMsg.clear();
        try {
            phoneNum = "8315885534";
            SmsManager smsManager = SmsManager.getDefault();

            switch(MainActivity.getCurrentLanguage()) {
                case 0:
                    multiMsg.add("Congratulations! You have successfully created a check in account at D'Arrigo. You will now be ");
                    multiMsg.add("able to check in with your email and phone number each time you pick up at the facility");
                case 1:
                    multiMsg.add("¡Felicidades! Ha creado con éxito una cuenta de registro en D'Arrigo. Ahora podrá registrarse ");
                    multiMsg.add("con su correo electrónico y número de teléfono cada vez que recoja en la instalación.");
                case 2:
                    multiMsg.add("Toutes nos félicitations! Vous avez réussi à créer un compte d'enregistrement chez D'Arrigo. ");
                    multiMsg.add("Vous pourrez désormais vous enregistrer avec votre adresse e-mail et votre numéro de téléphone ");
                    multiMsg.add("chaque fois que vous récupérez dans l'établissement.");
            }
            smsManager.sendMultipartTextMessage(phoneNum, null, multiMsg, null, null);
            Toast.makeText(context, "SMS sent successfully.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "SMS failed: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
