package com.pdmanager.communication;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pdmanager.models.LoginModel;
import com.pdmanager.models.LoginResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by george on 9/12/2015.
 */


public class RESTClient {


    private String accessToken;

    public RESTClient() {

        accessToken = null;
    }

    public RESTClient(String a) {

        accessToken = a;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public void updateToken(String a) {

        this.accessToken = a;

    }

    public LoginResult Login(LoginModel model) {

        String uri = "http://pdmanager.3dnetmedical.com/oauth/token";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(uri);

        if (accessToken != null)
            httppost.addHeader("Authorization", "Bearer " + accessToken);
        //   String mobileServiceAppId = "AZURE_SERVICE_APP_ID";

        try {

            StringEntity se = new StringEntity(model.toJsonString());
            se.setContentEncoding("application/x-www-form-urlencoded");
            se.setContentType("application/json");
            httppost.setEntity(se);
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                Gson gson = new Gson();


                LoginResult obj = gson.fromJson(convertInputStreamToString(response.getEntity().getContent()), new TypeToken<LoginResult>() {
                }.getType());
                obj.success = true;
                return obj;
            } else

            {
                LoginResult res = new LoginResult();
                res.success = false;
                return res;

            }


        } catch (Exception e) {
            Log.d("ERROR", e.toString());
            LoginResult res = new LoginResult();
            res.success = false;
            return res;
        }


    }

    public boolean Put(String uri, String item) {


        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httppost = new HttpPut(uri);
        if (accessToken != null)
            httppost.addHeader("Authorization", "Bearer " + accessToken);
        //   String mobileServiceAppId = "AZURE_SERVICE_APP_ID";

        try {

            StringEntity se = new StringEntity(item);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");
            httppost.setEntity(se);
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();

            return code == HttpStatus.SC_OK || code == HttpStatus.SC_NO_CONTENT;

        } catch (Exception e) {


            Log.d("ERROR", e.toString());
            return false;
        }


    }

    public boolean Post(String uri, String item) {


        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(uri);
        if (accessToken != null)
            httppost.addHeader("Authorization", "Bearer " + accessToken);
        //   String mobileServiceAppId = "AZURE_SERVICE_APP_ID";

        try {

            StringEntity se = new StringEntity(item);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");
            httppost.setEntity(se);
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();

            return code == HttpStatus.SC_OK || code == HttpStatus.SC_NO_CONTENT;

        } catch (Exception e) {

            return false;
        }


    }

    public String Get(String uri) {

        String result = null;
        HttpClient httpclient = new DefaultHttpClient();

        HttpGet httppost = new HttpGet(uri);
        if (accessToken != null)
            httppost.addHeader("Authorization", "Bearer " + accessToken);
        //   String mobileServiceAppId = "AZURE_SERVICE_APP_ID";
        InputStream inputStream = null;
        try {


            HttpResponse response = httpclient.execute(httppost);
            //  String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            // receive response as inputStream
            inputStream = response.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "error";


        } catch (Exception e) {

            Log.d("ERROR", e.toString());
            result = "error";
        }


        return result;

    }

}
