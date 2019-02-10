package com.example.william.nodatracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}

public class MainActivity extends AppCompatActivity {

    private String key ="AIzaSyCPb9TQhfFBLGA_X36GIAeBPbz8e2sX9AI";

    private final static String DEBUG_TAG = "MakePhotoActivity";
    private Bitmap imageBitmap = null;


    private String url ="https://vision.googleapis.com/v1/images:annotate?key=";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.captureFront);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
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
            imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);

            String imgString = Base64.encodeToString(stream.toByteArray(),
                    Base64.NO_WRAP);

            post_google(imgString);
        }

    }


    private void post_google(String im) {

        RequestQueue queue = Volley.newRequestQueue(this); //very bad lol hackathon code

        JSONObject myrequest = null;

        try {

            JSONObject content = new JSONObject().put("content", im);

            JSONObject type = new JSONObject()
                    .put("type","DOCUMENT_TEXT_DETECTION");

            JSONArray feature = new JSONArray().put(type);

            JSONObject thing = new JSONObject().put("image", content).put("features", feature);

            JSONArray request = new JSONArray().put(thing);

            myrequest = new JSONObject()
                    .put("requests", request);

        }catch(JSONException e){
            Toast.makeText(MainActivity.this,"REQUEST JSON EXC",
                    Toast.LENGTH_LONG).show();
        }

        System.out.println(myrequest);
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

                        if(found != ""){
                            System.out.println("");
                            System.out.println("found: " + found);
                            System.out.println("");
                        }

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


//        try {
//            URL serverUrl = new URL(url + key);
//            URLConnection urlConnection = serverUrl.openConnection();
//            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
//
//            httpConnection.setRequestMethod("POST");
//            httpConnection.setRequestProperty("Content-Type", "application/json");
//
//            httpConnection.setDoOutput(true);
//
//            BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
//                    OutputStreamWriter(httpConnection.getOutputStream()));
//            httpRequestBodyWriter.write(myrequest);
//            httpRequestBodyWriter.close();
//
//            response = httpConnection.getResponseMessage();
//
//        }catch(IOException e){
//            Toast.makeText(MainActivity.this,"google call did not work, exception caught",
//                    Toast.LENGTH_LONG).show();
//            System.out.print(e.getMessage());
//        }
//
//        if(response != null){
//            System.out.println(response);
//        }
    }
}