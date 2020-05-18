package com.timoschesslmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main5Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
    }

    public void sendMessage(View view)
    {
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        Log.i("Test:",email);

        String fromInput = "Timo Schessl Messenger";

        EditText editTextMessage = findViewById(R.id.editText2);
        String messageInput = editTextMessage.getText().toString();

        if(messageInput.length()>9)
        {
            Toast toast = Toast.makeText(this,"Sending message......",Toast.LENGTH_SHORT);
            toast.show();

            SendMailTask task = new SendMailTask();

            try {

                task.execute("https://mailserver.timoschessl.com/index.php?to="+email+"&from="+fromInput+"&message="+messageInput+"");

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast toast = Toast.makeText(this,"Please type in at least 10 characters!",Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    public void goBack(View view)
    {
        Intent intent = new Intent(this,Main4Activity.class);
        startActivity(intent);
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
