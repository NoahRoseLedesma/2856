package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.*;
import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;

import java.lang.reflect.Array;

/**
 * An example of a synchronous opmode that implements a simple drive-a-bot. 
 */
@TeleOp(name="New 2856 TeleOp")
public class NewTeleop extends SynchronousOpMode
{
	//motor / servo declarations
	DcMotor leftDrive = null;
	DcMotor rightDrive = null;
	DcMotor backBrace = null;
	DcMotor backWheel = null;
	DcMotor blockCollector = null;
	DcMotor hangingArm = null;
	Servo hangingControl = null;
	Servo blockConveyer = null;
	Servo leftGate = null;
	Servo rightGate = null;
	Servo leftWing = null;
	Servo rightWing = null;
	Servo hangLock = null;
	Servo allClear = null;
	Servo otherAllClear = null;
	Servo climberDeploy = null;
	int backBraceShouldBe;

	//open, closed
	double LeftGatePositions[] = new double[]{1, .27};
	double RightGatePositions[] = new double[]{.36, 1};

	// down, up
	double HangingServoPositions[] = new double[]{.10, .55};

	//this is the default servo position
	double HangingServoPosition = HangingServoPositions[1];

	boolean CollectBlocks = false;

	boolean PreviousFrameButtonPressed = false;

	@Override
	protected void main() throws InterruptedException
	{
		//initialize motors
		this.leftDrive = this.hardwareMap.dcMotor.get("left_drive");
		this.rightDrive = this.hardwareMap.dcMotor.get("right_drive");
		this.backBrace = this.hardwareMap.dcMotor.get("back_brace");
		this.backWheel = this.hardwareMap.dcMotor.get("back_wheel");
		this.blockCollector = this.hardwareMap.dcMotor.get("block_collector");
		this.hangingArm = this.hardwareMap.dcMotor.get("hanging_motor");
		this.hangingControl = this.hardwareMap.servo.get("hang_adjust");
		this.blockConveyer = this.hardwareMap.servo.get("block_conveyor");
		this.rightGate = this.hardwareMap.servo.get("right_ramp");
		this.leftGate = this.hardwareMap.servo.get("left_ramp");
		this.leftWing = this.hardwareMap.servo.get("left_wing");
		this.rightWing = this.hardwareMap.servo.get("right_wing");
		this.hangLock = this.hardwareMap.servo.get("hang_stop");
		this.allClear = this.hardwareMap.servo.get("all_clear");
		this.climberDeploy = this.hardwareMap.servo.get("climber_deploy");
		this.otherAllClear = this.hardwareMap.servo.get("other_all_clear");

		this.backWheel.setDirection(DcMotor.Direction.REVERSE);
		this.leftDrive.setDirection(DcMotor.Direction.REVERSE);

		// Wait until we've been given the ok to go
		this.waitForStart();

		this.leftGate.setPosition(LeftGatePositions[1]); //close
		this.rightGate.setPosition(RightGatePositions[1]); //close
		this.blockConveyer.setPosition(.50);
		this.leftWing.setPosition(.2);
		this.rightWing.setPosition(.6);
		this.hangLock.setPosition(.63);
		this.otherAllClear.setPosition(.19);

		this.backBraceShouldBe = backBrace.getCurrentPosition();



		// Enter a loop processing all the input we receive
		while (this.opModeIsActive())
		{
			updateGamepads();

			this.DriveControl(this.gamepad1);
			this.BackBraceControl(this.gamepad1);
			this.Hanging(this.gamepad2);
			this.Blocks(this.gamepad1);
			this.BlockDeploy(this.gamepad2);
			this.Zipliners(this.gamepad2);
			this.allClear(this.gamepad1);

			// Emit telemetry with the freshest possible values
			this.telemetry.update();

			// Let the rest of the system run until there's a stimulus from the robot controller runtime.
			this.idle();
		}
	}

	void Blocks(Gamepad pad)
	{
		// if y wasn't pressed before but is now
		if(!PreviousFrameButtonPressed && pad.y)
		{
			CollectBlocks = !CollectBlocks;
		}

		PreviousFrameButtonPressed = pad.y;

		if(pad.x)
		{
			blockCollector.setPower(1);
			CollectBlocks = false;
		}
		else if(CollectBlocks) {
			this.blockCollector.setPower(-1);
		}
		else {
			blockCollector.setPower(0);
		}
	}

	void BackBraceControl(Gamepad pad)
	{

		float ChangeValue = 0;

		if (Math.abs(pad.right_trigger) > .1)
		{
			backBraceShouldBe = backBrace.getCurrentPosition();
			backBrace.setPower(pad.right_trigger);
			telemetry.addData("00", "right trigger pressed");
		}
		else if (Math.abs(pad.left_trigger) > .1)
		{
			backBraceShouldBe = backBrace.getCurrentPosition();
			backBrace.setPower(-pad.left_trigger);
			telemetry.addData("00", "left trigger pressed");
		}
		// if there is a  big error
		else if(Math.abs(backBrace.getCurrentPosition() - backBraceShouldBe) > 100) {
			ChangeValue = Math.abs(backBrace.getCurrentPosition() - backBraceShouldBe) / 1000f;

			if(ChangeValue > 1)
			{
				ChangeValue = 1;
			}

			if (backBrace.getCurrentPosition() > backBraceShouldBe) {
				backBrace.setPower(-1 * ChangeValue);
			}
			if (backBrace.getCurrentPosition() < backBraceShouldBe) {
				backBrace.setPower(ChangeValue);
			}
		} else
		{
			backBrace.setPower(0);
		}



		telemetry.addData("02", "encoder " + String.valueOf(backBrace.getCurrentPosition()) + " current" + String.valueOf(backBraceShouldBe) + " change value" + String.valueOf(ChangeValue));
	}

	void DriveControl(Gamepad pad) throws InterruptedException {
		// Remember that the gamepad sticks range from -1 to +1, and that the motor
		// power levels range over the same amount
		float leftPower = pad.left_stick_y;
		float rightPower = pad.right_stick_y;

		float backWheelPower = (leftPower + rightPower) / 2f;

		// drive the motors
		this.leftDrive.setPower(leftPower);
		this.rightDrive.setPower(rightPower);
		this.backWheel.setPower(backWheelPower);
	}

	void Hanging(Gamepad pad)
	{
		//engage hang lock servo
		if (pad.dpad_up) {
			this.hangLock.setPosition(.87f);
		}

		//moves the arm up and down
		if (Math.abs(pad.left_stick_y) > .1)
		{
			this.hangingArm.setPower(pad.left_stick_y);
		}
		else
		{
			this.hangingArm.setPower(0);
		}

		//moves the servo that angles the tape measure
		if (Math.abs(pad.right_stick_y) > .1)
		{
			HangingServoPosition  += pad.right_stick_y / 150;
		}

		if(HangingServoPosition <= HangingServoPositions[0]) {
			HangingServoPosition = HangingServoPositions[0];
		} else if (HangingServoPosition > HangingServoPositions[1]) {
			HangingServoPosition = HangingServoPositions[1];
		}

		if(this.gamepad1.b)
		{
			HangingServoPosition = HangingServoPositions[0];
		}

		hangingControl.setPosition(HangingServoPosition);

		if(pad.back) {
			climberDeploy.setPosition(1);
		} else if (pad.start) {
			climberDeploy.setPosition(0);
		} else {
			climberDeploy.setPosition(.5);
		}
	}

	void allClear(Gamepad pad) {
		if(pad.a) {
			this.allClear.setPosition(0); //engaged
			this.otherAllClear.setPosition(1);
		} else {
			this.allClear.setPosition(1); //disengaged
			this.otherAllClear.setPosition(0);
		}
	}

	void BlockDeploy(Gamepad pad)
	{
		//generate random numbers for bumpers
		double LeftRandom;
		if (pad.left_bumper) {
			LeftRandom = (Math.random() - .5) / 5;
		}
		else
		{
			LeftRandom = 0;
		}
		double RightRandom;
		if (pad.right_bumper) {
			RightRandom = (Math.random() - .5) / 5;
		}
		else
		{
			RightRandom = 0;
		}


		if(pad.left_trigger > 0.1)
		{
			blockConveyer.setPosition(0);
			telemetry.addData("01", "deploying blocks left");
		}
		else if (pad.right_trigger > 0.1)
		{
			blockConveyer.setPosition(1);
			telemetry.addData("01", "deploying blocks right");
		}
		else {
			blockConveyer.setPosition(0.55);
			telemetry.addData("01", "not deploying blocks");
		}

		if(pad.left_bumper)
		{
			this.leftGate.setPosition(LeftGatePositions[0] + LeftRandom); //open
			this.rightGate.setPosition(RightGatePositions[1]); //close
		}

		if(pad.right_bumper)
		{
			this.leftGate.setPosition(LeftGatePositions[1]); //close
			this.rightGate.setPosition(RightGatePositions[0] + RightRandom); //open
		}

		// close both gates for collection
		if(pad.y)
		{
			this.leftGate.setPosition(LeftGatePositions[1]); //close
			this.rightGate.setPosition(RightGatePositions[1]); //close
		}
	}

	void Zipliners(Gamepad pad)
	{
		if(pad.x)
		{
			this.leftWing.setPosition(.9);
		}
		else if(pad.b)
		{
			this.rightWing.setPosition(.05);
		}
		else if(!pad.x && !pad.b)
		{
			this.leftWing.setPosition(.3);
			this.rightWing.setPosition(.6);
		}
	}
}