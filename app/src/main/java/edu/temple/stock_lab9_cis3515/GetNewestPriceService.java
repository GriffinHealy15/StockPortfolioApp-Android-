package edu.temple.stock_lab9_cis3515;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


// Edited Sunday Dec. 7 O'Clock
public class GetNewestPriceService extends Service {

    IBinder someBinder = new ABinderObject();
    Thread thread_that_gets_Price;

    public GetNewestPriceService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return someBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class ABinderObject extends Binder {
        GetNewestPriceService getService() {
            return GetNewestPriceService.this;
        }
    }

    public void getNewestPriceServiceWork(final Handler handler){
        final Handler some_handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    // Find file that contains stock objects list
                    final File file = new File(getFilesDir(), "json_stocks19.json");
                    JSONArray jsonArray = null;
                    if (file.exists()){
                        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                            StringBuilder text = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                text.append(line);
                                text.append('\n');
                            }
                            bufferedReader.close();
                            jsonArray = new JSONArray(text.toString());
                            final JSONArray jsonArray2 = jsonArray;

                            // get each newest object for each stock
                            thread_that_gets_Price = new Thread(){
                                @Override
                                public void run(){
                                    for (int i = 0; i < jsonArray2.length(); i++){
                                        try {
                                            JSONObject jsonObject = jsonArray2.getJSONObject(i);
                                            String symbol = jsonObject.getString("Symbol");
                                            URL url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + symbol);
                                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                                            String response = "", tmpResponse;
                                            tmpResponse = bufferedReader.readLine(); //get line from input stream
                                            while(tmpResponse != null){ //keep reading until null
                                                response = response + tmpResponse;
                                                tmpResponse = bufferedReader.readLine();
                                            }
                                            JSONObject stockObject = new JSONObject(response); //create JSON object from lines read
                                            jsonArray2.put(i, stockObject);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    // put array back into the file
                                    FileOutputStream fileOutputStream = null;
                                    try {
                                        fileOutputStream = new FileOutputStream(file);
                                        fileOutputStream.write(jsonArray2.toString().getBytes());
                                        fileOutputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            // send the array as an object to the service handler which will get the newest price
                            thread_that_gets_Price.start();
                            Message msg = Message.obtain();
                            msg.obj = jsonArray2;
                            handler.sendMessage(msg);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
                some_handler.postDelayed(this, 10 * 1000);
            }
        }, 0);
    }

}
