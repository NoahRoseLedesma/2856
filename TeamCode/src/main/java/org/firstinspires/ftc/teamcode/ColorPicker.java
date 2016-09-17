package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robot.Robot;

import org.swerverobotics.library.SynchronousOpMode;
import org.swerverobotics.library.interfaces.TeleOp;

@TeleOp(name="ColorPicker")
public class ColorPicker extends SynchronousOpMode {

    @Override
    public void main() throws InterruptedException {

        waitForStart();

		Trigger.takeImage();
		Thread.sleep(1000);
		String side = Trigger.determineSides();
		Thread.sleep(500);
		if(side == "left") {
			Log.d("left", "Blue is on the left");
		} else if (side == "right") {
			Log.d("right", "Blue is on the right");
		} else {
			Log.d("FindAVGSides", "Inconclusive Result");
		}

    }

	public void RunIdle() throws InterruptedException
	{
		idle();
	}
}
