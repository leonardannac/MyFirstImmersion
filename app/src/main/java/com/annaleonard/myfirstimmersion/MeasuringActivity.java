package com.annaleonard.myfirstimmersion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.view.WindowUtils;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MeasuringActivity extends Activity implements ViewSwitcher.ViewFactory, View.OnClickListener {

    String joint1pos, joint2pos,joint3pos,joint4pos,joint5pos,joint6pos,joint7pos;
    private EditText desiredJoint, desiredJointPos;
    private TextSwitcher joint1Switcher,joint2Switcher,joint3Switcher,joint4Switcher,joint5Switcher,joint6Switcher,joint7Switcher;
    private TextSwitcher[] jointSwitcherArray = {joint1Switcher,joint2Switcher,joint3Switcher,joint4Switcher,joint5Switcher,joint6Switcher,joint7Switcher};
    private int [] switcherId = {R.id.joint1switcher,R.id.joint2switcher, R.id.joint3switcher, R.id.joint4switcher, R.id.joint5switcher, R.id.joint6switcher, R.id.joint7switcher};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startThread();

        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        setContentView(R.layout.activity_measuring);
        Log.i("setContentView", "message");

        // Justin is awesome
        // This does not allow you to directly change the size of the textSwitchers.
        for (int count =0; count< 7; count++)
        {
            jointSwitcherArray[count] = (TextSwitcher) findViewById(switcherId[count]);//attaches each switcher to its xml id
            jointSwitcherArray[count].setFactory(this);
            jointSwitcherArray[count].setText("0.00");
            Log.i("mS.setText", String.valueOf(count));
        }
        // This works the same as the loop above but allows more control over the size of the textSwitchers.
//        for (int count =0; count< 7; count++)
//        {
////            final String xmlSwitcherId = new String ("joint" + count+1 );
//            jointSwitcherArray[count] = (TextSwitcher) findViewById(switcherId[count]);
////            jointSwitcherArray[count].setFactory(this);
//            jointSwitcherArray[count].setFactory(new ViewSwitcher.ViewFactory() {
//                public View makeView() {
//                    TextView tv = new TextView(MeasuringActivity.this);
//                    tv.setTextSize(22);
////                    Log.i("mS makeView() ",xmlSwitcherId);
//                    return tv;
//                }
//            });
//            jointSwitcherArray[count].setText("0.00");
//            Log.i("mS.setText"," ");
//        }

    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.menu_measuring, menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_measuring, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS){
            switch(item.getItemId()) {
                case R.id.showJoint1:
                    setContentView(R.layout.joint_1);
                    return true;
                case R.id.showJoint2:
                    setContentView(R.layout.joint_2);
                    return true;
                case R.id.showJoint3:
                    setContentView(R.layout.joint_3);
                    return true;
                case R.id.showJoint4:
                    setContentView(R.layout.show_joint);
//                    setContentView(R.layout.show_joint_4);
                    return true;
                case R.id.showJoint5:
                    setContentView(R.layout.show_joint);
//                    setContentView(R.layout.show_joint_5);
                    return true;
                case R.id.showJoint6:
                    setContentView(R.layout.show_joint);
//                    setContentView(R.layout.show_joint_6);
                    return true;
                case R.id.showJoint7:
                    setContentView(R.layout.show_joint);
//                    setContentView(R.layout.show_joint_7);
                    return true;
                case R.id.showAllJoints:
                    setContentView(R.layout.activity_measuring);
//                    setContentView(R.layout.show_all_joints);
                    return true;
                default:
                    return super.onMenuItemSelected(featureId, item);
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public View makeView(){
        TextView t = new TextView(this);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(28);
        return t;
    }

    public void onClick(View v){
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.playSoundEffect(Sounds.DISALLOWED);
    }

    public void startThread(){
        Log.i("startThread() called", "message");

        try {
            Log.i("Entered try block.", "message");
            Thread thread = new Thread(new Runnable() {
//                private DatagramSocket mSocket = new DatagramSocket(55056, InetAddress.getByName("10.1.17.188"));
                private DatagramSocket mSocket = new DatagramSocket(61557, InetAddress.getByName("10.0.0.15")); //Use Glass IP address here
                private DatagramPacket mPacket;

                @Override
                public void run() {

//                    Log.i("thread.run.start","message");

                    while (true) {
                        byte[] buf = new byte[56];
//                        Log.i("byte[] buf = new byte","msg");
                        mPacket = new DatagramPacket(buf, buf.length);
//                        Log.i("mPacket =","new DatagramPacket");

                        try {
//                            Log.i("second try block","msg");
                            Thread.sleep(10, 0);
//                            Log.i("before mS.receive","msg");
                            mSocket.receive(mPacket);
//                            Log.i("after mS.receive","msg");

                            double[] jointDoubleArray = new double[7];
                            final String[] jointStringArray = new String[7];
                            for(int i=0; i<7; i++){
//                                int n = i+1;
                                jointDoubleArray[i] = ByteBuffer.wrap(mPacket.getData()).order(ByteOrder.LITTLE_ENDIAN).getDouble();
                                jointStringArray[i] = String.valueOf(Math.toRadians(jointDoubleArray[i]));
//                                Log.i("Joint "+n+" ", jointStringArray[i]);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run(){

                                    for(int i=0; i<7; i++)
                                    {
                                        Log.i("Joint "+i+": ", jointStringArray[i]);
                                        jointSwitcherArray[i].setText(jointStringArray[i]);
                                    }

                                }
                            });

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
