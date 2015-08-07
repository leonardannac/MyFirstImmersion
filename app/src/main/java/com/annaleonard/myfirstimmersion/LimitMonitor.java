package com.annaleonard.myfirstimmersion;

import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.view.View;

import com.google.android.glass.media.Sounds;

/**
 * Created by jelong3 on 8/3/15.
 */
public class LimitMonitor {

    private final int JOINT_LIMIT = 0;
    private int count = 0;
    private boolean [] limitHit = new boolean[7];
    private double [] jointPositions;

    private LimitMonitor()
    {
     for(int i=0; i<7; i++)
        {
            jointPositions[i] = 400;
            limitHit[i] = false;
        }
    }


    public LimitMonitor(double [] mjointPositions)
    {
        jointPositions = mjointPositions;
        isAnyLimitHit();
    }


    public boolean [] getLimitHit ()
    {
        return limitHit;
    }

    private void isAnyLimitHit ()
    {
        for(int i=0; i<7; i++)
        {
            if (jointPositions[i] > JOINT_LIMIT)
            {
                limitHit[i] = true;
            }
        }
    }

    public void updateGUI(View v, int whichJoint)
    {
        if(!limitHit[whichJoint])
        {
            v.setBackgroundColor(Color.RED);
            AudioManager am = (AudioManager) v.getContext().getSystemService(Context.AUDIO_SERVICE);
            am.playSoundEffect(Sounds.ERROR);
        }

        else
        {
            v.setBackgroundColor(Color.BLACK);
        }
    }

    public boolean isOneLimitHit (int whichJoint)
    {
        if(jointPositions[whichJoint-1] > JOINT_LIMIT)
        {
            return true;
        }
        return false;
    }

}
