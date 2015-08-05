package com.annaleonard.myfirstimmersion;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * Created by jelong3 on 8/4/15.
 */

public class MultipleJointGUI  extends MeasuringActivity
{

    final int [] jointLayout = {R.layout.show_1_joint, R.layout.show_2_joints, R.layout.show_3_joints, R.layout.show_4_joints, R.layout.show_5_joints, R.layout.show_6_joints, R.layout.activity_measuring};

    final int [] titleId = {R.id.joint_a_title, R.id.joint_b_title, R.id.joint_c_title, R.id.joint_d_title, R.id.joint_e_title, R.id.joint_f_title, R.id.joint_g_title};
    final String [] titleStrings = {"Joint 1: ", "Joint 2: ", "Joint 3: ", "Joint 4: ", "Joint 5: ", "Joint 6: ", "Joint 7: "};

    final int [] layoutId = {R.id.joint_a, R.id.joint_b, R.id.joint_c, R.id.joint_d, R.id.joint_e, R.id.joint_f, R.id.joint_g};

    final int [] valId = {R.id.joint_a_val,R.id.joint_b_val, R.id.joint_c_val, R.id.joint_d_val, R.id.joint_e_val, R.id.joint_f_val, R.id.joint_g_val};    //xml locations of switchers for all joints view


//    private RequestActivityMethods viewRequest;
    private int numJoints;
    private int [] whichJoints;
    private Context c;
    public TextSwitcher[] jointValSwitcherArray;
    public TextSwitcher[] jointTitleSwitcherArray;

    public MultipleJointGUI(){
        numJoints = 0;
        whichJoints = new int[numJoints];

    }

//    private MultipleJointGUI(int mNumJoints, int [] mWhichJoints)
//    {
//        numJoints = mNumJoints;x
//        whichJoints = mWhichJoints;
//    }

    public MultipleJointGUI (int[] mWhichJoints, Context mC)
    {
        whichJoints = mWhichJoints;
        numJoints = whichJoints.length;
        c = mC;
    }

    public View[] makeSwitchers(Activity m)
    {
        View [] jointValSwitchers = new View[numJoints];

        for (int count =0; count< numJoints; count++)
        {
            jointTitleSwitcherArray[count] = (TextSwitcher) (getCurrentFocus().findViewById(titleId[count]));
            jointTitleSwitcherArray[count].setFactory(new ViewSwitcher.ViewFactory() {
                public View makeView() {
                    TextView tv = (TextView) this.makeView();

                    switch (numJoints)
                    {
                        case(1):
                            tv.setTextSize(80);
                            break;
                        case(2):
                            tv.setTextSize(55);
                            break;
                        case(3):
                            tv.setTextSize(35);
                            break;
                        case(4):
                            tv.setTextSize(32);
                            break;
                        case(5):
                            tv.setTextSize(30);
                            break;
                        case(6):
                            tv.setTextSize(28);
                            break;
                        case(7):
                            tv.setTextSize(26);
                            break;

                    }

                    return tv;
                }
            });
            jointTitleSwitcherArray[count].setText("No Joint");
    //            Log.i("mS.setText"," ");

            jointValSwitcherArray[count] = (TextSwitcher) getCurrentFocus().findViewById(valId[count]);
            jointValSwitcherArray[count].setFactory(new ViewSwitcher.ViewFactory() {
                public View makeView() {
                    TextView tv = (TextView) this.makeView();

                    switch (numJoints) {
                        case (1):
                            tv.setTextSize(80);
                            break;
                        case (2):
                            tv.setTextSize(55);
                            break;
                        case (3):
                            tv.setTextSize(35);
                            break;
                        case (4):
                            tv.setTextSize(32);
                            break;
                        case (5):
                            tv.setTextSize(30);
                            break;
                        case (6):
                            tv.setTextSize(28);
                            break;
                        case (7):
                            tv.setTextSize(26);
                            break;

                    }

                    return tv;
                }
            });
            jointValSwitcherArray[count].setText("0.00");

        }
        return jointValSwitchers;

    }

    public View setJointLayout (Activity m)
    {

        View currentLayout;
        switch (numJoints)
        {
            case(1):
                m.setContentView(R.layout.show_1_joint);
//                setContentView(R.layout.show_1_joint);
                break;
            case(2):
                m.setContentView(R.layout.show_2_joints);
                break;
            case(3):
                m.setContentView(R.layout.show_3_joints);
                break;
            case(4):
                m.setContentView(R.layout.show_4_joints);
                break;
            case(5):
                m.setContentView(R.layout.show_5_joints);
                break;
            case(6):
                m.setContentView(R.layout.show_6_joints);
                break;
            case(7):
                m.setContentView(R.layout.activity_measuring);
                break;
        }
        currentLayout = m.getCurrentFocus();
        return currentLayout;
    }

    public void setSwitchers()
    {
        for(int i=0; i<numJoints; i++)
        {
            jointTitleSwitcherArray[i].setText(titleStrings[whichJoints[i]-1]);
            jointValSwitcherArray[i].setText("0.0");
        }
    }


    public void updateSwitchers(String[] valStrings, LimitMonitor alert)
    {

        for(int i=0; i<numJoints; i++) {
            alert.updateGUI(mMeasuringActivity.getCurrentFocus().findViewById(layoutId[i]), i);
            jointValSwitcherArray[i].setText(valStrings[whichJoints[i]-1]);
        }
    }

    public void setUpEntireScreen(Activity m)
    {
        setJointLayout(m);
        makeSwitchers(m);
        setSwitchers();
    }

}
