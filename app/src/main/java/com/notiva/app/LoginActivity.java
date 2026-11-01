package com.notiva.app;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import java.security.GeneralSecurityException;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // CAMPOS DEL LOGIN
        EditText edtCorreo = findViewById(R.id.edtCorreo);
        EditText edtPassword = findViewById(R.id.edtPassword);

        ImageButton btnMostrar =
                findViewById(R.id.btnMostrarPassword);

        // MOSTRAR Y OCULTAR CONTRASEÑA
        btnMostrar.setOnClickListener(v -> {

            passwordVisible = !passwordVisible;

            if (passwordVisible) {

                edtPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );

                btnMostrar.setImageResource(R.drawable.ic_ojo);
                btnMostrar.setContentDescription(
                        "Ocultar contraseña");

            } else {

                edtPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );

                btnMostrar.setImageResource(
                        R.drawable.ic_ojo_cerrado);

                btnMostrar.setContentDescription(
                        "Mostrar contraseña");
            }

            edtPassword.setSelection(edtPassword.length());
        });

        // FLECHA PARA VOLVER
        ImageButton btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> finish());

        // ENLACE DE REGISTRO
        TextView txtRegistro =
                findViewById(R.id.txtIrRegistro);

        String mensaje = "¿No tienes cuenta? Regístrate";
        String palabra = "Regístrate";

        SpannableString texto = new SpannableString(mensaje);

        int inicio = mensaje.indexOf(palabra);
        int fin = inicio + palabra.length();

        ClickableSpan enlace = new ClickableSpan() {

            @Override
            public void onClick(View widget) {

                Intent intent = new Intent(
                        LoginActivity.this,
                        RegisterActivity.class
                );

                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {

                ds.setColor(Color.parseColor("#7548C4"));
                ds.setUnderlineText(false);
                ds.setFakeBoldText(true);
            }
        };

        texto.setSpan(
                enlace,
                inicio,
                fin,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        txtRegistro.setText(texto);

        txtRegistro.setMovementMethod(
                LinkMovementMethod.getInstance()
        );

        txtRegistro.setHighlightColor(Color.TRANSPARENT);

        // DESPLAZAMIENTO AUTOMÁTICO DEL TECLADO
        ScrollView scrollLogin = findViewById(R.id.main);

        KeyboardScrollHelper.configurar(
                scrollLogin,
                edtCorreo,
                edtPassword
        );

        // BOTÓN INICIAR SESIÓN
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {

            String correo = edtCorreo.getText()
                    .toString().trim().toLowerCase(Locale.ROOT);

            String password = edtPassword.getText().toString();

            // VALIDAR CORREO
            if (correo.isEmpty()) {
                edtCorreo.setError("Ingresa tu correo");
                edtCorreo.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                edtCorreo.setError("Ingresa un correo válido");
                edtCorreo.requestFocus();
                return;
            }

            // VALIDAR CONTRASEÑA
            if (password.isEmpty()) {
                edtPassword.setError("Ingresa tu contraseña");
                edtPassword.requestFocus();
                return;
            }

            // EVITAR VARIOS INTENTOS SIMULTÁNEOS
            btnLogin.setEnabled(false);

            // CONSULTAR ROOM FUERA DEL HILO PRINCIPAL
            new Thread(() -> {

                try {

                    NotivaDatabase database =
                            NotivaDatabase.obtenerInstancia(
                                    getApplicationContext()
                            );

                    Usuario usuario = database.usuarioDao()
                            .buscarPorCorreo(correo);

                    boolean accesoCorrecto = false;

                    if (usuario != null) {

                        accesoCorrecto = PasswordUtils.verificarPassword(
                                password,
                                usuario.passwordSalt,
                                usuario.passwordHash
                        );
                    }

                    boolean resultado = accesoCorrecto;

                    runOnUiThread(() -> {

                        btnLogin.setEnabled(true);

                        if (resultado) {

                            // NUEVO: GUARDAR SESIÓN DEL USUARIO
                            SesionManager sesion =
                                    new SesionManager(LoginActivity.this);

                            sesion.iniciarSesion(
                                    usuario.id,
                                    usuario.nombre
                            );

                            Toast.makeText(
                                    LoginActivity.this,
                                    "¡Bienvenido a NOTIVA, "
                                            + usuario.nombre + "! 💜",
                                    Toast.LENGTH_LONG
                            ).show();

                            // ABRIR HOME
                            Intent intent = new Intent(
                                    LoginActivity.this,
                                    HomeActivity.class
                            );

                            intent.putExtra("USUARIO_ID", usuario.id);
                            intent.putExtra(
                                    "USUARIO_NOMBRE",
                                    usuario.nombre
                            );

                            // Evitar regresar al login con el botón Atrás
                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            edtPassword.setError(
                                    "Correo o contraseña incorrectos"
                            );

                            edtPassword.requestFocus();
                        }
                    });

                } catch (GeneralSecurityException e) {

                    runOnUiThread(() -> {

                        btnLogin.setEnabled(true);

                        Toast.makeText(
                                LoginActivity.this,
                                "No se pudo verificar la contraseña",
                                Toast.LENGTH_LONG
                        ).show();
                    });

                } catch (Exception e) {

                    runOnUiThread(() -> {

                        btnLogin.setEnabled(true);

                        Toast.makeText(
                                LoginActivity.this,
                                "Error al iniciar sesión. Intenta de nuevo.",
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }

            }).start();
        });
    }
}
