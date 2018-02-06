package de.raybit.countingshare;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Looper.*;

public class PasswordResetActivity extends AppCompatActivity {
    @BindView(R.id.link_back) TextView _linkBack;
    @BindView(R.id.btn_passwordReset) Button _btn_passwordReset;
    @BindView(R.id.editText_passwordReset_email) EditText editText_text_eMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        ButterKnife.bind(this);

        editText_text_eMail.setFocusableInTouchMode(true);
        editText_text_eMail.requestFocus();

        final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInputFromWindow(editText_text_eMail.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);



        _linkBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, 1);
                finish();
            }
        });

        _btn_passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                new Thread(new Runnable() {


                    @Override
                    public void run() {
                        Looper.prepare();
                        String eMail = editText_text_eMail.getText().toString();
                        PasswordResetCom _PRC = new PasswordResetCom();
                        Map<String, Object> answer = new HashMap<String, Object>();
                        answer = _PRC.sendToServer(eMail);
                        Boolean error = (Boolean) answer.get("error");

                        if (!error) onSuccess(answer.get("message").toString());
                        else onFailure(answer.get("message").toString());
                        Looper.loop();
                    }

                }).start();

            }
        });
    }

    public void onSuccess(String msg){
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, 1);

        finish();
    }

    public void onFailure(String msg){
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }
}
