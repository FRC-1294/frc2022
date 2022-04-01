// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.Tube;
import frc.robot.subsystems.Joysticks;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.Shooter;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  public final Joysticks joysticks = new Joysticks();

  public final Limelight limelight = new Limelight();
  public final DriveSubsystem drive = new DriveSubsystem(joysticks, limelight);
  public final Tube tube = new Tube(joysticks);
  public final Shooter shooter = new Shooter(joysticks);
   public final ClimbSubsystem climb = new ClimbSubsystem(joysticks);

  // public final limelightMove auto = new limelightMove(drive, limelight, Pipeline.blue, Constants.thor * Constants.tvert);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {}
}
