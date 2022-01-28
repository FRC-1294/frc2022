// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.motorcontrol.Talon;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants { //Need to use the correct motor ports/ channels
    public static Joystick joystick = new Joystick(0);
    public static Talon intakeMotor1 = new Talon(0);
    public static Talon intakeMoror2 = new Talon(1);
    public static ColorSensorV3 intakeColorSensor = new ColorSensorV3(null);
    public static final int kShooterPort = 0;
    public static final double increment = 0.2;
}
