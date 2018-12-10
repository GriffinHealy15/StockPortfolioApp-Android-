package edu.temple.stock_lab9_cis3515;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
// Edited Sunday Dec. 7 O'Clock
public class PortfolioPaneFrag extends Fragment {

    ListView stockListView;
    AddStockListAdapter addStockListAdapter;
    Context parent;
    TextView textView;

    public PortfolioPaneFrag() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.parent = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_portfolio_pane, container, false);
        stockListView = view.findViewById(R.id.list_of_stocks);

        // Check if file exists and is written to
        File stock_file = new File(getActivity().getFilesDir(), ("json_stocks19.json"));
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
                addStockListAdapter = new AddStockListAdapter(getContext(), new JSONArray(text.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            addStockListAdapter = new AddStockListAdapter(getContext(), new JSONArray());
        }

        stockListView.setAdapter(addStockListAdapter);


        textView = view.findViewById(R.id.addstockmessage);
        //hide the textView if there is a stock added
        if (addStockListAdapter.getCount() > 0){
            textView.setVisibility(View.GONE);
        } else{
            textView.setText(R.string.add_a_stock);
            textView.setTextSize(20);
            textView.setTextColor(Color.parseColor("Blue"));
        }

        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                ((PickedAStockInterface) parent).pickedAStock(position);
            }
        });

        return view;
    }

    public interface PickedAStockInterface {
        void pickedAStock(int position);
    }


}
