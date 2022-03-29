// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.autoPath;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.LimelightTurn;
import frc.robot.commands.MoveBy;
import frc.robot.commands.TurnBy;
import frc.robot.commands.TurnTo;
import frc.robot.commands.TimerCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Tube;
import frc.robot.subsystems.Shooter.ShooterMode;
import frc.robot.subsystems.Tube.TubeMode;

public class FourBallComplex extends CommandBase {
  boolean isFinished = false;
  int step = 0;

  DriveSubsystem drive;
  Tube tube;
  Shooter shooter;
  Limelight limelight;

  Command currentCommand;

  double initalAngle;

  public FourBallComplex(DriveSubsystem drive, Tube tube, Shooter shooter, Limelight limelight) {
    this.drive = drive;
    this.tube = tube;
    this.shooter = shooter;
    this.limelight = limelight;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    tube.setUseJoysticks(false);
    //extend pneumatics
    tube.setPneumatics(true);
    //set intake mode
    tube.setTubeMode(TubeMode.intake);
    //set shooter mode
    shooter.setShooterMode(ShooterMode.auto);

    step = 0;
    currentCommand = null;
    initalAngle = drive.getAngle();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (!currentCommand.isScheduled()) {
      //move forward 5 ft
      if (step == 0) currentCommand = new MoveBy(drive, 5.25);
      //rotate towards hub
      else if (step == 1) currentCommand = new LimelightTurn(drive, 160, limelight);
      //feedShooter for 3 seconds
      else if (step == 2) {
        currentCommand = new TimerCommand(3);
        tube.setTubeMode(TubeMode.feed);
      }
      //turns robot back to inital angle && stop intaking
      else if (step == 3) {
        tube.setTubeMode(TubeMode.off);
        currentCommand = new TurnTo(drive, initalAngle);
      }
      //intake while driving forward 10 ft
      else if (step == 4) {
        tube.setTubeMode(TubeMode.intake);
        currentCommand = new MoveBy(drive, 12);
      }
      //pause for 3 seconds to let human player feed ball
      else if (step == 5) currentCommand = new TimerCommand(3);
      //move backwards 12ft
      else if (step == 6) {
        tube.setTubeMode(TubeMode.off);
        currentCommand = new MoveBy(drive, -12);
      }
      //turn to face the hub
      else if (step == 7) currentCommand = new TurnBy(drive, 180);
      //feedShooter for 3 seconds
      else if (step == 8) {
        currentCommand = new TimerCommand(3);
        tube.setTubeMode(TubeMode.feed);
      }
      //end execute
      else if (step == 9) isFinished = true;

      currentCommand.schedule();
      step++;
    }

    //run shooter and tube
    shooter.runShooter();
    tube.runTube();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drive.stop();
    tube.stopTube();
    shooter.stopShooter();

    tube.setUseJoysticks(true);
    if (currentCommand != null) currentCommand.cancel();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return isFinished;
  }
}