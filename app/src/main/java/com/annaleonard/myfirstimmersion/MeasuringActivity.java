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
import java.text.DecimalFormat;

public class MeasuringActivity extends Activity implements ViewSwitcher.ViewFactory, View.OnClickListener {
    //TextSwitchers and ids that are used to update the xml layout displayed on the glass
    private TextSwitcher joint1Switcher,joint2Switcher,joint3Switcher,joint4Switcher,joint5Switcher,joint6Switcher,joint7Switcher;  //even though these are grayed out, you can't delete them
    private TextSwitcher[] jointSwitcherArray = {joint1Switcher,joint2Switcher,joint3Switcher,joint4Switcher,joint5Switcher,joint6Switcher,joint7Switcher};
    private int [] switcherId = {R.id.joint1switcher,R.id.joint2switcher, R.id.joint3switcher, R.id.joint4switcher, R.id.joint5switcher, R.id.joint6switcher, R.id.joint7switcher};

    DecimalFormat jointPosFormat = new DecimalFormat("0.00");   //format to specify sig figs

    private volatile Thread backgroundThread;   //flag to cleanly stop background thread

    private DatagramSocket mSocket;
    private DatagramPacket mPacket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startThread();

        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS); //Enable voice activated menu
        setContentView(R.layout.activity_measuring);    //Set the desired layout to display on screen

        makeAllJointTextSwitchers();

    }

    @Override
    protected void onDestroy(){
        stopThread();
        super.onDestroy();
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
                    return true;
                case R.id.showJoint5:
                    setContentView(R.layout.show_joint);
                    return true;
                case R.id.showJoint6:
                    setContentView(R.layout.show_joint);
                    return true;
                case R.id.showJoint7:
                    setContentView(R.layout.show_joint);
                    return true;
                case R.id.showAllJoints:
                    setContentView(R.layout.activity_measuring);
                    makeAllJointTextSwitchers();
                    return true;
                default:
                    return super.onMenuItemSelected(featureId, item);
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void makeAllJointTextSwitchers(){
//        for (int count =0; count< 7; count++)
//        {
//            jointSwitcherArray[count] = (TextSwitcher) findViewById(switcherId[count]); //attaches each switcher to its xml id
//            jointSwitcherArray[count].setFactory(this);
//            jointSwitcherArray[count].setText("0.00");
//            Log.i("mS.setText", String.valueOf(count));
//        }

        for (int count =0; count< 7; count++)
        {
            jointSwitcherArray[count] = (TextSwitcher) findViewById(switcherId[count]);
            jointSwitcherArray[count].setFactory(new ViewSwitcher.ViewFactory() {
                public View makeView() {
                    TextView tv = new TextView(MeasuringActivity.this);
                    tv.setTextSize(22);
                    return tv;
                }
            });
            jointSwitcherArray[count].setText("0.00");
            Log.i("mS.setText"," ");
        }
    }

    public View makeView(){
        //unsure where this is called but required -> might be default of .setFactory()
        TextView t = new TextView(this);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(28);
        return t;
    }

    public void onClick(View v){
        //This doesn't work but is also necessary. Could be that voice menu overrides the touch menu completely
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.playSoundEffect(Sounds.DISALLOWED);
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
                final String[] jointStringArray = new String[7];
                for (int i = 0; i < 7; i++) {
                    jointDoubleArray[i] = ByteBuffer.wrap(mPacket.getData()).order(ByteOrder.LITTLE_ENDIAN).getDouble(i*8);
//                    jointStringArray[i] = String.valueOf(Math.toRadians(jointDoubleArray[i]));  //convert to Radians
                    jointStringArray[i] = String.valueOf(jointPosFormat.format(jointDoubleArray[i]));
                }

                //This method says 'hey UI thread! i don't know what to do with this. can you run this code?'
                //All methods and variables available in the UI thread are available inside runOnUiThread
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 0; i < 7; i++) {
                            Log.i("Joint " + i + ": ", jointStringArray[i]);
                            jointSwitcherArray[i].setText(jointStringArray[i]);
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
