package com.timoschesslmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.stitch.core.internal.common.IoUtils;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    JSONArray globalJsonArray = new JSONArray();
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    JSONObject postJsonObject = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void alreadyHaveAccount(View view)
    {
        goToLoginPage();
    }

    public void goToLoginPage()
    {
        Intent intent = new Intent(this,Main3Activity.class);
        startActivity(intent);
    }


    public void createNewAccount(View view) throws JSONException {

        DownloadTask downloadTask = new DownloadTask();

        try {

            downloadTask.execute("https://android-restapi.herokuapp.com/");

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        Log.i("Test:","Button clicked");
        EditText editTextUsername = findViewById(R.id.editText5);
        EditText editTextEmail = findViewById(R.id.editText6);
        EditText editTextPassword = findViewById(R.id.editText7);
        EditText editTextRepeat = findViewById(R.id.editText8);

        String usernameInput = editTextUsername.getText().toString();
        String emailInput = editTextEmail.getText().toString();
        String passwordInput = editTextPassword.getText().toString();
        String repeatInput = editTextRepeat.getText().toString();

        Log.i("Test:",usernameInput);

        if(usernameInput.length()>3)
        {
            if(!checkIfUsernameExists(usernameInput))
            {


                if(validate(emailInput))
                {

                    if(passwordInput.length()>5)
                    {
                        if(passwordInput.equals(repeatInput))
                        {
                            Toast toast = Toast.makeText(this,"Creating a new account....",Toast.LENGTH_LONG);
                            toast.show();

                            postJsonObject.put("username",usernameInput);
                            postJsonObject.put("password",passwordInput);
                            postJsonObject.put("email",emailInput);

                            Log.i("POST:",postJsonObject.toString());

                            PostTask postTask = new PostTask();

                            try {

                                postTask.execute("https://android-restapi.herokuapp.com/");

                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                            sendEmail(emailInput);

                            openSecondActivity();


                        }
                        else
                        {
                            Toast toast = Toast.makeText(this,"The passwords dont match!",Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                    else
                    {
                        Toast toast = Toast.makeText(this,"Please enter at least 6 characters for the password!",Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(this,"The email is not valid!",Toast.LENGTH_SHORT);
                    toast.show();

                }
            }
            else
            {
                Toast toast = Toast.makeText(this,"This username already exists!",Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else
        {
            Toast toast = Toast.makeText(this,"Please enter at least 4 characters for the username!",Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    public void openSecondActivity()
    {
        Intent intent = new Intent(this,Main2Activity.class);
        startActivity(intent);
    }


    public boolean checkIfUsernameExists(String username) throws JSONException {

        Log.i("Global",globalJsonArray.toString());

        for(int i=0;i<globalJsonArray.length() ;i++)
        {
            Log.i("Objects:",globalJsonArray.getJSONObject(i).toString());
            JSONObject userJsonObject = globalJsonArray.getJSONObject(i);

            String usernameOfUserInDatabase = userJsonObject.getString("username");
            Log.i("Usernames:",usernameOfUserInDatabase);

            if(usernameOfUserInDatabase.equals(username))
            {
                return true;
            }

        }

        return false;

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

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public class PostTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {

            String postJsonObjectAsString = postJsonObject.toString();
            Log.i("We want to post:",postJsonObjectAsString);

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                OutputStream outputStream = urlConnection.getOutputStream();

                outputStream.write(postJsonObjectAsString.getBytes("UTF-8"));
                outputStream.close();

                 try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                          StringBuilder response = new StringBuilder();
                          String responseLine = null;
                          while ((responseLine = br.readLine()) != null) {
                              response.append(responseLine.trim());
                          }
                          Log.i("PostResponse:",response.toString());

                }



            } catch (IOException e) {
                e.printStackTrace();
            }



            return null;
        }


    }


    public void sendEmail(String email)
    {
        SendMailTask sendMailTask = new SendMailTask();

        String from ="Timo Schessl Messenger Android App";
        String message ="Registration successful.";

        try{
            sendMailTask.execute("https://timoschessl.com/mailserverAndroid.php?to="+email+"&from="+from+"&message="+message+"");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public class SendMailTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection httpURLConnection = null;

            String result="";

            try {

                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                BufferedReader b = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;

                while ((line = b.readLine()) != null) {
                    stringBuffer.append(line);
                }
                b.close();
                System.out.println(stringBuffer.toString());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return null;

        }
    }




}
