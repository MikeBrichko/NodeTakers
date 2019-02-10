package com.example.william.nodatracker;


import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private String key ="AIzaSyDGJx0eC5Pum5LyZqCrhmRaoeRwQLmj5R4";

    private String url ="https://vision.googleapis.com/v1/images:annotate?key=";

    private String base_ngkok = "https://1a4dde59.ngrok.io";

    private String url_sub = base_ngkok + "/postPic";

    private MqttClient mqttClient = null;
    private MqttConnectOptions connOpts = null;

    private RequestQueue queue;

    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        dispatchTakePictureIntent();

        editText = (EditText)findViewById(R.id.editText);

        final Button button = (Button) findViewById(R.id.retake_photo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        final Button button2 = (Button) findViewById(R.id.send_message);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                send_message(editText.getText().toString());
            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            String imgString = Base64.encodeToString(stream.toByteArray(),
                    Base64.NO_WRAP);

            post_google(imgString);
        }

    }


    private void post_google(String im) {

        JSONObject myrequest = null;

        try {

            JSONObject content = new JSONObject().put("content", im);
            JSONObject type = new JSONObject().put("type","DOCUMENT_TEXT_DETECTION");
            JSONArray feature = new JSONArray().put(type);
            JSONObject thing = new JSONObject().put("image", content).put("features", feature);
            JSONArray request = new JSONArray().put(thing);
            myrequest = new JSONObject()
                    .put("requests", request);

        }catch(JSONException e){
            Toast.makeText(MainActivity.this,"REQUEST JSON EXC",
                    Toast.LENGTH_LONG).show();
        }

        String response  = null;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + key, myrequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String found = "";
                        try{
                            JSONArray res = response.getJSONArray("responses");
                            for(int i = 0; i< res.length(); i++){
                                found = found + res.getJSONObject(i).getJSONObject("fullTextAnnotation").getString("text");
                            }
                        }catch(JSONException e){
                            System.out.println("nothing found");
                        }
                        editText.setText(found, TextView.BufferType.EDITABLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"google call did not work",
                        Toast.LENGTH_LONG).show();
                System.out.println(error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void send_message(String mes) {

        JSONObject myrequest = null;

        try {
            myrequest = new JSONObject().put("message", mes).put("topic","typing"); //hard coded topic lol
        }catch(JSONException e){
            Toast.makeText(MainActivity.this,"REQUEST JSON EXCeption",
                    Toast.LENGTH_LONG).show();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url_sub, myrequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(MainActivity.this,"Text sent Successfully!",
                                Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"node call did not work",
                        Toast.LENGTH_LONG).show();
                System.out.println(error.getMessage());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}