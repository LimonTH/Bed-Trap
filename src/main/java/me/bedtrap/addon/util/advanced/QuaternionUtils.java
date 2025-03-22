package me.bedtrap.addon.util.advanced;

import org.joml.Quaternionf;

public class QuaternionUtils {
    public static Quaternionf fromEulerXyz(float pitch, float yaw, float roll) {
        // Преобразуем углы в радианы
        float halfYaw = yaw * 0.5f;
        float halfPitch = pitch * 0.5f;
        float halfRoll = roll * 0.5f;

        // Вычисляем синусы и косинусы
        float cosYaw = (float) Math.cos(halfYaw);
        float sinYaw = (float) Math.sin(halfYaw);
        float cosPitch = (float) Math.cos(halfPitch);
        float sinPitch = (float) Math.sin(halfPitch);
        float cosRoll = (float) Math.cos(halfRoll);
        float sinRoll = (float) Math.sin(halfRoll);

        // Вычисляем компоненты кватерниона
        float x = sinYaw * cosPitch * cosRoll - cosYaw * sinPitch * sinRoll;
        float y = cosYaw * sinPitch * cosRoll + sinYaw * cosPitch * sinRoll;
        float z = cosYaw * cosPitch * sinRoll - sinYaw * sinPitch * cosRoll;
        float w = cosYaw * cosPitch * cosRoll + sinYaw * sinPitch * sinRoll;

        return new Quaternionf(x, y, z, w);
    }
}