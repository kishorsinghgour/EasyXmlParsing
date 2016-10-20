package com.xmlparse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xmlparse.handler.HtmlParseHandler;
import com.xmlparse.handler.VolleyResponseHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity implements VolleyResponseHandler, HtmlParseHandler {

    // All static variables
    static final String URL = "http://paytopaid.schoolerponline.in/service.asmx/";
    // XML node keys
    static final String KEY_TABLE = "Table"; // parent node
    static final String KEY_ID = "ASAID";
    static final String KEY_NUMBER = "Number";
    static final String KEY_INSTAMAOUNT = "InstallmentAmount";
    static final String KEY_TAX = "Tax";
    static final String KEY_INSTLDATE = "InstallmentDate";
    static final String KEY_INSTLNAME = "InstallmentName";
    static final String KEY_DISDESC = "DiscountDescription";
    static final String KEY_FEEDESC = "FeeDescription";

    static final String INTENT_KEY = "asaid";

    private ProgressDialog progressDialog;
    private List tableItems; // list with items
    private ListView listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listItems = (ListView) findViewById(R.id.list_item);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        // Add the request to the RequestQueue.
        VolleyInstance volleyInstance = new VolleyInstance(this);
        //url and context as parameters
        volleyInstance.volleyRequest(String.format("%sBindFeeDetails?SAMID=%d", URL, 8), this);//replace 8 with your SAMID

        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String asaid = (String) view.getTag();

                Bundle bundle = new Bundle();
                bundle.putString(INTENT_KEY, asaid);
                Intent intent = new Intent(MainActivity.this, ChildActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResponse(String response) {
        try {
            MySaxHandler handler = new MySaxHandler(this);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            InputSource source = new InputSource(new StringReader(response));
            parser.parse(source, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError() {
        progressDialog.cancel();
    }

    @Override
    public void onHtmlParser(String content) {
        tableItems = new ArrayList<HashMap<String, String>>();

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(content.toString()); // getting DOM element

        NodeList nl = doc.getElementsByTagName(KEY_TABLE);
        // looping through all item nodes <item>
        for (int i = 0; i < nl.getLength(); i++) {
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            Element e = (Element) nl.item(i);
            // adding each child node to HashMap key => value
            map.put(KEY_ID, parser.getValue(e, KEY_ID));
            map.put(KEY_DISDESC, parser.getValue(e, KEY_DISDESC));
            map.put(KEY_FEEDESC, parser.getValue(e, KEY_FEEDESC));
            map.put(KEY_INSTAMAOUNT, parser.getValue(e, KEY_INSTAMAOUNT));
            map.put(KEY_INSTLDATE, parser.getValue(e, KEY_INSTLDATE));
            map.put(KEY_INSTLNAME, parser.getValue(e, KEY_INSTLNAME));
            map.put(KEY_NUMBER, parser.getValue(e, KEY_NUMBER));
            map.put(KEY_TAX, parser.getValue(e, KEY_TAX));

            // adding HashList to ArrayList
            tableItems.add(map); // TODO: this list will be used in your case
            ListAdapter usersAdapter = new ListAdapter(MainActivity.this, tableItems);
            listItems.setAdapter(usersAdapter);
            progressDialog.cancel();
        }
    }
}
