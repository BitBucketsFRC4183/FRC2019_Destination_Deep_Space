/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.navigation;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
/**
 * Add your docs here.
 */
public class BitBucketsAHRS {
    private static AHRS inst;

    public static AHRS instance() {
        if (inst == null){
            inst = new AHRS(SPI.Port.kMXP);
        }
        return inst;
    }
    private BitBucketsAHRS(){};

}
