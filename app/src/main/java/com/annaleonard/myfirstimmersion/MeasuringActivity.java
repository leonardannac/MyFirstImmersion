package com.annaleonard.myfirstimmersion;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.view.WindowUtils;

public class MeasuringActivity extends Activity {

    private EditText joint1pos, joint2pos,joint3pos,joint4pos,joint5pos,joint6pos,joint7pos;
    private EditText desiredJoint, desiredJointPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        setContentView(R.layout.activity_measuring);

        //Listener for Joint Position Updates from MainActivity background thread
//        joint1pos = (EditText) findViewById(R.id.joint1pos);
//        joint2pos = (EditText) findViewById(R.id.joint2pos);
//        joint3pos = (EditText) findViewById(R.id.joint3pos);
//        joint4pos = (EditText) findViewById(R.id.joint4pos);
//        joint5pos = (EditText) findViewById(R.id.joint5pos);
//        joint6pos = (EditText) findViewById(R.id.joint6pos);
//        joint7pos = (EditText) findViewById(R.id.joint7pos);

        Bundle extras = getIntent().getExtras();

//        joint1pos.setText(extras.getString("joint1pos"));
//        joint2pos.setText(extras.getString("joint2pos"));
//        joint3pos.setText(extras.getString("joint3pos"));
//        joint4pos.setText(extras.getString("joint4pos"));
//        joint5pos.setText(extras.getString("joint5pos"));
//        joint6pos.setText(extras.getString("joint6pos"));
//        joint7pos.setText(extras.getString("joint7pos"));

        //Listener for Voice Command


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
}
