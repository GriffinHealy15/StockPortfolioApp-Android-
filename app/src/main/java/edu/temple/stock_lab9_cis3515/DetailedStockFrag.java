package edu.temple.stock_lab9_cis3515;


import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */

// Edited Sunday Dec. 7 O'Clock
public class DetailedStockFrag extends Fragment {
    TextView stock_name;
    TextView open_price;
    TextView last_price;
    ConstraintLayout dl;
    String Symbol = "";
    String stock_image_url;

    public DetailedStockFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detailed_stock, container, false);
        stock_name = view.findViewById(R.id.stockNameID);
        open_price = view.findViewById(R.id.openPriceID);
        last_price = view.findViewById(R.id.lastPriceID);
        dl = view.findViewById(R.id.detailedConstraintLayout);
        dl.setBackgroundColor(Color.parseColor("#00ff99"));

         WebView webView = view.findViewById(R.id.webView);
         webView.getSettings().setJavaScriptEnabled(true);


        Bundle collect_args = getArguments();

        if (collect_args != null) {

            stock_name.setText(collect_args.getString("stock_name"));
            stock_name.setTextSize(22);
            stock_name.setTextColor(Color.parseColor("White"));
            open_price.setText("Open Price: $".concat(collect_args.getString("open_price")));
            open_price.setTextSize(20);
            open_price.setTextColor(Color.parseColor("White"));
            last_price.setText("Last Price: $".concat(collect_args.getString("last_price")));
            last_price.setTextSize(20);
            last_price.setTextColor(Color.parseColor("White"));
            Symbol = collect_args.getString("symbol");
            stock_image_url = ("https://macc.io/lab/cis3515/?symbol=".concat(Symbol).concat("&width=400&height=200"));
            Log.i("Stock image url", stock_image_url);
            webView.loadUrl(stock_image_url);

        }

        return view;
    }

}
