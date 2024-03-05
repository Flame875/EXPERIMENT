// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
 
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Arm extends SubsystemBase {
 
  TalonFX AngleMotor;
  TalonFX BottomShootingMotor;
  TalonFX CentralShootingMotor;
  TalonFX TopShootingMotor;
 
  DutyCycleEncoder AngleEncoder;

  double CurrentTicks;
  double CurrentAngle;
  double TargetAngle;
 
  VelocityVoltage VelocityVolts;
 
  public Arm() {
 
    // Motors
    AngleMotor = new TalonFX(Constants.CAN_IDs.AngleID,"FRC 1599");
    BottomShootingMotor = new TalonFX(Constants.CAN_IDs.BottomShootingID,"FRC 1599");
    CentralShootingMotor = new TalonFX(Constants.CAN_IDs.CentralShootingID,"FRC 1599");
    TopShootingMotor = new TalonFX(Constants.CAN_IDs.TopShootingID,"FRC 1599");

    VelocityVolts = new VelocityVoltage(0);

    var VelocityConfig = new Slot0Configs();
    VelocityConfig.kV = 0.12;
    VelocityConfig.kP = 0.11;
    VelocityConfig.kI = 0.48;
    VelocityConfig.kD = 0.01;

    TopShootingMotor.getConfigurator().apply(VelocityConfig, 0.050);
    CentralShootingMotor.getConfigurator().apply(VelocityConfig, 0.050);

    AngleMotor.setNeutralMode(NeutralModeValue.Brake);

    AngleEncoder = new DutyCycleEncoder(6);

    AngleEncoder.setPositionOffset(.828);
  
  }

  @Override
  public void periodic() {
 
    if (AngleEncoder.getAbsolutePosition() < .2) {

      CurrentTicks = AngleEncoder.getAbsolutePosition() + 1;
      
    } else {

      CurrentTicks = AngleEncoder.getAbsolutePosition();

    }

    CurrentAngle = -(CurrentTicks / (.072 / 28) - 328) - 14;
 
    SmartDashboard.putNumber("Angle Encoder Degrees", CurrentAngle);
    
  }

  public double ReturnCurrentAngle() {

    return CurrentAngle;

  }
 
  public void RunAngleWithLimits(double Speed) {

    if (Speed < 0 && CurrentAngle >= Constants.UpperArmLimit) {

      AngleMotor.set(0);
      
    } else if (Speed > 0 && CurrentAngle <= Constants.LowerArmLimit) {

      AngleMotor.set(0);
      
    } else {

      AngleMotor.set(Speed);

    }
 
  }

  public void ChangeAngleThroughPID() {

    RunAngleWithLimits(Constants.AnglePID.calculate(CurrentAngle, TargetAngle));
 
  }

  public void ChangeTarget(double Target) {

    TargetAngle = Target;

  }
 
  public void Spinup(double Velocity) {
    
    TopShootingMotor.setControl(VelocityVolts.withVelocity(Velocity));
    CentralShootingMotor.setControl(VelocityVolts.withVelocity(Velocity));
    
  }

  public double ReturnVelocity() {

    return ( TopShootingMotor.getVelocity().getValueAsDouble() + 
    CentralShootingMotor.getVelocity().getValueAsDouble() ) / 2;

  }
 
  public void RunBottom(double speed) {

    BottomShootingMotor.set(-speed);

  }

}