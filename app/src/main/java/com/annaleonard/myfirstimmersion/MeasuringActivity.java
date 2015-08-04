package com.annaleonard.myfirstimmersion;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.view.WindowUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;

public class MeasuringActivity extends Activity implements ViewSwitcher.ViewFactory, View.OnClickListener {
    //TextSwitchers and ids that are used to update the xml layout displayed on the glass
    private TextSwitcher [] jointSwitcherArray = new TextSwitcher[7];    //Array containing text switchers for all joints view
    private TextSwitcher desiredJoint, desiredJointPos;    //Text Switchers for single joints view
    private int [] switcherId = {R.id.joint_a_val,R.id.joint_b_val, R.id.joint_c_val, R.id.joint_d_val, R.id.joint_e_val, R.id.joint_f_val, R.id.joint_g_val};    //xml locations of switchers for all joints view

    DecimalFormat jointPosFormat = new DecimalFormat("0.00");   //format to specify sig figs

    private volatile Thread backgroundThread;   //flag to cleanly stop background thread

    private DatagramSocket mSocket;
    private DatagramPacket mPacket;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.playSoundEffect(Sounds.TAP);
            openOptionsMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //On Create is called when the application is first opened
    //The bundle saves the state of the app in case the app is being reopened.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startThread();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keeps activity awake

        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);//Enable voice activated menu
        setContentView(R.layout.activity_measuring);  //Set the desired layout to display on screen

        //Set up 7 text switchers for all joint view.  One for each joint.
        makeAllJointTextSwitchers();
    }

    @Override
    //Stops the background thread of data collection when activity is destroyed
    protected void onDestroy(){
        stopThread();
        super.onDestroy();
    }


    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if ((featureId == WindowUtils.FEATURE_VOICE_COMMANDS) || (featureId == Window.FEATURE_OPTIONS_PANEL)) {
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



//2 flags
    int whichJoint = -1;
    String jointValue = "No Value Set";
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int [] showJoints = {R.id.showJoint1, R.id.showJoint2, R.id.showJoint3, R.id.showJoint4, R.id.showJoint5, R.id.showJoint6, R.id.showJoint7};

        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId == Window.FEATURE_OPTIONS_PANEL)
        {
            switch (item.getItemId())
            {
                case (R.id.showAllJoints):
                    //returns to layout with all 7 joints.
                    setContentView(R.layout.activity_measuring);
                    makeAllJointTextSwitchers();
                    whichJoint = -1;
                    return true;

//                case (R.id.double_joint_option):
//                    setContentView(R.layout.show_2_joints);
//                    break;

                case (R.id.single_joint_option):
                    //sets view to single joint layout, but does not set switchers
                    setContentView(R.layout.show_1_joint);
                    makeSingleJointTextSwitchers();
                    break;

                //each option below individually sets the switchers in the single joint view to display the name and data for that particular joint.
                case (R.id.showJoint1):
                    whichJoint = 1;
                    desiredJoint.setText("Joint 1");
                    break;

                case (R.id.showJoint2):
                    whichJoint = 2;
                    desiredJoint.setText("Joint 2");
                    break;

                case (R.id.showJoint3):
                    whichJoint = 3;
                    desiredJoint.setText("Joint 3");
                    break;

                case (R.id.showJoint4):
                    whichJoint = 4;
                    desiredJoint.setText("Joint 4");
                    break;

                case (R.id.showJoint5):
                    whichJoint = 5;
                    desiredJoint.setText("Joint 5");
                    break;

                case (R.id.showJoint6):
                    whichJoint = 6;
                    desiredJoint.setText("Joint 6");
                    break;

                case (R.id.showJoint7):
                    whichJoint = 7;
                    desiredJoint.setText("Joint 7");
                    break;


                //the default never gets called.
                default:
                    return super.onMenuItemSelected(featureId, item);

            }
        }

        return super.onMenuItemSelected(featureId, item);
    }

    public void makeAllJointTextSwitchers(){
        for (int count =0; count< 7; count++)
        {
            final int i=count;
            jointSwitcherArray[count] = (TextSwitcher) findViewById(switcherId[count]);
            jointSwitcherArray[count].setFactory(new ViewSwitcher.ViewFactory() {
                public View makeView() {
                    TextView tv = new TextView(MeasuringActivity.this);
                    tv.setTextSize(26);
                    return tv;
                }
            });
            jointSwitcherArray[count].setText("0.00");
            Log.i("mS.setText"," ");
        }
    }


    void makeSingleJointTextSwitchers()
    {
        desiredJoint = (TextSwitcher) findViewById(R.id.desired_joint);//attaches each switcher to its xml id
        desiredJoint.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {
                TextView t = new TextView(getApplicationContext());
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                t.setTextSize(40);
                return t;
            }
        });


        desiredJointPos = (TextSwitcher) findViewById(R.id.desired_joint_pos);//attaches each switcher to its xml id
        desiredJointPos.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {
                TextView t = new TextView(getApplicationContext());
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                t.setTextSize(90);
                return t;
            }
        });
        desiredJointPos.setText("0.0");
    }

    public View makeView(){
        TextView t = new TextView(this);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(20);
        return t;
    }

    public void onClick(View v){
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.playSoundEffect(Sounds.SUCCESS);
    }




    final String[] jointStringArray = new String[7];
    //The background thread where all the data collection occurs
    public void startThread(){
        //Create a thread, define it's run() method, and start the thread
        backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runThread();
            }
        });
        backgroundThread.start();
    }

    public void stopThread(){
        mSocket.close();    //Socket must be closed here or 'SocketException: bind failed: EADDRINUSE'
        backgroundThread = null;    //Asks the thread to stop nicely by setting flag var
    }

    public void runThread(){
        Thread thisThread = Thread.currentThread();
        if (mSocket == null){   //possibly can get rid of this check if the socket exists
            Log.i("mSocket","null");
            try {
                mSocket = new DatagramSocket(61557, InetAddress.getByName("10.0.0.15")); //Use Glass IP address here
            } catch (UnknownHostException e){
                Log.i("UnknownHostException",e.getMessage());
            } catch (SocketException e){
                Log.i("SocketException",e.getMessage());
            }
        }
        while (backgroundThread == thisThread) {    //while backgroundThread has not been asked to stop
            byte[] buf = new byte[56];
            mPacket = new DatagramPacket(buf, buf.length);

            try {
                Thread.sleep(10, 0);
                mSocket.receive(mPacket);

                double[] jointDoubleArray = new double[7];

                for (int i = 0; i < 7; i++) {
                    jointDoubleArray[i] = ByteBuffer.wrap(mPacket.getData()).order(ByteOrder.LITTLE_ENDIAN).getDouble(i * 8);
//                    jointStringArray[i] = String.valueOf(Math.toRadians(jointDoubleArray[i]));  //convert to Radians
                    jointStringArray[i] = String.valueOf(jointPosFormat.format(jointDoubleArray[i]));
                }
                final LimitMonitor onlyOne = new LimitMonitor(jointDoubleArray);

                //This method says 'hey UI thread! i don't know what to do with this. can you run this code?'
                //All methods and variables available in the UI thread are available inside runOnUiThread
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (whichJoint == -1) {
                            for (int i = 0; i < 7; i++) {
                                Log.i("Joint " + i + ": ", jointStringArray[i]);
                                if(!(onlyOne.getLimitHit()[i]))
                                {
                                    jointSwitcherArray[i].setBackgroundColor(Color.RED);
                                }
                                jointSwitcherArray[i].setText(jointStringArray[i]);
                            }
                        } else {
                            desiredJointPos.setText(jointStringArray[whichJoint - 1]);
                        }

                    }
                });

            } catch (InterruptedException e) {
                Log.i("InterruptedException",e.getMessage());
            } catch (IOException e) {
                Log.i("IOException",e.getMessage());
            }
        }   //Justin Brannan is awesome and helps poor lost souls with git.
    }
}
