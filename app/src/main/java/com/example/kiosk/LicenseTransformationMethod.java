package com.example.kiosk;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

public class LicenseTransformationMethod extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;
        PasswordCharSequence(CharSequence source) {
            mSource = source;
        }
        public char charAt(int index) {
            if (index < 5) {
                return '*';
            } else {
                return mSource.charAt(index);
            }
        }
        public int length() {
            return mSource.length();
        }
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end - 3);
        }
    }
}
