package com.timoschesslmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main3Activity extends AppCompatActivity {

    JSONArray globalJsonArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }


    public void goBackClicked(View view)
    {
        goToCreateNewAccountPage();
    }

    public void goToCreateNewAccountPage()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void loginClicked(View view) throws JSONException {

        EditText editTextUsername = findViewById(R.id.editText3);
        EditText editTextPassword = findViewById(R.id.editText4);

        String usernameInput = editTextUsername.getText().toString();
        String passwordInput = editTextPassword.getText().toString();

        DownloadTask downloadTask = new DownloadTask();

        try{
            downloadTask.execute("https://timoschessl-mail.netlify.app/.netlify/functions/server");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        boolean loginSuccess = false;

        for(int i=0;i<globalJsonArray.length();i++)
        {
            JSONObject jsonObject = globalJsonArray.getJSONObject(i);
            String usernameOfUserInDatabase = jsonObject.get("username").toString();
            String passwordOfUserInDatabase = jsonObject.get("password").toString();

            if(usernameOfUserInDatabase.equals(usernameInput)&&passwordOfUserInDatabase.equals(passwordInput))
            {
                loginSuccess =true;
                goToLoginSuccessPage();
            }

        }

        if(!loginSuccess)
        {
        Toast toast = Toast.makeText(this,"Login failed!",Toast.LENGTH_SHORT);
        toast.show();
        }

    }

    public void goToLoginSuccessPage()
    {
        Intent intent = new Intent(this,Main4Activity.class);
        startActivity(intent);
    }


    public class DownloadTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {

            JSONArray jsonArray = new JSONArray();

            URL url;
            HttpURLConnection urlConnection = null;

            String result = "";

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }

                jsonArray = new JSONArray(result);
                globalJsonArray= new JSONArray(result);
                Log.i("Json:", jsonArray.toString());

                inputStreamReader.close();
                inputStream.close();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonArray.toString();
        }


    }
}
