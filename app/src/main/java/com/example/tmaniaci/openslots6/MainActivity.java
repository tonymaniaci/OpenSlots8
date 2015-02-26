package com.example.tmaniaci.openslots6;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ArrayAdapter<Object> itemAdapter;
    private String username;
    private String select_date;

    //JSON Node Names
    private static final String TAG_JID = "events";      /* json data identifier */

    //private static final String TAG_11 =     "start";      /* level 1 */
    //private static final String TAG_12 =     "end";   /* level 1 */
    private static final String TAG_13 = "title";      /* level 1 */
    private static final String TAG_14 = "duration";   /* level 1 */
    private static final String TAG_15 = "startText";   /* level 1 */
    private static final String TAG_16 = "endText";   /* level 1 */
    private static final String TAG_17 = "attendee_id";   /* level 1 */

    //Get from SharedPreferenceSettings file
    final String startOfDay = "08:00";
    final int startOfDay_int = Integer.parseInt(startOfDay.replace(":", ""));

    final String endOfDay = "19:00";
    final int endOfDay_int = Integer.parseInt(endOfDay.replace(":", ""));


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Intent i = getIntent();
        // Receiving the Data
        //String full_name = i.getStringExtra("full_name");
        username = i.getStringExtra("username");
        select_date = i.getStringExtra("selectedDate");
        */

        username = "doctor1";
        select_date = "2015-02-24";

        Log.d("*** username ****", username);
        Log.d("*** select_date ***", select_date);


        itemAdapter = new ArrayAdapter<Object>(this, 0) {

            String prev_end_time = null;
            String slot_start_time;
            String slot_end_time;

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.activity_schedule_detail, null);

                Slot item = (Slot) getItem(position);

                slot_start_time = item.getSlot_start();
                slot_end_time = item.getSlot_end();

                //TextView session_duration = (TextView) convertView.findViewById(R.id.duration);
                //session_duration.setText(dur_mins);

                TextView start_text = (TextView) convertView.findViewById(R.id.startTime);
                start_text.setText(slot_start_time);

                TextView end_text = (TextView) convertView.findViewById(R.id.endTime);
                end_text.setText(slot_end_time);

                return convertView;

            }
        };

        // basic setup of the ListView and adapter
        setContentView(R.layout.activity_schedule);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(itemAdapter);

        // authenticate and do the first load
        //getCredentials();
        load2();
    }

    //Future<JsonArray> loading;
    Future<JsonObject> loading;

    private void load2() {
        // don't attempt to load more if a load is already in progress
        if (loading != null && !loading.isDone() && !loading.isCancelled())
            return;

        //https://www.itelepsych.com/home/getEvents.php?iOS=Y&host_id=(the doctor id)
        String url_host = "https://dev1.itelepsych.com/home/";
        String url_php = "getEvents.php";
        String url_params = "?iOS=Y&host_id=" + username;
        String url = url_host + url_php + url_params;

        Future<JsonObject> JsonObject = Ion.with(this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // this is called back onto the ui thread, no Activity.runOnUiThread or Handler.post necessary.
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Error loading tweets", Toast.LENGTH_LONG).show();
                            return;
                        }

                        JsonArray result_jsa = result.getAsJsonArray(TAG_JID);

                        ArrayList scheduleList = new ArrayList();

                        for (int i = 0; i < result_jsa.size(); i++) {

                            JsonObject result_jso = result_jsa.get(i).getAsJsonObject();


                            String startText = result_jso.get(TAG_15).getAsString();
                            String yyyy_mm_dd = startText.substring(0, 10);

                            if (select_date.equals(yyyy_mm_dd)) {
                                scheduleList.add(result_jso);
                            }
                        }

                        ArrayList OpenSlots = GetOpenSlots(scheduleList);
                        int num_slots = OpenSlots.size();

                        for (int i=0; i < num_slots; i++) {

                            itemAdapter.add( OpenSlots.get(i));

                        }

                        int num_items = itemAdapter.getCount();

                        if (num_items == 0) {
                            Toast.makeText(getApplicationContext(), "No Open Slots Today", Toast.LENGTH_LONG).show();
                        }

                    }
                });

        }


    ArrayList GetOpenSlots(ArrayList sch_jsa) {
         String start_time;
         String end_time;
         String prev_end_time = null;

         int num_items = sch_jsa.size();

         ArrayList oslots = new ArrayList();

        for (int i = 0; i < num_items; i++) {


            JsonObject item = (JsonObject) sch_jsa.get(i);

            String startText = item.get(TAG_15).getAsString();
            start_time = startText.substring(11, 16);
            int start_time_int = Integer.parseInt(start_time.replace(":", ""));

            String endText = item.get(TAG_16).getAsString();
            end_time = endText.substring(11, 16);

            Slot open_slot = new Slot();

            if ((i == 0)&&(start_time_int > startOfDay_int))  {
                open_slot.setSlot_start(startOfDay);
                open_slot.setSlot_end(end_time);
                oslots.add(open_slot);
                prev_end_time = end_time;

                }

            else {
                open_slot.setSlot_start(prev_end_time);
                open_slot.setSlot_end(start_time);
                prev_end_time = end_time;
                oslots.add(open_slot);

                if ((i == num_items-1 )&&(start_time_int < endOfDay_int))  {
                    Slot open_slot_last = new Slot();
                    open_slot_last.setSlot_start(prev_end_time);
                    open_slot_last.setSlot_end(endOfDay);
                    oslots.add(open_slot_last);
                }

                prev_end_time = end_time;
                }


        }



        return oslots;





    }



}//END