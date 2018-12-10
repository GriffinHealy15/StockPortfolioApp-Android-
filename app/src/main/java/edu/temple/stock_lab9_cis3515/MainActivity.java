package edu.temple.stock_lab9_cis3515;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import android.support.v4.app.FragmentManager;

// Edited Sunday Dec. 7 O'Clock
public class MainActivity extends AppCompatActivity implements PortfolioPaneFrag.PickedAStockInterface {

    EditText editStockSearch;
    FloatingActionButton searchStockActionButton;
    FragmentManager fragmentManager;
    PortfolioPaneFrag portfolioPaneFrag;

    File stock_file;
    String jsonFileN = "json_stocks19.json";
    String temp = "";
    int stockposition;
    TextView textView;
    GetNewestPriceService getNewestPriceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Stock Search");
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setBackgroundColor(Color.parseColor("#00ff99"));

        // Access to files using stock_file
        stock_file = new File(getFilesDir(), jsonFileN);
        //add the fragment to the container. fragContainer is present in all layouts
        fragmentManager = getSupportFragmentManager();

        //this is the fragment that holds the list of stocks
        portfolioPaneFrag = new PortfolioPaneFrag();

        fragmentManager.beginTransaction().replace(R.id.portfolioContainer, portfolioPaneFrag).commit();

        editStockSearch = (EditText) findViewById(R.id.search_for_stock);
        searchStockActionButton = (FloatingActionButton) findViewById(R.id.stockSearchActionButton);

      searchStockActionButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String Text = editStockSearch.getText().toString();
              Log.i(":", Text);
              //Thread for accessing the url + search (Goog)
              Thread thread = new Thread(){
                  @Override
                  public void run(){
                      URL url;
                      try{
                          url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + editStockSearch.getText().toString()); // build url
                          BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                          String response = "", tmpResponse;
                          tmpResponse = bufferedReader.readLine(); //get line from input stream
                          while(tmpResponse != null){ //keep reading until null
                              response = response + tmpResponse;
                              tmpResponse = bufferedReader.readLine();
                          }
                          temp = response;
                          JSONObject stockObject = new JSONObject(response); //create JSON object from lines read

                          //Message object to send to handler
                          Message msg = Message.obtain();
                          msg.obj = stockObject;
                          // Send to stock handler
                          stockHandler.sendMessage(msg);

                      } catch(Exception e){
                          e.printStackTrace();
                      }
                  }
              };
              thread.start();
          }
      });
    }


    // Service work ////////
    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, GetNewestPriceService.class);
        bindService(serviceIntent, serviceWork, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceWork = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GetNewestPriceService.ABinderObject binder = (GetNewestPriceService.ABinderObject) service;
            getNewestPriceService = binder.getService();
            getNewestPriceService.getNewestPriceServiceWork(AServiceHandler);
        }

        Handler AServiceHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                DetailedStockFrag detailedStockFrag;
                if ((detailedStockFrag = (DetailedStockFrag) fragmentManager.findFragmentByTag("findthis")) != null){
                    JSONArray jsonArray = (JSONArray) msg.obj;
                    try {
                        String newPrice = ("Last Price: $".concat(jsonArray.getJSONObject(stockposition).getString("LastPrice")));
                        detailedStockFrag.last_price.setText(newPrice);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceWork);
    }

    // Service Work ends here //////////

    Handler stockHandler = new Handler(new Handler.Callback() {

        // This is over rid to receive the messages (stock) Goog
        @Override
        public boolean handleMessage(Message msg) {

            Log.i("response", temp);
            //this is the json object passed to the handler
            JSONObject stockResponseObject = (JSONObject) msg.obj;
            if (!stockResponseObject.has("Name")){
                Toast.makeText(MainActivity.this, "This is not any Companies Stock Name", Toast.LENGTH_LONG).show();
                return false;
            }

            else {
                Toast.makeText(MainActivity.this, "Successfully added a Stock", Toast.LENGTH_LONG).show();
            }

            // Create a json array
            JSONArray jsonArray_Has_Stocks = null;

            // Check if file exists and is written to
            if (stock_file.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(stock_file));
                    StringBuilder text = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                    jsonArray_Has_Stocks = new JSONArray(text.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                jsonArray_Has_Stocks = new JSONArray();
            }
            // Put the stock typed in edit text into json array
            jsonArray_Has_Stocks.put(stockResponseObject);

            // Now that newest stock is added, place the json array back into the file
            try {
                FileOutputStream outputStockStream  = new FileOutputStream(stock_file);
                // output json array to outputStockStream
                outputStockStream.write(jsonArray_Has_Stocks.toString().getBytes());
                outputStockStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            portfolioPaneFrag.addStockListAdapter.updateStocks(jsonArray_Has_Stocks);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    portfolioPaneFrag.addStockListAdapter.notifyDataSetChanged();
                }
            });

            return false;
        }
    });

    @Override
    public void pickedAStock(int position) {

        this.stockposition = position;

        JSONArray jsonArray = null;

        // Check if file exists and is written to
        if (stock_file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(stock_file));
                StringBuilder text = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
                jsonArray = new JSONArray(text.toString());

                DetailedStockFrag detailedStockFrag = new DetailedStockFrag();

                Bundle stock_bundle = new Bundle();

                stock_bundle.putString("stock_name", jsonArray.getJSONObject(stockposition).getString("Name"));
                stock_bundle.putString("open_price", jsonArray.getJSONObject(stockposition).getString("Open"));
                stock_bundle.putString("last_price", jsonArray.getJSONObject(stockposition).getString("LastPrice"));
                stock_bundle.putString("symbol", jsonArray.getJSONObject(stockposition).getString("Symbol"));
                detailedStockFrag.setArguments(stock_bundle);

                if (findViewById(R.id.detailedContainer) != null){
                    fragmentManager.beginTransaction().replace(R.id.detailedContainer, detailedStockFrag, "findthis").commit();
                } else{
                    fragmentManager.beginTransaction().replace(R.id.portfolioContainer, detailedStockFrag, "findthis").addToBackStack(null).commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.mainmenu, menu);

        final MenuItem searchItem = menu.findItem(R.id.searchForStock); //this is the search icon menu item
        final SearchView searchView = (SearchView) searchItem.getActionView(); //the action of search icon

        return super.onCreateOptionsMenu(menu);

    }

}
