package io.github.viciscat.kustweaks.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class KusUtils {

    private KusUtils() {
        throw new IllegalAccessError("Utility class");
    }

	public static Vec3d rotationToVector(float yaw, float pitch) {
		float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
		float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
		float f2 = -MathHelper.cos(-pitch * 0.017453292F);
		float f3 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
	}

	public static Rotation vectorToRotation(Vec3d vec) {
		return vectorToRotation(vec.x, vec.y, vec.z);
	}

	public static Rotation vectorToRotation(double x, double y, double z) {
		double d3 = MathHelper.sqrt(x * x + z * z);
		float yaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(z, x) * (180D / Math.PI)) - 90);
		float pitch = MathHelper.wrapDegrees((float) (-MathHelper.atan2(y, d3) * (180D / Math.PI)));
		return new Rotation(yaw, pitch);
	}
}
