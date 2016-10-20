package com.xmlparse;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.xmlparse.handler.VolleyResponseHandler;

/**
 * Created by gour.kishor on 17-Oct-16.
 */
public class VolleyInstance {
    private VolleyResponseHandler responseHandler;

    public VolleyInstance(VolleyResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    public void volleyRequest(String url, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (responseHandler != null) {
                            responseHandler.onResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (responseHandler != null) {
                    responseHandler.onError();
                }
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
