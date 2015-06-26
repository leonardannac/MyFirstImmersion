package com.annaleonard.myfirstimmersion;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.NoRouteToHostException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {

    private CardScrollView mCardScrollView; //horizontal view that shows scrolling cards
    private CardScrollAdapter mCardScrollAdapter; //supplies cards from mCards to mCardScrollView
    private List<CardBuilder> mCards;   //a list to store cards made with CardBuilder
    private Thread thread;

    private EditText joint1pos, joint2pos, joint3pos, joint4pos, joint5pos, joint6pos, joint7pos;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Log.i("MainAct super.onCreate", " ");

        startThread();

        createCards();

        publishCards();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScrollView.activate();
    }

    @Override
    protected void onPause() {
        mCardScrollView.deactivate();
        super.onPause();
    }

    private void createCards() {
        mCards = new ArrayList<CardBuilder>();
        mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT).setText("Quick-Know"));
    }

    private void publishCards() {
        mCardScrollView = new CardScrollView(this);
        mCardScrollAdapter = new CardScrollAdapter() {
            @Override
            public int getCount() {
                return mCards.size();
            }

            @Override
            public Object getItem(int position) {
                return mCards.get(position);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mCards.get(position).getView(convertView, parent);
            }

            @Override
            public int getPosition(Object item) {
                return mCards.indexOf(item);
            }

            @Override
            public int getItemViewType(int position){
                return mCards.get(position).getItemViewType();
            }

            @Override
            public int getViewTypeCount() {
                return CardBuilder.getViewTypeCount();
            }
        };
        mCardScrollView.setAdapter(mCardScrollAdapter);
        // Handle the TAP event.
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Plays tap sound to indicate that TAP actions are supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.TAP);
                openOptionsMenu();
            }
        });
        mCardScrollView.activate();
        setContentView(mCardScrollView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.measuring_menu_item:
//                // Alternative way to start class. Does not require a new Action.
//                Intent intent = new Intent(this, InformationActivity.class);
//                this.startActivity(intent);
//                break;
                startActivity(new Intent(this, MeasuringActivity.class));
                return true;
            case R.id.information_menu_item:
                startActivity(new Intent(this, InformationActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startThread(){
        Log.i("startThread() called", " ");

            try {
                Log.i("Entered try block.", " ");
                Thread thread = new Thread(new Runnable() {
//                    private DatagramSocket mSocket = new DatagramSocket(55056, InetAddress.getByName("10.1.17.188"));
                    private DatagramSocket mSocket = new DatagramSocket(61557, InetAddress.getByName("10.0.0.15")); //Use Glass IP address here
                    private DatagramPacket mPacket;
                    public String message;

                    public String joint1, joint2, joint3, joint4, joint5, joint6, joint7;

                    @Override
                    public void run() {

                        Log.i("thread.run.start"," ");

                        while (true) {
                            byte[] buf = new byte[56];
                            mPacket = new DatagramPacket(buf, buf.length);

                            try {
                                Thread.sleep(10, 0);
                                mSocket.receive(mPacket);
                                byte[] data = mPacket.getData();



//                                byte[] j2byte = Arrays.copyOfRange(byte[] buff, int 8, int 15);
                                double[] jointDoubleArray = new double[7];
                                String[] jointStringArray = new String[7];
                                for(int i=0; i<7; i++){
                                    int n = i+1;
                                    jointDoubleArray[i] = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble();
                                    jointStringArray[i] = String.valueOf(jointDoubleArray[i]);
                                    Log.i("Joint "+n+" ", jointStringArray[i]);
                                }
//                                double j1 = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble();
//                                double j2 = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble();
//                                double j3 = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble();
//                                double j4 = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble();
//                                double j5 = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble();
//                                double j6 = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble();
//                                double j7 = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble();
//
//                                joint1 = String.valueOf(j1);
//                                joint2 = String.valueOf(j2);
//                                joint3 = String.valueOf(j3);
//                                joint4 = String.valueOf(j4);
//                                joint5 = String.valueOf(j5);
//                                joint6 = String.valueOf(j6);
//                                joint7 = String.valueOf(j7);


//                                Log.i("All Bytes: ", sb.toString());
//                                Log.i("Joint Positions: "+joint1+" "+joint2+" "+joint3+" "+joint4+" "+joint5+" "+joint6+" "+joint7, " ");
//                                Log.i("Joint 1:", joint1);
//                                Log.i("Joint 2:", joint2);
//                                Log.i("Joint 3:", joint3);
//                                Log.i("Joint 4:", joint4);
//                                Log.i("Joint 5:", joint5);
//                                Log.i("Joint 6:", joint6);
//                                Log.i("Joint 7:", joint7);

                                // Sends updated joint position value to MeasuringActivity
                                Intent intent = new Intent(getApplicationContext(), MeasuringActivity.class);
                                intent.putExtra("joint1pos", jointStringArray[0]);
                                intent.putExtra("joint2pos", jointStringArray[1]);
                                intent.putExtra("joint3pos", jointStringArray[2]);
                                intent.putExtra("joint4pos", jointStringArray[3]);
                                intent.putExtra("joint5pos", jointStringArray[4]);
                                intent.putExtra("joint6pos", jointStringArray[5]);
                                intent.putExtra("joint7pos", jointStringArray[6]);

                            } catch (IOException e) {
                                Log.i("IOException ", e.getMessage());
                            } catch (InterruptedException e) {
                                Log.i("InterruptedException ", e.getMessage());
                            }
                        }

                    }

                });
                thread.start();
            } catch (BindException e) {
                Log.i("BindEx.",  e.getMessage());
            } catch (ConnectException e) {
                Log.i("ConnectEx.",  e.getMessage());
            } catch (NoRouteToHostException e) {
                Log.i("NoRouteToHostException.",  e.getMessage());
            } catch (PortUnreachableException e) {
                Log.i("PrtUnreachbleException.",  e.getMessage());
            } catch (SocketException e) {
                Log.i("SocketException",  e.getMessage());
            } catch (UnknownHostException e) {
                Log.i("UnknownHostException", e.getMessage());
            }
    }

}
