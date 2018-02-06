package de.raybit.countingshare;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button buttonSend;
    TextView textView;
    FloatingActionButton _floatingActionButton_addProject;
    Button menuBTN_logout;

    final String scripturl = "raybit.de";
    final String scriptFile = "/app/receive_script.php";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar1);
        setSupportActionBar(myToolbar);

        editText = (EditText) findViewById(R.id.editTextSend);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        textView = (TextView) findViewById(R.id.textViewReceive);
        _floatingActionButton_addProject = (FloatingActionButton) findViewById(R.id.floatingActionButton_addProject);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){
                if (internetAvailable()) {
                    sendToServer(editText.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(), "Internet nicht verfügbar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        _floatingActionButton_addProject.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), ProjectActivity.class);
                //startActivityForResult(intent, 1);
            }

        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, 1);
//TODO Cookie destroy
            //TODO use Logout in PHP
            finish();
            return true;
        }

        if (id == R.id.action_close){
//TODO Cookie destroy
            //TODO use Logout in PHP
            finish();
            System.exit(0);
            return true;
        }
    return super.onOptionsItemSelected(item);
    }
       /*
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
*/
    public void sendToServer(final String text){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String textparam = "text1=" + URLEncoder.encode(text, "UTF-8");


                    URL scripturl = new URL("http", "raybit.de", 80, scriptFile);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();
                    contentWriter.close();

                    InputStream answerInputStream = connection.getInputStream();
                    final String answer = getTextFromInputStream(answerInputStream);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(answer);
                        }
                    });
                    answerInputStream.close();
                    connection.disconnect();

                } catch(MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public String getTextFromInputStream(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringbuilder = new StringBuilder();

        String aktuelleZeile;
        try {
            while ((aktuelleZeile = reader.readLine()) != null) {
                stringbuilder.append(aktuelleZeile);
                stringbuilder.append("\n");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return stringbuilder.toString().trim();
    }


    public boolean internetAvailable(){
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
     }

}
