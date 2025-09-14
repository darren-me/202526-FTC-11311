package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;
import java.util.Locale;

@TeleOp(name="BasicMovement", group="Testing")
public class BasicMovement extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    GoBildaPinpointDriver odo;
    double oldTime = 0;

    @Override
    public void runOpMode() {
        // Motor mapping
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");

        // Set motor directions
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Only run odometry if available
        try {
            odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
            odo.setOffsets(-84.0, -168.0, DistanceUnit.MM);
            odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
            odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
            odo.resetPosAndIMU();

            telemetry.addData("Status", "Initialized");
            telemetry.addData("X offset", odo.getXOffset(DistanceUnit.MM));
            telemetry.addData("Y offset", odo.getYOffset(DistanceUnit.MM));
            telemetry.addData("Device Version", odo.getDeviceVersion());
            telemetry.addData("Heading Scalar", odo.getYawScalar());
        } catch (Exception e) {
            telemetry.addData("Odometry", "Not Found");
        }

        telemetry.update();
        waitForStart();
        resetRuntime();

        // This was Darren trust
        // telemetry.speak("I'm going to tickle your toes hehehehhehehehehhehe muahahhahhaha hehhehehhehhehehheheh hahahhahahhahahah hehehhehehhehe hahahhahhahaha hehehheehhehehehehehhehehee hahahhahahhahhaa huhuhuhuhuhuuu heehehhehehhehhee muahhahahhahahhaha A journey of a thousand miles begins with a single step yes yes"You may delay, but time will not uehuehuheuheuheuehheuhuaha");

        while (opModeIsActive()) {
            if (odo != null) {
                odo.update();

                if (gamepad1.a) {
                    odo.resetPosAndIMU();
                }

                if (gamepad1.b) {
                    odo.recalibrateIMU();
                }
            }

            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            double frontLeftPower = drive + strafe + turn;
            double frontRightPower = drive - strafe - turn;
            double backLeftPower = drive - strafe + turn;
            double backRightPower = drive + strafe - turn;

            double maxPower = Math.max(Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)));

            if (maxPower > 1.0) {
                frontLeftPower /= maxPower;
                frontRightPower /= maxPower;
                backLeftPower /= maxPower;
                backRightPower /= maxPower;
            }

            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

            double newTime = getRuntime();
            double loopTime = newTime - oldTime;
            double frequency = 1 / loopTime;
            oldTime = newTime;

            telemetry.addData("Drive", "%.2f", drive);
            telemetry.addData("Strafe", "%.2f", strafe);
            telemetry.addData("Turn", "%.2f", turn);
            telemetry.addData("FL Power", "%.2f", frontLeftPower);
            telemetry.addData("FR Power", "%.2f", frontRightPower);
            telemetry.addData("BL Power", "%.2f", backLeftPower);
            telemetry.addData("BR Power", "%.2f", backRightPower);

            if (odo != null) {
                Pose2D pos = odo.getPosition();
                String data = String.format(Locale.US, "{X: %.1f, Y: %.1f, H: %.1f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
                telemetry.addData("Position", data);

                String velocity = String.format(Locale.US,"{XVel: %.1f, YVel: %.1f, HVel: %.1f}", odo.getVelX(DistanceUnit.MM), odo.getVelY(DistanceUnit.MM), odo.getHeadingVelocity(UnnormalizedAngleUnit.DEGREES));
                telemetry.addData("Velocity", velocity);

                telemetry.addData("Status", odo.getDeviceStatus());
                telemetry.addData("Pinpoint Frequency", odo.getFrequency());
            }

            telemetry.addData("Loop Frequency", "%.1f Hz", frequency);
            telemetry.update();
        }
    }
}
