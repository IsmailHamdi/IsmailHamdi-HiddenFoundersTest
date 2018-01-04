package com.example.ismail.hiddenfounderstest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by ismail on 02.01.2018.
 * AsyncTask that retrieve jsoon data from the Git API without blocking the UI thread
 */


public class GitHubRepoGetter extends AsyncTask<String, String, Void> {

    private Context context; //the context where the task is executed (in our case the MainActivity)
    private CustomListView listAttached; //the Custom list view that will retrieve the data
    private static List<Repository> resultsFromGitAPI = new ArrayList<Repository>(); //List of result retrieved from the Git API, it's static to maintain the old items when adding another page
    private ProgressDialog pDialog;
    private static  int currentPage = 0; //Counter to maintain the page to retrieve because we must always create new instance when we want to execute the task, so it's static
    private Handler mHandler; //A handler to send and process Runnable objects to the UI thread
    private boolean isConnected;
    private  BroadcastReceiver receiver; //A BroadcastReceiver to maintain the connection changing status in the middle of lunching the task

    public GitHubRepoGetter(Context context, CustomListView listAttached) {
        this.context = context;
        this.listAttached = listAttached;
        mHandler=new Handler();
        this.isConnected = false;
    }

    @Override
    protected Void doInBackground(String... strings) {

        if(this.isConnected) { //If the phone is connected to internet we execute our task to retrieve data from Git API

            HttpCall sh = new HttpCall(); //Instantiate a variable that make an Http request to the URL

            // Making a request to GitAPI and getting response
            String jsonStr = sh.makeServiceCall(this.constructUrl());
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr); //Casting the response to an JSON object

                    JSONArray Repos = jsonObj.getJSONArray("items"); //Get the items array that contain desired informations of the repositorys

                    for (int i = 0; i < Repos.length(); i++) {
                        JSONObject repo = Repos.getJSONObject(i);   //Getting every repository from the JSON array and construct a Repository instance
                        Repository r = new Repository();
                        JSONObject owner = repo.getJSONObject("owner");

                        r.setId(repo.getLong("id"));
                        r.setName(repo.getString("name"));
                        r.setNumberStars(repo.getLong("stargazers_count"));
                        r.setDescription(repo.getString("description"));
                        r.setOwnerName(owner.getString("login"));

                        Bitmap ownerPic = null;
                        try {
                            InputStream in = new java.net.URL(owner.getString("avatar_url")).openStream();
                            ownerPic = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                        r.setOwnerAvatar(ownerPic);

                        resultsFromGitAPI.add(r); // Adding the constructed Repository to the list of results
                    }

                } catch (final JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            } else {
                mHandler.post(new Runnable() {
                    public void run(){
                        Toast.makeText(context, "Reponse vide du serveur !!!", Toast.LENGTH_LONG).show();
                    }
                }); //Show a toast Message on UI Thread
            }
        }else { //If the phone is not connected to internet, the handler post an AlertDialog to the UI thread
            mHandler.post(new Runnable() {
                public void run(){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Pas de connection !!!");
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (resultsFromGitAPI.size()==0) //If the application is first opened while there is no connection it will exit
                                        System.exit(-1);
                                }
                            });
                    alertDialogBuilder.create().show();
                }
            });
        }
        return null;
    }

    @Override
    protected void onPreExecute() { //Before execution method
        super.onPreExecute();
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager.getActiveNetworkInfo() != null) { //Verifying if the phone is connected to internet
                if (connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {
                    this.isConnected = true;

                    final Context c = this.context; //Variables will be used when
                    final GitHubRepoGetter g = this;//initialising the broadcast receiver
                    receiver = new BroadcastReceiver() { //Initialising the receiver tha will be notified if the connection state is changed
                        @Override                        // and the task is running
                        public void onReceive(Context context, Intent intent) {
                            if (connectivityManager.getActiveNetworkInfo() == null || !connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) { //Verifying if the connection to the internet
                                //is interrupted
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
                                        alertDialogBuilder.setMessage("Connection interrompue !!!");
                                        alertDialogBuilder.setPositiveButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                    }
                                                });
                                        alertDialogBuilder.create().show();
                                    }
                                }); //If it's the case, an AlertDialog is shown informing the user that the connection is interrupted
                                try {
                                    g.finalize(); //And the task is terminated
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                        }
                    };

                    context.getApplicationContext().registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")); //Registering the receiver in the manifest file

                    pDialog = new ProgressDialog(context);
                    pDialog.setMessage("Attendez SVP...");//Set progress dialog message
                    pDialog.setCancelable(false);
                    pDialog.show();//Showing progress dialog
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) { //After execution method
        super.onPostExecute(aVoid);
        if(this.isConnected) { //If the phone is connected, the waiting dialog is dismissed, the receiver is unregistered from manifest file and the data source of the list adapter is refreshed
            if (this.pDialog.isShowing())
                this.pDialog.dismiss();
            context.getApplicationContext().unregisterReceiver(receiver);
            this.listAttached.getCustomAdapter().changeDataSource(this.resultsFromGitAPI);
        }
    }

    private String constructUrl(){ //Construct the Git API request URL based on the page attended and the current date
        String url = "";

        Calendar calendar = Calendar.getInstance(); //Instantiating an calendar object to get the current date and subtract 30 days

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//Formatting date with the pattern used in the request URL to Git API
        calendar.add(Calendar.DATE, -30); //Subtract 30 days from the current date
        url = "https://api.github.com/search/repositories?q=created:>"+sdf.format(calendar.getTime())+"&sort=stars&order=desc&page="+this.currentPage; // Construct the URL

        return  url;
    }

    public List<Repository> Next(){//Method that execute this task on the next page
        this.currentPage ++; //Incrementing the counter
        this.execute(); //Executing the task
        return this.resultsFromGitAPI; // Returning the list of results for using it if necessary
    }
}