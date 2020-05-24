package com.timoschesslmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Main4Activity extends AppCompatActivity {

    JSONArray globalJsonArray=new JSONArray();
    ListView listView;
    ArrayAdapter arrayAdapter;
    ArrayList<String> users = new ArrayList<>();
    ArrayList<String> emails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        listView= findViewById(R.id.listView);

        DownloadTask downloadTask = new DownloadTask();

        try{
            downloadTask.execute("https://android-restapi.herokuapp.com/");
        }
        catch(Exception e)
            {
            e.printStackTrace();
        }

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,users);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),Main5Activity.class);
                intent.putExtra("email",emails.get(position));

                startActivity(intent);

            }
        });

    }


    public void goBackClicked(View view)
    {
        goBackToLoginPage();
    }

    public void goBackToLoginPage()
    {
        Intent intent = new Intent(this,Main3Activity.class);
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

                for(int i=0; i< globalJsonArray.length();i++)
                {
                    try {
                        JSONObject jsonObjectOfUser = globalJsonArray.getJSONObject(i);
                        String username = jsonObjectOfUser.get("username").toString();
                        String email = jsonObjectOfUser.get("email").toString();
                        users.add(username);
                        emails.add(email);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                inputStreamReader.close();
                inputStream.close();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonArray.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            arrayAdapter.notifyDataSetChanged();
        }


    }


}
