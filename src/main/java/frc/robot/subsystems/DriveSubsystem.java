// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.SPI;
import frc.robot.Constants;
import frc.robot.Gains;

public class DriveSubsystem extends SubsystemBase {
  CANSparkMax m_frontLeftSpark = new CANSparkMax(Constants.frontLeftSparkID, MotorType.kBrushless);
  CANSparkMax m_frontRightSpark = new CANSparkMax(Constants.frontRightSparkID, MotorType.kBrushless);
  CANSparkMax m_backLeftSpark = new CANSparkMax(Constants.backLeftSparkID, MotorType.kBrushless);
  CANSparkMax m_backRightSpark = new CANSparkMax(Constants.backRightSparkID, MotorType.kBrushless);

  // Locations of the wheels relative to the robot center.
  Translation2d m_frontLeftLocation = new Translation2d(Constants.wheelDisFromCenter, Constants.wheelDisFromCenter);
  Translation2d m_frontRightLocation = new Translation2d(Constants.wheelDisFromCenter, -Constants.wheelDisFromCenter);
  Translation2d m_backLeftLocation = new Translation2d(-Constants.wheelDisFromCenter, Constants.wheelDisFromCenter);
  Translation2d m_backRightLocation = new Translation2d(-Constants.wheelDisFromCenter, -Constants.wheelDisFromCenter);

  // Creating my kinematics object using the wheel locations.
  MecanumDriveKinematics m_kinematics = new MecanumDriveKinematics(m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation);

  AHRS gyro;
  Double gyroHold = null;

  XboxController driveJoystick = new XboxController(0);
  Joystick driveJoystickMain = new Joystick(0);
  Joystick driveJoystickSide = new Joystick(1);
  boolean usingXboxController = true;

  public DriveSubsystem() {
    gyro = new AHRS(SPI.Port.kMXP);

    m_frontLeftSpark.restoreFactoryDefaults(true);
    m_frontRightSpark.restoreFactoryDefaults(true);
    m_backLeftSpark.restoreFactoryDefaults(true);
    m_backRightSpark.restoreFactoryDefaults(true);

    m_frontLeftSpark.getEncoder();
    m_frontRightSpark.getEncoder();
    m_backLeftSpark.getEncoder();
    m_backRightSpark.getEncoder();

    m_frontLeftSpark.setSmartCurrentLimit(60);
    m_frontRightSpark.setSmartCurrentLimit(60);
    m_backLeftSpark.setSmartCurrentLimit(60);
    m_backRightSpark.setSmartCurrentLimit(60);

    setRampRates(0.5);
    setMode(idleMode.coast);

    setPidControllers(m_frontLeftSpark.getPIDController(), Constants.defaultPID, Constants.defaultPID.kSlot);
    setPidControllers(m_frontRightSpark.getPIDController(), Constants.defaultPID, Constants.defaultPID.kSlot);
    setPidControllers(m_backLeftSpark.getPIDController(), Constants.defaultPID, Constants.defaultPID.kSlot);
    setPidControllers(m_backRightSpark.getPIDController(), Constants.defaultPID, Constants.defaultPID.kSlot);

    m_frontLeftSpark.setInverted(false);
    m_frontRightSpark.setInverted(false);
    m_backLeftSpark.setInverted(false);
    m_backRightSpark.setInverted(false);

    m_frontLeftSpark.getPIDController().setP(0);
    m_frontRightSpark.getPIDController().setP(0);
    m_backLeftSpark.getPIDController().setP(0);
    m_backRightSpark.getPIDController().setP(0);
  }

  @Override
  public void periodic() {
    if (gyro == null) {
      gyro = new AHRS(SPI.Port.kMXP);
      if (!gyro.isConnected()) gyro = null;
    }

    drive();
  }

  public void drive() {
    //controller inputs
    double xSpeed = 0;
    double ySpeed = 0;
    double rotation = 0;
    if (usingXboxController) {
      xSpeed = driveJoystick.getLeftX();
      ySpeed = driveJoystick.getLeftY();
      rotation = driveJoystick.getRightX();
    }
    else {
      xSpeed = driveJoystickMain.getX();
      ySpeed = driveJoystickMain.getY();
      rotation = driveJoystickSide.getX();
    }

    //deadzone
    if (Math.abs(xSpeed) < Constants.joystickDeadzone) {
      xSpeed = 0;
    }
    if (Math.abs(ySpeed) < Constants.joystickDeadzone) {
      ySpeed = 0;
    }
    if (Math.abs(rotation) < Constants.joystickDeadzone) {
      rotation = 0;
    }

    //normalize
    if (xSpeed != 0) {
      xSpeed = Constants.maxSpeed / xSpeed;
    }
    if (ySpeed != 0) {
      ySpeed = Constants.maxSpeed / ySpeed;
    }
    if (rotation != 0) {
      rotation = Constants.maxTurnOutput / rotation;
      if (gyroHold != null) gyroHold = null;
    }
    //GYRO HOLD
    // else {
    //   if (gyroHold == null) {
    //     gyroHold = gyro.getAngle();
    //   }
    //   else {
    //     rotation = Constants.pGyro * (gyro.getAngle()-gyroHold);
    //     if (rotation > 1) rotation = 1;
    //     else if (rotation < -1) rotation = -1;
    //     else if (Math.abs(rotation) < Constants.minTurnInput) rotation = 0;
    //   }
    // }

    // Convert to wheel speeds
    ChassisSpeeds speeds = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rotation, Rotation2d.fromDegrees(gyro.getAngle()));
    MecanumDriveWheelSpeeds wheelSpeeds = m_kinematics.toWheelSpeeds(speeds);
    wheelSpeeds.desaturate(Constants.maxSpeed);

    // Get the individual wheel speeds
    double frontLeft = wheelSpeeds.frontLeftMetersPerSecond;
    double frontRight = wheelSpeeds.frontRightMetersPerSecond;
    double backLeft = wheelSpeeds.rearLeftMetersPerSecond;
    double backRight = wheelSpeeds.rearRightMetersPerSecond;

    m_frontLeftSpark.set(frontLeft/Constants.maxSpeed);
    m_frontRightSpark.set(frontRight/Constants.maxSpeed);
    m_backLeftSpark.set(backLeft/Constants.maxSpeed);
    m_backRightSpark.set(backRight/Constants.maxSpeed);
    
    // m_frontLeftSpark.getPIDController().setReference(frontLeft, ControlType.kSmartVelocity);
    // m_frontRightSpark.getPIDController().setReference(frontRight, ControlType.kSmartVelocity);
    // m_backLeftSpark.getPIDController().setReference(backLeft, ControlType.kSmartVelocity);
    // m_backRightSpark.getPIDController().setReference(backRight, ControlType.kSmartVelocity);

    SmartDashboard.putNumber("frontLeft", m_frontLeftSpark.getEncoder().getPosition());
    SmartDashboard.putNumber("frontRightt", m_frontRightSpark.getEncoder().getPosition());
    SmartDashboard.putNumber("rearLeft", m_backLeftSpark.getEncoder().getPosition());
    SmartDashboard.putNumber("rearRight", m_backRightSpark.getEncoder().getPosition());
  }

  public void setRampRates(double time) {
    m_frontLeftSpark.setOpenLoopRampRate(time);
    m_frontLeftSpark.setClosedLoopRampRate(time);

    m_frontRightSpark.setOpenLoopRampRate(time);
    m_frontRightSpark.setClosedLoopRampRate(time);

    m_backLeftSpark.setOpenLoopRampRate(time);
    m_backLeftSpark.setClosedLoopRampRate(time);

    m_backRightSpark.setOpenLoopRampRate(time);
    m_backRightSpark.setClosedLoopRampRate(time);
  }

  private enum idleMode {
    brake,
    coast
  }

  public void setMode(idleMode type) {
    if (type == idleMode.brake) {
      m_frontLeftSpark.setIdleMode(IdleMode.kBrake);
      m_frontRightSpark.setIdleMode(IdleMode.kBrake);
      m_backLeftSpark.setIdleMode(IdleMode.kBrake);
      m_backRightSpark.setIdleMode(IdleMode.kBrake);
    } else if (type == idleMode.coast) {
      m_frontLeftSpark.setIdleMode(IdleMode.kCoast);
      m_frontRightSpark.setIdleMode(IdleMode.kCoast);
      m_backLeftSpark.setIdleMode(IdleMode.kCoast);
      m_backRightSpark.setIdleMode(IdleMode.kCoast);
    }
  }

  private void setPidControllers (SparkMaxPIDController pidController, Gains pidSet, int slot) {
    pidController.setP(pidSet.kP, slot);
    pidController.setI(pidSet.kI, slot);
    pidController.setD(pidSet.kD, slot);
    pidController.setIZone(pidSet.kIz, slot);
    pidController.setFF(pidSet.kFF, slot);
    pidController.setOutputRange(pidSet.kMinOutput, pidSet.kMaxOutput, slot);
  }
}
