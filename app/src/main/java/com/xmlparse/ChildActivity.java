package com.xmlparse;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.xmlparse.handler.HtmlParseHandler;
import com.xmlparse.handler.VolleyResponseHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ChildActivity extends AppCompatActivity implements VolleyResponseHandler, HtmlParseHandler {

    // keys to get values
    static final String KEY_TABLE = "Table"; // parent node
    static final String KEY_RECIEPT_NO = "Recipt_No";
    static final String KEY_TAXAMOUNT = "TaxableAmount";
    static final String KEY_INSTAMAOUNT = "InstallmentAmount";
    static final String KEY_TAX = "Tax";
    static final String KEY_NONTAXAMOUNT = "NonTaxableAmount";
    static final String KEY_SERTAX = "ServiceTax";
    static final String KEY_PAYAMOUNT = "PayableAmount";

    private String asaid;
    private ProgressDialog progressDialog;
    private HashMap<String, String> childItems; // list with items
    private TextView tvChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        tvChild = (TextView) findViewById(R.id.tv_child);
        Bundle bundle = getIntent().getExtras();
        asaid = bundle.getString(MainActivity.INTENT_KEY);

        // start progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        // Add the request to the RequestQueue.
        VolleyInstance volleyInstance = new VolleyInstance(this);
        //url and context as parameters
        volleyInstance.volleyRequest(String.format("%sBindPayNow?ASAID=%s", MainActivity.URL, asaid), this);
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
        childItems = new HashMap<String, String>();

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(content.toString()); // getting DOM element

        NodeList nl = doc.getElementsByTagName(KEY_TABLE);
        // looping through all item nodes <item>
        // creating new HashMap
        Element e = (Element) nl.item(0);
        // adding each child node to HashMap key => value
        childItems.put(KEY_RECIEPT_NO, parser.getValue(e, KEY_RECIEPT_NO));
        childItems.put(KEY_TAXAMOUNT, parser.getValue(e, KEY_TAXAMOUNT));
        childItems.put(KEY_NONTAXAMOUNT, parser.getValue(e, KEY_NONTAXAMOUNT));
        childItems.put(KEY_INSTAMAOUNT, parser.getValue(e, KEY_INSTAMAOUNT));
        childItems.put(KEY_SERTAX, parser.getValue(e, KEY_SERTAX));
        childItems.put(KEY_PAYAMOUNT, parser.getValue(e, KEY_PAYAMOUNT));
        childItems.put(KEY_TAX, parser.getValue(e, KEY_TAX));

        //process complete remove progress dialog
        progressDialog.cancel();
        tvChild.setText(
                String.format("payamonut: %s, tax: %s, reciept_no: %s",
                        childItems.get(KEY_PAYAMOUNT),
                        childItems.get(KEY_TAX),
                        childItems.get(KEY_RECIEPT_NO)));
    }
}
