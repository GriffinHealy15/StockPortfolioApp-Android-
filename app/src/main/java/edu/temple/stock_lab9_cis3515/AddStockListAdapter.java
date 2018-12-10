package edu.temple.stock_lab9_cis3515;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


// Edited Sunday Dec. 7 O'Clock
public class AddStockListAdapter extends BaseAdapter {

    Context context;
    JSONArray jsonStock_Array;
    String open_price;
    String last_price;
    double open_priceDouble;
    double last_priceDouble;
    String whatevr;

    public AddStockListAdapter(Context context, JSONArray jsonStock_Array) {

        this.context = context;
        this.jsonStock_Array = jsonStock_Array;
    }

    @Override
    public int getCount() {
        return jsonStock_Array.length();
    }

    @Override
    public Object getItem(int position) {
        JSONObject stockObject = new JSONObject();
        try {
            // return item at position in array
            stockObject = jsonStock_Array.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stockObject;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView stock_textView = new TextView(context);
        try {
            stock_textView.setText(jsonStock_Array.getJSONObject(position).getString("Symbol"));
            whatevr = jsonStock_Array.getJSONObject(position).getString("Symbol");
            Log.i("ehh", whatevr);
            open_price = jsonStock_Array.getJSONObject(position).getString("Open");
            last_price = jsonStock_Array.getJSONObject(position).getString("LastPrice");

            open_priceDouble = Double.valueOf(open_price);
            last_priceDouble = Double.valueOf(last_price);
            if (last_priceDouble > open_priceDouble) {
                stock_textView.setBackgroundColor(Color.parseColor("Green"));
            }
            else {
                stock_textView.setBackgroundColor(Color.parseColor("Red"));
            }
            stock_textView.setTextSize(24);
            stock_textView.setTextColor(Color.parseColor("White"));

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return stock_textView;
    }

    public void updateStocks(JSONArray jsonArray){
        this.jsonStock_Array = jsonArray;
    }
}
