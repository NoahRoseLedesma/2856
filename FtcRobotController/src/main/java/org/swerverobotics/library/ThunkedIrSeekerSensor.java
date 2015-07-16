package org.swerverobotics.library;

import com.qualcomm.robotcore.hardware.*;

/**
 * An IrSeekerSensor that can be called on the main() thread.
 */
public class ThunkedIrSeekerSensor extends IrSeekerSensor
    {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    IrSeekerSensor target;   // can only talk to him on the loop thread

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    private ThunkedIrSeekerSensor(IrSeekerSensor target)
        {
        this.target = target;
        }

    static public ThunkedIrSeekerSensor Create(IrSeekerSensor target)
        {
        return target instanceof ThunkedIrSeekerSensor ? (ThunkedIrSeekerSensor)target : new ThunkedIrSeekerSensor(target);
        }

    //----------------------------------------------------------------------------------------------
    // IrSeekerSensor
    //----------------------------------------------------------------------------------------------

    @Override public void setMode(final IrSeekerSensor.Mode mode)
        {
        class Thunk extends NonwaitingThunk
            {
            @Override public void actionOnLoopThread()
                {
                target.setMode(mode);
                }
            }
        Thunk thunk = new Thunk();
        thunk.dispatch();
        }

    @Override public IrSeekerSensor.Mode getMode()
        {
        class Thunk extends ResultableThunk<Mode>
            {
            @Override public void actionOnLoopThread()
                {
                this.result = target.getMode();
                }
            }
        Thunk thunk = new Thunk();
        thunk.dispatch();
        return thunk.result;
        }

    @Override public boolean signalDetected()
        {
        class Thunk extends ResultableThunk<Boolean>
            {
            @Override public void actionOnLoopThread()
                {
                this.result = target.signalDetected();
                }
            }
        Thunk thunk = new Thunk();
        thunk.dispatch();
        return thunk.result;
        }

    @Override public double getAngle()
        {
        class Thunk extends ResultableThunk<Double>
            {
            @Override public void actionOnLoopThread()
                {
                this.result = target.getAngle();
                }
            }
        Thunk thunk = new Thunk();
        thunk.dispatch();
        return thunk.result;
        }

    @Override public double getStrength()
        {
        class Thunk extends ResultableThunk<Double>
            {
            @Override public void actionOnLoopThread()
                {
                this.result = target.getStrength();
                }
            }
        Thunk thunk = new Thunk();
        thunk.dispatch();
        return thunk.result;
        }

    @Override public IrSeekerSensor.IrSensor[] getSensors()
        {
        class Thunk extends ResultableThunk<IrSensor[]>
            {
            @Override public void actionOnLoopThread()
                {
                this.result = target.getSensors();
                }
            }
        Thunk thunk = new Thunk();
        thunk.dispatch();
        return thunk.result;
        }

    }
