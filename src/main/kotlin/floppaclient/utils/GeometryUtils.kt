package floppaclient.utils

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.util.Vec3
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

object GeometryUtils {

    /**
     * Returns array(distance, yaw, pitch) in minecraft coordinate  system to get from x0y0z0 to x1y1z1.
     */
    fun getDirection(X0: Double, Y0: Double, Z0: Double, X1: Double, Y1: Double, Z1: Double): Array<Double> {
        val dist = sqrt((X1 - X0).pow(2) + (Y1 - Y0).pow(2) + (Z1 - Z0).pow(2))
        val yaw = -atan2((X1-X0), (Z1-Z0)) /Math.PI*180
        val pitch = -atan2((Y1-Y0), sqrt((X1 - X0).pow(2) + (Z1 - Z0).pow(2))) /Math.PI*180
        return arrayOf( dist, yaw, pitch)
    }

    /**
     * Returns array(distance, yaw, pitch) in minecraft coordinate  system to get from vec0 to vec1.
     */
    fun getDirection(vec0: Vec3,vec1: Vec3): Array<Double> {
        return this.getDirection(vec0.xCoord, vec0.yCoord, vec0.zCoord, vec1.xCoord, vec1.yCoord, vec1.zCoord)
    }

    /**
     * Returns array(distance, yaw, pitch) in minecraft coordinate  system to get from the Player feet to the given vec.
     * offset can be used to offset the target height.
     */
    fun EntityPlayerSP.getDirection(vec: Vec3, offset: Double): Array<Double> {
        return this@GeometryUtils.getDirection(this.positionVector, vec.addVector(0.0, offset, 0.0))
    }

    /**
     * Returns array(distance, yaw, pitch) in minecraft coordinate  system to get from the Player to the given entity at the same height.
     * offset can be used to offset the target height.
     */
    fun EntityPlayerSP.getDirection(entity: Entity, offset: Double): Array<Double> {
        return this.getDirection(entity.positionVector, offset)
    }

    /**
     * Returns the distance between two lines in 3D space.
     * The lines are passes as two points for each line.
     */
    fun distanceBetweenLines(pointA0: Vec3, pointB0: Vec3, pointA1: Vec3, pointB1: Vec3): Double {
        val vec1 =  pointB0.subtract(pointA0).normalize()
        val vec2 =  pointB1.subtract(pointA1).normalize()
        val normal = vec1.crossProduct(vec2).normalize()
        return if (normal.lengthVector() < 1.0E-4) {
            // the lines are parallell
            pointA0.subtract(pointA1).crossProduct(vec1).lengthVector()
        }else{
            // the lines are skew
            abs(pointA0.subtract(pointA1).dotProduct(normal))
        }
    }
}