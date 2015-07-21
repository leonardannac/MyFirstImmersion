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

    private TextSwitcher desiredJoint, desiredJointPos;
    private int [] switcherId = {R.id.joint1switcher,R.id.joint2switcher, R.id.joint3switcher, R.id.joint4switcher, R.id.joint5switcher, R.id.joint6switcher, R.id.joint7switcher};
    TextSwitcher[] jointSwitcherArray = new TextSwitcher[7];


    //Attaches textSwitcher to xml resource, and creates to child views to switch between
    //DOES NOT WORK
    public void switcherSetup (TextSwitcher switcher, int switcherXMLResource)
    {
        switcher = (TextSwitcher) findViewById(switcherXMLResource);//attaches each switcher to its xml id
        switcher.setFactory(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startThread();

        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        setContentView(R.layout.activity_measuring);
        Log.i("setContentView", " ");

        //Sets up switcher for all 7 joints and has them display a value of 0
        for (int count =0; count< 7; count++)
        {
            jointSwitcherArray[count] = (TextSwitcher) findViewById(switcherId[count]);//attaches each switcher to its xml id
            jointSwitcherArray[count].setFactory(this);
            jointSwitcherArray[count].setText("0.00");
            Log.i("mS.setText", String.valueOf(count));
        }

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
        int [] showJoints = {R.id.showJoint1, R.id.showJoint2, R.id.showJoint3, R.id.showJoint4, R.id.showJoint5, R.id.showJoint6, R.id.showJoint7};
        boolean jointSelected = false;
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            //switch(item.getItemId()) {

            if (item.getItemId() == R.id.single_joint_option) {
                //switcherSetup(desiredJoint, R.id.desired_joint);
                desiredJoint = (TextSwitcher) findViewById(R.id.desired_joint);//attaches each switcher to its xml id
                desiredJoint.setFactory(this);
                desiredJoint.setText("does it show up?");

                //switcherSetup(desiredJointPos, R.id.desired_joint_pos);
                desiredJointPos = (TextSwitcher) findViewById(R.id.desired_joint_pos);//attaches each switcher to its xml id
                desiredJointPos.setFactory(this);
                desiredJointPos.setText("it should...");

                setContentView(R.layout.show_joint);

//                switch (item.getItemId()) {
//                    case R.id.showJoint1:
//                        desiredJoint.setText("Joint 1");
//                        jointSelected = true;
//                    case R.id.showJoint2:
//                        desiredJoint.setText("Joint 2");
//                        jointSelected = true;
//                    case R.id.showJoint3:
//                        desiredJoint.setText("Joint 3");
//                        jointSelected = true;
//                    case R.id.showJoint4:
//                        desiredJoint.setText("Joint 4");
//                        jointSelected = true;
//                    case R.id.showJoint5:
//                        desiredJoint.setText("Joint 5");
//                        jointSelected = true;
//                    case R.id.showJoint6:
//                        desiredJoint.setText("Joint 6");
//                        jointSelected = true;
//                    case R.id.showJoint7:
//                        desiredJoint.setText("Joint 7");
//                        jointSelected = true;
//                    default:
//                        jointSelected = false;
//                        break;
//                }

                //setContentView(R.layout.show_joint);

                if (jointSelected) {
                    return jointSelected;
                }
                else
                {
                    return super.onMenuItemSelected(featureId, item);
                }
            }

            else if (R.id.showAllJoints == item.getItemId())
            {
                setContentView(R.layout.activity_measuring);
                return true;
            }

            else
            {
                return super.onMenuItemSelected(featureId, item);
            }
        }

        return super.onMenuItemSelected(featureId, item);
    }

    public View makeView(){
        TextView t = new TextView(this);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(24);
        return t;
    }

    public void onClick(View v){
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.playSoundEffect(Sounds.DISALLOWED);
    }

    public void startThread(){
        Log.i("startThread() called", " ");

        try {
//            Log.i("Entered try block.", " ");
            Thread thread = new Thread(new Runnable() {
//                private DatagramSocket mSocket = new DatagramSocket(55056, InetAddress.getByName("10.1.17.188"));
                private DatagramSocket mSocket = new DatagramSocket(61557, InetAddress.getByName("10.0.0.15")); //Use Glass IP address here
                private DatagramPacket mPacket;
                public String message;

                public String joint1, joint2, joint3, joint4, joint5, joint6, joint7;

                @Override
                public void run() {

//                    Log.i("thread.run.start"," ");

                    while (true)
                    {
                                byte[] buf = new byte[56];
                        mPacket = new DatagramPacket(buf, buf.length);

                        try {

                            Thread.sleep(10, 0);
                            mSocket.receive(mPacket);
//                            byte[] data = mPacket.getData();


//                            byte[] j2byte = Arrays.copyOfRange(byte[] buff, int 8, int 15);

                            //creates double array to store all joint values recieved
                            //converts doubles to strings for display
                            double[] jointDoubleArray = new double[7];
                            final String[] jointStringArray = new String[7];
                            for(int i=0; i<7; i++){
                                int n = i+1;
                                jointDoubleArray[i] = ByteBuffer.wrap(mPacket.getData()).order(ByteOrder.LITTLE_ENDIAN).getDouble();
                                jointStringArray[i] = String.valueOf(Math.toRadians(jointDoubleArray[i]));
//                                Log.i("Joint "+n+" ", jointStringArray[i]);
                            }

                            Log.i("about to run on UI", "UI Thread Running ");


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    //prints position of each joint to GUI
                                    for(int i=0; i<7; i++)
                                    {
                                        Log.i("Joint "+ String.valueOf(i+1) + ": ", jointStringArray[0]);
                                        jointSwitcherArray[i].setText(jointStringArray[i]);
                                    }
                                }
                            });
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

//                            // Sends updated joint position value to MeasuringActivity
//                            Intent intent = new Intent(getApplicationContext(), MeasuringActivity.class);
//                            intent.putExtra("joint1pos", jointStringArray[0]);
//                            intent.putExtra("joint2pos", jointStringArray[1]);
//                            intent.putExtra("joint3pos", jointStringArray[2]);
//                            intent.putExtra("joint4pos", jointStringArray[3]);
//                            intent.putExtra("joint5pos", jointStringArray[4]);
//                            intent.putExtra("joint6pos", jointStringArray[5]);
//                            intent.putExtra("joint7pos", jointStringArray[6]);

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
