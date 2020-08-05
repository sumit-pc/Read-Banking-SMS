package com.sumit.payo;


import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class Dashboard extends Fragment {


    View rootView;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    TextView expense, income;
    Double totalExpense, totalIncome;

    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        smsListView = (ListView) rootView.findViewById(R.id.SMSList);
        expense = rootView.findViewById(R.id.txtExpense);
        income = rootView.findViewById(R.id.txtIncome);
        totalExpense = 0.0;
        totalIncome = 0.0;

        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);

        refreshSmsInbox();
        return rootView;

    }


    public void refreshSmsInbox() {
        totalExpense = 0.0;
        totalIncome = 0.0;
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexDate = smsInboxCursor.getColumnIndex("date");;
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {

            if(smsInboxCursor.getString(indexBody).contains("credited")|| smsInboxCursor.getString(indexBody).contains("debited") || smsInboxCursor.getString(indexBody).contains("withdrawn")) {

//                String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
//                        "\n" + smsInboxCursor.getString(indexBody) + "\n";
//                arrayAdapter.add(str);

                String smsDto = smsInboxCursor.getString(indexBody);
                Pattern regEx
                        = Pattern.compile("(?i)(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)");
                // Find instance of pattern matches
                Matcher m = regEx.matcher(smsDto);
                if (m.find()) {
                    try {
                        //Log.e("amount_value= ", "" + m.group(0));
                        String amount = (m.group(0).replaceAll("inr", ""));
                        amount = amount.replaceAll("rs", "");
                        amount = amount.replaceAll("inr", "");
                        amount = amount.replaceAll(" ", "");
                        amount = amount.replaceAll(",", "");

                        //smsDto.setAmount(Double.valueOf(amount));

                        if (smsDto.contains("debited") || smsDto.contains("purchasing") || smsDto.contains("purchase") || smsDto.contains("dr") || smsDto.contains("dr"))
                        {
                            long date = smsInboxCursor.getLong(indexDate);
                            Date dateVal = new Date(date);
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            String dateText = format.format(dateVal);


                            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                                    "\n" + "Debit Amount: "+ String.valueOf(amount) + "\n" +
                                    "Index Date: " + dateText +
                                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
                            arrayAdapter.add(str);

                            amount = amount.replaceAll("[^\\d.]", "");
                            if(String.valueOf(amount.charAt(0)).equals("."))
                            {
                                amount = amount.substring(1);
                            }

                            totalExpense = totalExpense + Double.parseDouble(amount);

                            //smsDto.setTransactionType("0");
                        }
                        else if (smsDto.contains("credited") || smsDto.contains("cr"))
                        {
                            long date = smsInboxCursor.getLong(indexDate);
                            Date dateVal = new Date(date);
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            String dateText = format.format(dateVal);


                            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                                    "\n" + "Debit Amount: "+ String.valueOf(amount) + "\n" +
                                    "Index Date: " + dateText +
                                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
                            arrayAdapter.add(str);

                            amount = amount.replaceAll("[^\\d.]", "");
                            if(String.valueOf(amount.charAt(0)).equals("."))
                            {
                                amount = amount.substring(1);
                            }
                            totalIncome = totalIncome + Double.parseDouble(amount);
                            //smsDto.setTransactionType("1");

                        }
                        //smsDto.setParsed("1");




                        Log.e("matchedValue= ", "" + amount);
                        //Log.e("matchedValue= ", "" + amount.replaceAll("[^\\d.]", ""));

//                        if (!Character.isDigit(smsDto.charAt(0)))
//                            resSms.add(smsDto);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("No_matchedValue ", "No_matchedValue ");
                }

            }

        } while (smsInboxCursor.moveToNext());


        expense.setText("Total Expenses: ₹ "+String.valueOf(String.format("%.2f", totalExpense)));
        income.setText("Total Income: ₹ "+String.valueOf(String.format("%.2f", totalIncome)));
        updateChart();
    }

    public void updateChart(){
        // Update the text in a center of the chart:
        TextView numberOfCals = rootView.findViewById(R.id.number_of_calories);
        numberOfCals.setText(String.valueOf(String.format("%.2f", totalIncome) + " / " + String.format("%.2f", totalExpense)));

        // Calculate the slice size and update the pie chart:
        ProgressBar pieChart = rootView.findViewById(R.id.stats_progressbar);

        double d = (double) totalExpense + (double) totalIncome;


        int progress = (int) ((totalExpense / d) * 100);
        //Toast.makeText(MainActivity.this, String.valueOf(progress),Toast.LENGTH_LONG).show();
        pieChart.setProgress(progress);



    }

}
