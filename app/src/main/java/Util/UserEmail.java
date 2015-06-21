package Util;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.theconnoisseur.R;

import Database.ConnoisseurDatabase;

/**
 * Code used for analysing an edittext for user email entry - to be used when asking for user email (buying process...)
 */
public class UserEmail {
//    mEditEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            if(!hasFocus) {
//                String input = ((EditText) v).getText().toString();
//                if(input.isEmpty()) { return; }
//                boolean unique = ConnoisseurDatabase.getInstance().getLoginTable().isEmailUnique(input);
//                boolean valid = ConnoisseurDatabase.getInstance().getLoginTable().isEmailUnique(input);
//
//                if(unique && valid) {
//                    mFeedbackEmail.setImageResource(R.drawable.greentick);
//                } else {
//                    mFeedbackEmail.setImageResource(R.drawable.redcross);
//                    if (!unique) {
//                        ToastHelper.toast(getApplicationContext(), getString(R.string.not_unique_email), Toast.LENGTH_LONG);
//                    }
//                    if(!valid) {
//                        ToastHelper.toast(getApplicationContext(), getString(R.string.invalid_email), Toast.LENGTH_LONG);
//                    }
//                }
//                mFeedbackEmail.setVisibility(View.VISIBLE);
//            } else {
//                mFeedbackEmail.setVisibility(View.INVISIBLE);
//            }
//        }
//    });
}
