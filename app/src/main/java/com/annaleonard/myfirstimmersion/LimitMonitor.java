package com.annaleonard.myfirstimmersion;

/**
 * Created by jelong3 on 8/3/15.
 */
public class LimitMonitor {

    private final int JOINT_LIMIT = 0;
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
        for(int i=0; i<7; i++)
        {
            limitHit[i] = false;
        }



    }


    public boolean [] getLimitHit ()
    {
        return limitHit;
    }

    public boolean[] isAnyLimitHit ()
    {
        for(int i=0; i<7; i++)
        {
            if (jointPositions[i] > JOINT_LIMIT)
            {
                limitHit[i] = true;
            }
        }
        return limitHit;
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
