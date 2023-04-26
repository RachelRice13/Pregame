package com.example.pregame.Common;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public static boolean validateBlank(String text, TextInputLayout layout) {
        if (text.isEmpty()) {
            layout.setError("This is Required");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validImage(Uri uri, View view) {
        if (uri == null) {
            Snackbar.make(view, "Choose a profile picture", Snackbar.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static boolean validatePhone(String phone, TextInputLayout phoneLO) {
        if (phone.isEmpty()) {
            phoneLO.setError("This is Required");
            return false;
        } else {
            if (phone.length() == 10) {
                phoneLO.setError(null);
                return true;
            } else {
                phoneLO.setError("This is not a valid number");
                return false;
            }
        }
    }

    public static boolean validatePassword(String password, TextInputLayout passwordLO) {
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
        Matcher matcher = pattern.matcher(password);
        if (password.isEmpty()) {
            passwordLO.setError("This is Required");
            return false;
        } else {
            if (!matcher.matches()) {
                passwordLO.setError("Password must be at least 8 characters and contain both uppercase and lowercase characters/numbers/special characters");
                return false;
            } else {
                passwordLO.setError(null);
                return true;
            }
        }
    }

    public static boolean validateDate(String date) {
        if (date == null) {
            Log.e("DATE", "Date is required");
            return false;
        } else {
            return true;
        }
    }

    public static boolean validateNumberOfPhotosVideos(List<Uri> imagesUris, List<Uri> videosUris, Context context) {
        if (imagesUris.size() == 0 && videosUris.size() == 0) {
            Toast.makeText(context, "You must select some photos or videos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean validateString(String string) {
        return !string.isEmpty();
    }

    public static boolean validateLengthOfInjury(String lengthOfInjury) {
        Pattern pattern = Pattern.compile("^\\d+\\s[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(lengthOfInjury);
        return matcher.matches();
    }

    public static boolean validateSeenPhysio(String seenPhysio) {
        return !seenPhysio.equals("Nothing Selected");
    }
}
