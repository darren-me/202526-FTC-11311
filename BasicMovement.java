package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="BasicMovement", group="Testing")
public class BasicMovement extends LinearOpMode {

    // Declare motors
    private DcMotor frontLeft  = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft   = null;
    private DcMotor backRight  = null;

    @Override
    public void runOpMode() {
        // Initialize hardware
        frontLeft  = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backLeft   = hardwareMap.get(DcMotor.class, "bl");
        backRight  = hardwareMap.get(DcMotor.class, "br");

        // Set motor directions
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Wait for start
        waitForStart();

        while (opModeIsActive()) {
            // Get joystick values
            double drive = -gamepad1.left_stick_y;    // Forward/backward movement
            double strafe = gamepad1.left_stick_x;    // Left/right strafing
            double turn = gamepad1.right_stick_x;     // Rotation/turning

            // Calculate motor powers
            double frontLeftPower  = drive + strafe + turn;
            double frontRightPower = drive - strafe - turn;
            double backLeftPower   = drive - strafe + turn;
            double backRightPower  = drive + strafe - turn;

            double maxPower = Math.max(Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)));

            if (maxPower > 1.0) {
                frontLeftPower  /= maxPower;
                frontRightPower /= maxPower;
                backLeftPower   /= maxPower;
                backRightPower  /= maxPower;
            }

            // Set motor powers
            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

            // Show telemetry
            telemetry.addData("Drive", drive);
            telemetry.addData("Strafe", strafe);
            telemetry.addData("Turn", turn);
            telemetry.addData("FL Power", frontLeftPower);
            telemetry.addData("FR Power", frontRightPower);
            telemetry.addData("BL Power", backLeftPower);
            telemetry.addData("BR Power", backRightPower);
            telemetry.update();
        }
    }
}