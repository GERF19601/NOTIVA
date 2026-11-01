
package com.notiva.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    private AnimatorSet latido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // COMPROBAR SI EXISTE UNA SESIÓN LOCAL
        SesionManager sesion = new SesionManager(this);
        boolean sesionGuardada = sesion.haySesion();

        // Si hay sesión, ocultamos temporalmente la bienvenida
        // mientras comprobamos el usuario en Room.
        if (sesionGuardada) {
            getWindow().getDecorView().setVisibility(View.INVISIBLE);
        }

        setContentView(R.layout.activity_main);

        // BOTÓN DE BIENVENIDA
        Button btnComenzar =
                findViewById(R.id.btnComenzar);

        btnComenzar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
        });

        // ANIMACIÓN LOTTIE
        LottieAnimationView libreta =
                findViewById(R.id.imgBienvenida);

        ObjectAnimator escalaX = ObjectAnimator.ofFloat(
                libreta, "scaleX", 1f, 1.08f
        );

        ObjectAnimator escalaY = ObjectAnimator.ofFloat(
                libreta, "scaleY", 1f, 1.08f
        );

        escalaX.setRepeatCount(ValueAnimator.INFINITE);
        escalaY.setRepeatCount(ValueAnimator.INFINITE);

        escalaX.setRepeatMode(ValueAnimator.REVERSE);
        escalaY.setRepeatMode(ValueAnimator.REVERSE);

        escalaX.setDuration(1800);
        escalaY.setDuration(1800);

        latido = new AnimatorSet();
        latido.playTogether(escalaX, escalaY);

        latido.setInterpolator(
                new AccelerateDecelerateInterpolator()
        );

        latido.start();

        // RESPETAR LAS BARRAS DEL SISTEMA
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets barras = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            barras.left,
                            barras.top,
                            barras.right,
                            barras.bottom
                    );

                    return insets;
                }
        );

        // VERIFICAR LA SESIÓN GUARDADA
        if (sesionGuardada) {

            new Thread(() -> {

                try {

                    int usuarioId = sesion.obtenerUsuarioId();

                    NotivaDatabase database =
                            NotivaDatabase.obtenerInstancia(
                                    getApplicationContext()
                            );

                    Usuario usuario = database.usuarioDao()
                            .buscarPorId(usuarioId);

                    runOnUiThread(() -> {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        if (usuario != null
                                && sesion.obtenerUsuarioId() == usuario.id) {

                            // Usuario válido: abrir Home
                            Intent intent = new Intent(
                                    MainActivity.this,
                                    HomeActivity.class
                            );

                            intent.putExtra(
                                    "USUARIO_ID",
                                    usuario.id
                            );

                            intent.putExtra(
                                    "USUARIO_NOMBRE",
                                    usuario.nombre
                            );

                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            // Sesión inválida: mostrar bienvenida
                            sesion.cerrarSesion();

                            getWindow().getDecorView()
                                    .setVisibility(View.VISIBLE);
                        }
                    });

                } catch (Exception e) {

                    Log.e(
                            "NOTIVA",
                            "Error al verificar sesión",
                            e
                    );

                    runOnUiThread(() -> {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        // Ante un error, mostrar la bienvenida.
                        getWindow().getDecorView()
                                .setVisibility(View.VISIBLE);

                        Toast.makeText(
                                MainActivity.this,
                                "No se pudo recuperar la sesión",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
                }

            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (latido != null) {
            latido.cancel();
        }
    }
}
