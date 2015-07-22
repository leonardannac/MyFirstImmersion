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
        Log.i("setContentView", " ");

        // Justin is awesome

//        for (int count =0; count< 7; count++)
//        {
//            final String xmlSwitcherId = new String ("joint" + count+1 );
//            jointSwitcherArray[count] = (TextSwitcher) findViewById(switcherId[count]);
////            jointSwitcherArray[count].setFactory(this);
//            jointSwitcherArray[count].setFactory(new ViewSwitcher.ViewFactory() {
//                public View makeView() {
//                    TextView tv = new TextView(MeasuringActivity.this);
//                    tv.setTextSize(22);
//                    Log.i("mS makeView() ",xmlSwitcherId);
//                    return tv;
//                }
//            });
//            jointSwitcherArray[count].setText("0.00");
//            Log.i("mS.setText"," ");
//        }
        joint1Switcher = (TextSwitcher) findViewById(R.id.joint1switcher);
        joint2Switcher = (TextSwitcher) findViewById(R.id.joint2switcher);
        joint3Switcher = (TextSwitcher) findViewById(R.id.joint3switcher);
        joint4Switcher = (TextSwitcher) findViewById(R.id.joint4switcher);
        joint5Switcher = (TextSwitcher) findViewById(R.id.joint5switcher);
        joint6Switcher = (TextSwitcher) findViewById(R.id.joint6switcher);
        joint7Switcher = (TextSwitcher) findViewById(R.id.joint7switcher);

        joint1Switcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView tv = new TextView(MeasuringActivity.this);
                tv.setTextSize(20);
                Log.i("mS1 makeView()", " ");
                return tv;
            }
        });
        joint2Switcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView tv = new TextView(MeasuringActivity.this);
                tv.setTextSize(20);
                Log.i("mS2 makeView()", " ");
                return tv;
            }
        });
        joint3Switcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView tv = new TextView(MeasuringActivity.this);
                tv.setTextSize(20);
                Log.i("mS3 makeView()", " ");
                return tv;
            }
        });
        joint4Switcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView tv = new TextView(MeasuringActivity.this);
                tv.setTextSize(20);
                Log.i("mS4 makeView()", " ");
                return tv;
            }
        });
        joint5Switcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView tv = new TextView(MeasuringActivity.this);
                tv.setTextSize(20);
                Log.i("mS5 makeView()", " ");
                return tv;
            }
        });
        joint6Switcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView tv = new TextView(MeasuringActivity.this);
                tv.setTextSize(20);
                Log.i("mS6 makeView()", " ");
                return tv;
            }
        });
        joint7Switcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView tv = new TextView(MeasuringActivity.this);
                tv.setTextSize(20);
                Log.i("mS7 makeView()", " ");
                return tv;
            }
        });

        joint1Switcher.setText("there is some text here.");
        joint2Switcher.setText("and here too, hopefully.");
        joint3Switcher.setText("there is some text here.");
        joint4Switcher.setText("and here too, hopefully.");
        joint5Switcher.setText("there is some text here.");
        joint6Switcher.setText("and here too, hopefully.");
        joint7Switcher.setText("there is some text here.");

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
//        Log.i("startThread() called", " ");

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

                    while (true) {
                        byte[] buf = new byte[56];
                        mPacket = new DatagramPacket(buf, buf.length);

                        try {
                            Thread.sleep(10, 0);
                            mSocket.receive(mPacket);
//                            byte[] data = mPacket.getData();


//                            byte[] j2byte = Arrays.copyOfRange(byte[] buff, int 8, int 15);
                            double[] jointDoubleArray = new double[7];
                            final String[] jointStringArray = new String[7];
                            for(int i=0; i<7; i++){
                                int n = i+1;
                                jointDoubleArray[i] = ByteBuffer.wrap(mPacket.getData()).order(ByteOrder.LITTLE_ENDIAN).getDouble();
                                jointStringArray[i] = String.valueOf(Math.toRadians(jointDoubleArray[i]));
//                                Log.i("Joint "+n+" ", jointStringArray[i]);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run(){

//                                    for(int i=0; i<7; i++)
//                                    {
//                                        Log.i("Joint "+i+": ", jointStringArray[0]);
//                                        jointSwitcherArray[i].setText(jointStringArray[i]);
//                                    }
//                                    makeView().invalidate();

                                    joint1Switcher.setText(jointStringArray[0]);
                                    joint2Switcher.setText(jointStringArray[1]);
                                    joint3Switcher.setText(jointStringArray[2]);
                                    joint4Switcher.setText(jointStringArray[3]);
                                    joint5Switcher.setText(jointStringArray[4]);
                                    joint6Switcher.setText(jointStringArray[5]);
                                    joint7Switcher.setText(jointStringArray[6]);
//                                    Log.i("Joint 1",joint1pos);
//                                    Log.i("Joint 2",joint2pos);
//                                    Log.i("Joint 3",joint3pos);
//                                    Log.i("Joint 4",joint4pos);
//                                    Log.i("Joint 5",joint5pos);
//                                    Log.i("Joint 6",joint6pos);
//                                    Log.i("Joint 7",joint7pos);
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
