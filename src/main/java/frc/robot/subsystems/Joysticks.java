// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Joysticks extends SubsystemBase {
  // private final XboxController driveXboxController = null;//new XboxController(0);
  // public final int robotOrientedToggle = XboxController.Button.kA.value;

  //main drive joystick
  private final Joystick driveJoystickMain = new Joystick(0);
  public double getDriveForward() {return driveJoystickMain.getY();}
  public double getDriveSideways() {return driveJoystickMain.getX();}

  public boolean getRobotOrientedToggle() {return driveJoystickMain.getRawButton(1);}
  public boolean getGyroResetButton() {return driveJoystickMain.getRawButton(8);}
  public boolean getEncoderResetButton() {return driveJoystickMain.getRawButton(9);}


  //side drive joysticks
  private final Joystick driveJoystickSide = new Joystick(1);
  public double getDriveRotation() {return driveJoystickSide.getX();}


  //operator controls
  private final XboxController operatorController = new XboxController(2);
  public double getClimbAxis() {return operatorController.getLeftY();}

  public boolean getIndexToggle() {return operatorController.getAButton();}
  public boolean getIntakeToggle() {return operatorController.getRightBumper();}
  public boolean getOutakeToggle() {return operatorController.getLeftBumper();}
  public boolean getIncreaseShooter() {return operatorController.getPOV() == 0;}
  public boolean getDecreaseShooter() {return operatorController.getPOV() == 180;}
}
