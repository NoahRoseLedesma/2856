package org.usfirst.ftc.exampleteam.yourcodehere;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.robotcore.hardware.*;
import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;
import com.qualcomm.ftcrobotcontroller.CustomSettingsActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * A skeletal example of a do-nothing first OpMode. Go ahead and change this code
 * to suit your needs, or create sibling OpModes adjacent to this one in the same
 * Java package.
 */
@TeleOp(name="2856 Autonomous")
public class MainAutonomous extends SynchronousOpMode {

	public IMU Robot;

	@Override
	public void main() throws InterruptedException {


		// Read from preferences file written by the CustomSettingsActivity to determine what side we are on.
		String side;
		// Retrieve file.
		File file = new File("/sdcard/Pictures","prefs");
		StringBuilder text = new StringBuilder();
		// Attempt to load line from file into the buffer.
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			// Ensure that the first line is not null.
			while ((line = br.readLine()) != null) {
				text.append(line);
			}
			// Close the buffer reader
			br.close();
		}
		// Catch exceptions... Or don't because that would require effort.
		catch (IOException e) {
		}

		// Provide in a more user friendly form.
		side = text.toString();


		DcMotor LeftMotor = hardwareMap.dcMotor.get("left_drive");
		DcMotor RightMotor = hardwareMap.dcMotor.get("right_drive");
		DcMotor BackBrace = hardwareMap.dcMotor.get("back_brace");
		DcMotor BackWheel = hardwareMap.dcMotor.get("back_wheel");
		DcMotor ClimberDeployment = this.hardwareMap.dcMotor.get("climber_control");
		Servo leftDebris = this.hardwareMap.servo.get("left_debris");
		Servo rightDebris = this.hardwareMap.servo.get("right_debris");
		Servo leftClimberServo = this.hardwareMap.servo.get("left_climber");
		Servo rightClimberServo = this.hardwareMap.servo.get("right_climber");

		LeftMotor.setDirection(DcMotor.Direction.REVERSE);

		Robot = new IMU(LeftMotor, RightMotor, hardwareMap, telemetry, this);

		//set positions of servos
		rightDebris.setPosition(1);
		leftDebris.setPosition(.1);

		leftClimberServo.setPosition(0);
		rightClimberServo.setPosition(1);


		waitForStart();

		telemetry.log.add(side + " side");

		double InitialRotation = Robot.Rotation();
		double BackBraceInitial = BackBrace.getCurrentPosition();

		Robot.Power = .5f;

		Robot.Straight(1.5f, 5);

		Robot.Power = .6f;
		Robot.Stop();

		//back brace to correct height
		while (Math.abs(BackBraceInitial - BackBrace.getCurrentPosition()) < 1440 * 4)
		{
			//need to do this whenever not using rotation libraries
			Robot.UpdateAngles();

			BackBrace.setPower(1);
		}
		BackBrace.setPower(0);


		float Offest = (float)(InitialRotation - Robot.Rotation());

		Offest = ContainValue(Offest);

		telemetry.log.add("Offset of " + Offest);

		//(40, left tread) for blue

		//!!!!!!! If no option is selected the robot will default to running on the blue alliance

		if (side.equals("blue")) {
			Robot.Turn(45 + Offest, "Left");
		} else
		{
			Robot.Turn(-45 + Offest, "Right");
		}

		Robot.Straight(7f, 15);

		Robot.Stop();

		// Back brace to correct height
		while (Math.abs(BackBraceInitial - BackBrace.getCurrentPosition()) > 200)
		{
			// Need to do this whenever not using rotation libraries
			Robot.UpdateAngles();

			BackBrace.setPower(-1);
		}

		telemetry.log.add("Backbrace Encoder Dif: " + Math.abs(BackBraceInitial - BackBrace.getCurrentPosition()));

		// Current rotation minus initial rotation
		// There was a bug where the robot would turn 360 degrees and add 360, so take the mod
		double AdditionalTurnDegrees = (((Robot.Rotation() - InitialRotation)));

		AdditionalTurnDegrees = ContainValue((float)AdditionalTurnDegrees);

		telemetry.log.add(AdditionalTurnDegrees + " additional degrees to turn.");

		BackBrace.setPower(0);

		if (side.equals("blue")) {
			float DegreeOffset = 20;
			telemetry.log.add("Turning " + (-135 - (float) (AdditionalTurnDegrees - 45) + DegreeOffset));
			Robot.Turn(-135 - (float) (AdditionalTurnDegrees - 45) + DegreeOffset, "Right");
		} else
		{
			Robot.Turn(135 - (float) (AdditionalTurnDegrees + 45), "Left");
		}

		if (side == "blue") {
			Robot.Straight(-1.9f, 4);
		}
		else
		{
			Robot.Straight(-1.6f, 4);
		}

		telemetry.log.add("done backing up");

		//deploy climbers
		ClimberDeployment.setPower(-.15);
		Thread.sleep(2000);
		ClimberDeployment.setPower(0);
		Thread.sleep(500);
		ClimberDeployment.setPower(.15);
		Thread.sleep(1500);
		ClimberDeployment.setPower(0);

		telemetry.log.add("done scoring climbers");

		Robot.Stop();
	}

	private float ContainValue(float Value)
	{
		if(Value < -200)
		{
			Value += 360;
		}
		else if(Value > 200)
		{
			Value -= 360;
		}

		return Value;
	}

	public void RunIdle() throws InterruptedException
	{
		idle();
	}
}
