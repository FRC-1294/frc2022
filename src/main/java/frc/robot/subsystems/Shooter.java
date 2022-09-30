// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;
import java.lang.Math;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.Constants;
import frc.robot.constants.IDs;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

public class Shooter extends SubsystemBase {
  /** Creates a new Shooter. */
  Joysticks joysticks;
  TalonFX mainTalon = new TalonFX(IDs.flyWheelID);
  TalonFX rollerTalon = new TalonFX(IDs.rollerID);
  int shooterModeIndex = 0;
  ShooterMode shooterMode = ShooterMode.off;
  boolean shooterReady = false;
  boolean shooterScrollPressed = false;
  double setVelocityMain = 0;
  double setVelocityRoller = 0;

  final double rpmDiff = 200;
  double rpmOffset = -400;
 //gaming starts here 
  final boolean tuningRPM = false;
  final ShooterMode[] modes = new ShooterMode[] 
  {ShooterMode.normal};
  
  public Shooter(Joysticks joysticks) {
    this.joysticks = joysticks;

    mainTalon.config_kP(0, Constants.shooterMainPID[0]);
    mainTalon.config_kI(0, Constants.shooterMainPID[1]);
    mainTalon.config_kD(0, Constants.shooterMainPID[2]);
    mainTalon.config_kF(0, Constants.shooterMainPID[3]);
    
    rollerTalon.config_kP(0, Constants.shooterRollerPID[0]);
    rollerTalon.config_kI(0, Constants.shooterRollerPID[1]);
    rollerTalon.config_kD(0, Constants.shooterRollerPID[2]);
    rollerTalon.config_kF(0, Constants.shooterRollerPID[3]);

    if (tuningRPM) {
      SmartDashboard.putNumber("mainRoll", 0);
      SmartDashboard.putNumber("secondRoll", 0); 
    }
  }

  public enum ShooterMode {
    normal, normalLess, normalLessPlus, ballReject, off;
  }


  @Override
  public void periodic() {
    // shooter toggle
    if (joysticks.getShooterToggle()) {
      if (shooterMode != ShooterMode.off) setShooterMode(ShooterMode.off);
      else setShooterMode(modes[shooterModeIndex]);
    }
    else if (joysticks.getSlowModeShooter()) setShooterMode(ShooterMode.ballReject);

    if (joysticks.getNotPOV()) shooterScrollPressed = false;
    if (joysticks.getShooterScrollRight() && !shooterScrollPressed){
      if (shooterModeIndex < modes.length -1) shooterModeIndex++;
      shooterScrollPressed = true;
    }
    else if (joysticks.getShooterScrollLeft() && !shooterScrollPressed){
      if (shooterModeIndex > 0) shooterModeIndex--;
      shooterScrollPressed = true;
    } 
    else if (joysticks.getShooterScrollUp() && !shooterScrollPressed){
      rpmOffset += rpmDiff;
      shooterScrollPressed = true;
    }
    else if (joysticks.getShooterScrollDown() && !shooterScrollPressed){
      rpmOffset -= rpmDiff;
      shooterScrollPressed = true;
    } 

    SmartDashboard.putNumber("RPM Offset", rpmOffset);
    SmartDashboard.putString("Selected Shooter Mode", modes[shooterModeIndex] + "");

    //shooter mode
    runShooter();
  }

  public boolean getShooterReady() {
    return shooterReady;
  }
  
  public void setShooterMode(ShooterMode mode) {
    this.shooterMode = mode;
  }

  public void stopShooter() {
    this.shooterMode = ShooterMode.off;
    runShooter();
  }
  
  public void runShooter() {
    if (tuningRPM) {
      mainTalon.set(TalonFXControlMode.PercentOutput, SmartDashboard.getNumber("mainRoll", 0));
      rollerTalon.set(TalonFXControlMode.PercentOutput, SmartDashboard.getNumber("secondRoll", 0));
      
      SmartDashboard.putNumber("Shooter-Main", mainTalon.getSelectedSensorVelocity());
      SmartDashboard.putNumber("Shooter-Roller", rollerTalon.getSelectedSensorVelocity());
      return;
    }
    else if (shooterMode == ShooterMode.normal) {
      setVelocityMain = 6880;//7000//6000;//5100;//5600;//5200;//6400;
      setVelocityRoller = -3345-rpmOffset;//-3600//-2000;//-4650;//-4300;//-4600;//-4200;
    }
    else if (shooterMode == ShooterMode.normalLess) {
      setVelocityMain = 5200;
      setVelocityRoller = -4400-rpmOffset;

    }
    else if (shooterMode == ShooterMode.normalLessPlus) {
      setVelocityMain = 5200;
      setVelocityRoller = -4200-rpmOffset;
    }
    else if (shooterMode == ShooterMode.ballReject) {
      setVelocityMain = 2000;
      setVelocityRoller = -2000;
    }
    else if (shooterMode == ShooterMode.off) {
      setVelocityMain = 0;
      setVelocityRoller = 0;
    }

    if (setVelocityMain == 0) mainTalon.set(TalonFXControlMode.PercentOutput, 0);
    else mainTalon.set(TalonFXControlMode.Velocity, setVelocityMain);

    if (setVelocityRoller == 0) rollerTalon.set(TalonFXControlMode.PercentOutput, 0);
    else rollerTalon.set(TalonFXControlMode.Velocity, setVelocityRoller);

    shooterReady = Math.abs(Math.abs(setVelocityMain)-Math.abs(mainTalon.getSelectedSensorVelocity())) <= Constants.shooterDeadzone &&
    Math.abs(Math.abs(setVelocityRoller)-Math.abs(rollerTalon.getSelectedSensorVelocity())) <= Constants.shooterDeadzone && 
                    setVelocityMain != 0 && setVelocityRoller != 0;

    SmartDashboard.putBoolean("Shooter Running", mainTalon.getMotorOutputPercent() != 0 || rollerTalon.getMotorOutputPercent() != 0);

    if (shooterReady) joysticks.rumbleOperator(1);
    else joysticks.rumbleOperator(0);

    SmartDashboard.putNumber("Shooter-Main", mainTalon.getSelectedSensorVelocity());
    SmartDashboard.putNumber("Shooter-Roller", rollerTalon.getSelectedSensorVelocity());
  }
}