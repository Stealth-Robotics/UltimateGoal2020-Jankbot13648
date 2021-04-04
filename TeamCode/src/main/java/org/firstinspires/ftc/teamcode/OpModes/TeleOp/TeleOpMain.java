package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.DriveBase.drive.PoseStorage;
import org.firstinspires.ftc.teamcode.Intake;
import org.firstinspires.ftc.teamcode.Shooter;
import org.firstinspires.ftc.teamcode.WobbleGoal;
import org.firstinspires.ftc.teamcode.DriveBase.drive.DriveBase;

@TeleOp(name = "TeleOp - Main")
public class TeleOpMain extends LinearOpMode {

    DriveBase driveBase;
    Shooter shooter;
    Intake intake;
    WobbleGoal wobbleGoal;

    @Override
    public void runOpMode() throws InterruptedException {

        driveBase = new DriveBase(hardwareMap);
        shooter = new Shooter(hardwareMap);
        intake = new Intake(hardwareMap);
        wobbleGoal = new WobbleGoal(hardwareMap);

        Trajectory traj1 = driveBase.trajectoryBuilder(new Pose2d())
                .strafeLeft(6.5)
                .build();

        Trajectory traj2 = driveBase.trajectoryBuilder(traj1.end())
                .strafeLeft(7.5)
                .build();

        Trajectory traj3 = driveBase.trajectoryBuilder(traj2.end())
                .strafeLeft(7.5)
                .build();


        telemetry.addLine("System Initialization Complete");
        telemetry.update();

        waitForStart();

        telemetry.clearAll();
        telemetry.update();

        shooter.SetShooter(Constants.SHOOTER_VELOCITY);

        if(isStopRequested()) return;

        shooter.Unkick();

        while (!isStopRequested() && opModeIsActive())
        {
            Pose2d poseEstimate = driveBase.getPoseEstimate();

            if(!gamepad1.right_bumper)
            {
                Vector2d input = new Vector2d(
                        -gamepad1.left_stick_y,
                        -gamepad1.left_stick_x
                ).rotated(-poseEstimate.getHeading());

                driveBase.setWeightedDrivePower(
                        new Pose2d(
                                input.getX(),
                                input.getY(),
                                -gamepad1.right_stick_x
                        )
                );
            }

            else {
                Vector2d input = new Vector2d(
                        -gamepad1.left_stick_y/2,
                        -gamepad1.left_stick_x/2
                ).rotated(-poseEstimate.getHeading());

                driveBase.setWeightedDrivePower(
                        new Pose2d(
                                input.getX(),
                                input.getY(),
                                -gamepad1.right_stick_x/2
                        )
                );
            }

            driveBase.update();

            if(gamepad1.a)
            {
                poseEstimate = driveBase.getPoseEstimate();

                telemetry.addData("Current X", poseEstimate.getX());
                telemetry.addData("Current Y", poseEstimate.getY());
                telemetry.update();

                //double angleToGoal = Math.atan2(Constants.GOAL_VECTOR2D.getX() - poseEstimate.getX(), Constants.GOAL_VECTOR2D.getY() - poseEstimate.getY());

                //driveBase.turn(angleToGoal);

                for(int i = 0; i <3; i++)
                {
                    shooter.Kick();

                    sleep(200);

                    shooter.Unkick();

                    sleep(200);
                }
            }


            if(gamepad1.left_bumper)
            {
                shooter.SetShooter(Constants.POWER_SHOT_VELOCITY);

                driveBase.followTrajectory(traj1);

                // Kick 1
                shooter.Kick();

                sleep(200);

                shooter.Unkick();

                sleep(200);

                // Move 1

                driveBase.followTrajectory(traj2);

                // Kick 2
                shooter.Kick();

                sleep(200);

                shooter.Unkick();

                sleep(200);

                // Move 2

                driveBase.followTrajectory(traj3);

                shooter.Kick();

                sleep(200);

                shooter.Unkick();

                sleep(200);

            }

            intake.SetIntake(gamepad1.right_trigger, gamepad1.left_trigger);
            wobbleGoal.MoveWobbleGoalPosition(gamepad2.dpad_left, gamepad2.dpad_up, gamepad2.dpad_right);
            wobbleGoal.WobbleGoalManipulatorHandler(gamepad2.x, gamepad2.b);
        }
    }
}