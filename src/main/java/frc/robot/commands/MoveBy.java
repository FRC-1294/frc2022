// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.constants.Constants;
import frc.robot.subsystems.DriveSubsystem;
public class MoveBy extends CommandBase {
  DriveSubsystem drive;
  double initialPos;
  double move = 0;

  PIDController movePID = new PIDController(Constants.movePID.kP, Constants.movePID.kI, Constants.movePID.kD);

  public MoveBy(DriveSubsystem drive, double move) {
    this.drive = drive;
    this.move = move;
  }

  public void setMove(double move) {
    this.move = move;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    initialPos = drive.getAveragePos();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double speed = movePID.calculate(drive.getAveragePos(), initialPos+move);
    drive.drive(0, speed, 0, false);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drive.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (Math.abs(drive.getAveragePos()-initialPos) < Constants.encoderDeadzone) {
      return true;
    }

    return false;
  }
}
