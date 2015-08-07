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
    private int [] layoutId = {R.id.joint_a, R.id.joint_b, R.id.joint_c, R.id.joint_d, R.id.joint_e, R.id.joint_f, R.id.joint_g};

    DecimalFormat jointPosFormat = new DecimalFormat("0.00");   //format to specify sig figs

    private volatile Thread backgroundThread;   //flag to cleanly stop background thread

    private DatagramSocket mSocket;
    private DatagramPacket mPacket;

    final String[] jointStringArray = new String[7];
    int whichJoint = -1;

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

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int [] showJoints = {R.id.showJoint1, R.id.showJoint2, R.id.showJoint3, R.id.showJoint4, R.id.showJoint5, R.id.showJoint6, R.id.showJoint7};
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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
                    whichJoint=0;
                    break;

                //each option below individually sets the switchers in the single joint view to display the name and data for that particular joint.
                case (R.id.showJoint1):
                    am.playSoundEffect(Sounds.TAP);
                    whichJoint = 1;
                    desiredJoint.setText("Joint 1");
                    break;

                case (R.id.showJoint2):
                    am.playSoundEffect(Sounds.TAP);
                    whichJoint = 2;
                    desiredJoint.setText("Joint 2");
                    break;

                case (R.id.showJoint3):
                    am.playSoundEffect(Sounds.TAP);
                    whichJoint = 3;
                    desiredJoint.setText("Joint 3");
                    break;

                case (R.id.showJoint4):
                    am.playSoundEffect(Sounds.TAP);
                    whichJoint = 4;
                    desiredJoint.setText("Joint 4");
                    break;

                case (R.id.showJoint5):
                    am.playSoundEffect(Sounds.TAP);
                    whichJoint = 5;
                    desiredJoint.setText("Joint 5");
                    break;

                case (R.id.showJoint6):
                    am.playSoundEffect(Sounds.TAP);
                    whichJoint = 6;
                    desiredJoint.setText("Joint 6");
                    break;

                case (R.id.showJoint7):
                    am.playSoundEffect(Sounds.TAP);
                    whichJoint = 7;
                    desiredJoint.setText("Joint 7");
                    break;

                default:
                    return super.onMenuItemSelected(featureId, item);

            }
        }

        return super.onMenuItemSelected(featureId, item);
    }

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
//            Log.i("mS.setText"," ");
        }
    }

    void makeSingleJointTextSwitchers() {
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
        am.playSoundEffect(Sounds.TAP);
    }

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
        try {                   //Have to try in case there isn't an internet connection and socket wasn't created.
            mSocket.close();    //Socket must be closed here or 'SocketException: bind failed: EADDRINUSE'
        } catch(NullPointerException e){
        }
        backgroundThread = null;    //Asks the thread to stop nicely by setting flag var
    }

    public void runThread(){
        Thread thisThread = Thread.currentThread(); //set flag to current thread
        //check that the socket does not exist already before creating and binding it
        if (mSocket == null){
            try {
                mSocket = new DatagramSocket(61557, InetAddress.getByName("10.0.0.15")); //Use Glass IP address here
            } catch (UnknownHostException e){
                e.printStackTrace();
            } catch (SocketException e){
                e.printStackTrace();
            }
        }

        //while the backgroundThread has not been asked to stop
        while (backgroundThread == thisThread) {
            byte[] buf = new byte[56];
            mPacket = new DatagramPacket(buf, buf.length);

            try {
                Thread.sleep(10, 0);

                try{
                    mSocket.receive(mPacket);   //receive UDP packet
                } catch (NullPointerException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //NullPointerException indicates that mSocket was not created --> no internet connection
                            setContentView(R.layout.no_internet_connection_layout);
                        }
                    });
                    break;
                }

                //Get data from UDP packet and convert to user-ready information    (joint values are in degrees)
                double[] jointDoubleArray = new double[7];
                for (int i = 0; i < 7; i++) {
                    jointDoubleArray[i] = ByteBuffer.wrap(mPacket.getData()).order(ByteOrder.LITTLE_ENDIAN).getDouble(i * 8);
                    jointStringArray[i] = String.valueOf(jointPosFormat.format(jointDoubleArray[i]));
                }

                final LimitMonitor onlyOne = new LimitMonitor(jointDoubleArray);

                //RunOnUiThread method says 'hey UI thread! i don't know what to do with this. can you run this code?'
                //All methods and variables available in the UI thread are available inside runOnUiThread
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (whichJoint == -1) {
                            for (int i = 0; i < 7; i++) {
                                onlyOne.updateGUI(findViewById(layoutId[i]), i);
                                jointSwitcherArray[i].setText(jointStringArray[i]);
                            }
                        } else {
                            if (whichJoint > 0) {
                            desiredJointPos.setText(jointStringArray[whichJoint - 1]);
                            onlyOne.updateGUI(findViewById(R.id.layout), whichJoint - 1);
                            }
                        }
                    }
                });

            } catch (InterruptedException e) {
//                Log.i("InterruptedException",e.getMessage());
            } catch (IOException e) {
//                Log.i("IOException",e.getMessage());
            }
        }   //Justin Brannan is awesome and helps poor lost souls with git.
    }
}
