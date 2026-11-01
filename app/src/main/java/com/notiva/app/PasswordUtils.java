    package com.notiva.app;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {

    private static final int ITERACIONES = 600_000;
    private static final int LONGITUD_HASH = 256;
    private static final int LONGITUD_SAL = 16;

    // Generar una sal aleatoria para cada usuario
    public static String generarSal() {

        byte[] sal = new byte[LONGITUD_SAL];

        SecureRandom random = new SecureRandom();
        random.nextBytes(sal);

        return Base64.encodeToString(
                sal,
                Base64.NO_WRAP
        );
    }

    // Transformar la contraseña con PBKDF2
    public static String generarHash(
            String password,
            String salBase64
    ) throws GeneralSecurityException {

        byte[] sal = Base64.decode(
                salBase64,
                Base64.NO_WRAP
        );

        PBEKeySpec especificacion = new PBEKeySpec(
                password.toCharArray(),
                sal,
                ITERACIONES,
                LONGITUD_HASH
        );

        try {

            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] hash = fabrica
                    .generateSecret(especificacion)
                    .getEncoded();

            return Base64.encodeToString(
                    hash,
                    Base64.NO_WRAP
            );

        } finally {
            especificacion.clearPassword();
        }
    }

    // Comprobar una contraseña al iniciar sesión
    public static boolean verificarPassword(
            String password,
            String salGuardada,
            String hashGuardado
    ) throws GeneralSecurityException {

        String hashCalculado = generarHash(
                password,
                salGuardada
        );

        byte[] calculado = Base64.decode(
                hashCalculado,
                Base64.NO_WRAP
        );

        byte[] guardado = Base64.decode(
                hashGuardado,
                Base64.NO_WRAP
        );

        return MessageDigest.isEqual(
                calculado,
                guardado
        );
    }
}
