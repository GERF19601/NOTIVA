package com.notiva.app;

import android.os.Bundle;
import android.graphics.Color;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteConstraintException;
import java.security.GeneralSecurityException;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    // METODO REUTILIZABLE PARA LOS OJITOS
    private void configurarOjito(
            EditText campo,
            ImageButton boton) {

        boton.setOnClickListener(v -> {

            boolean estaOculta =
                    (campo.getInputType()
                            & InputType.TYPE_MASK_VARIATION)
                            == InputType.TYPE_TEXT_VARIATION_PASSWORD;

            if (estaOculta) {

                campo.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );

                boton.setImageResource(R.drawable.ic_ojo);
                boton.setContentDescription(
                        "Ocultar contraseña");

            } else {

                campo.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );

                boton.setImageResource(
                        R.drawable.ic_ojo_cerrado);

                boton.setContentDescription(
                        "Mostrar contraseña");
            }

            campo.setSelection(campo.length());
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // CAMPOS DEL REGISTRO
        EditText edtNombre =
                findViewById(R.id.edtNombreRegistro);

        EditText edtCorreo =
                findViewById(R.id.edtCorreoRegistro);

        EditText edtPassword =
                findViewById(R.id.edtPasswordRegistro);

        EditText edtConfirmar =
                findViewById(R.id.edtConfirmarPassword);

        // BOTONES DE VISIBILIDAD
        ImageButton btnOjoPassword =
                findViewById(R.id.btnOjoRegistro);

        ImageButton btnOjoConfirmar =
                findViewById(R.id.btnOjoConfirmar);

        configurarOjito(edtPassword, btnOjoPassword);
        configurarOjito(edtConfirmar, btnOjoConfirmar);

        // FLECHA PARA VOLVER AL LOGIN
        ImageButton btnVolverRegistro =
                findViewById(R.id.btnVolverRegistro);

        btnVolverRegistro.setOnClickListener(
                v -> finish()
        );

        // ENLACE INICIA SESIÓN
        TextView txtIrLogin =
                findViewById(R.id.txtIrLogin);

        String mensaje = "¿Ya tienes cuenta? Inicia sesión";
        String palabra = "Inicia sesión";

        SpannableString texto = new SpannableString(mensaje);

        int inicio = mensaje.indexOf(palabra);
        int fin = inicio + palabra.length();

        ClickableSpan enlace = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                finish();
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

        txtIrLogin.setText(texto);

        txtIrLogin.setMovementMethod(
                LinkMovementMethod.getInstance()
        );

        txtIrLogin.setHighlightColor(Color.TRANSPARENT);

        // DESPLAZAMIENTO AUTOMÁTICO DEL TECLADO
        ScrollView scrollRegistro = findViewById(R.id.main);

        KeyboardScrollHelper.configurar(
                scrollRegistro,
                edtNombre,
                edtCorreo,
                edtPassword,
                edtConfirmar
        );


        Button btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        btnCrearCuenta.setOnClickListener(v -> {

            String nombre = edtNombre.getText().toString().trim();
            String correo = edtCorreo.getText().toString().trim();
            String password = edtPassword.getText().toString();
            String confirmar = edtConfirmar.getText().toString();

            // 1. Validar nombre
            if (nombre.isEmpty()) {
                edtNombre.setError("Ingresa tu nombre");
                edtNombre.requestFocus();
                return;
            }

            // 2. Validar correo
            if (correo.isEmpty()) {
                edtCorreo.setError("Ingresa tu correo electrónico");
                edtCorreo.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                edtCorreo.setError("Ingresa un correo válido");
                edtCorreo.requestFocus();
                return;
            }

            // 3. Validar contraseña
            if (password.length() < 8) {
                edtPassword.setError("Mínimo 8 caracteres");
                edtPassword.requestFocus();
                return;
            }

            // 4. Confirmar contraseña
            if (!password.equals(confirmar)) {
                edtConfirmar.setError("Las contraseñas no coinciden");
                edtConfirmar.requestFocus();
                return;
            }


            /* Los cuatro campos ya pasaron las validaciones */

            // Normalizamos el correo antes de buscarlo y guardarlo
            String correoNormalizado = correo.toLowerCase(Locale.ROOT);

            // Evitamos pulsar varias veces mientras se registra
            btnCrearCuenta.setEnabled(false);

            // Creamos un hilo secundario
            new Thread(() -> {

                try {
                    // 1. Obtener nuestra base de datos
                    NotivaDatabase database =
                            NotivaDatabase.obtenerInstancia(
                                    getApplicationContext()
                            );

                    UsuarioDao usuarioDao = database.usuarioDao();

                    // 2. Comprobar si el correo ya existe
                    Usuario existente =
                            usuarioDao.buscarPorCorreo(correoNormalizado);

                    if (existente != null) {

                        runOnUiThread(() -> {
                            btnCrearCuenta.setEnabled(true);

                            edtCorreo.setError(
                                    "Este correo ya está registrado"
                            );

                            edtCorreo.requestFocus();
                        });

                        return;
                    }

                    // 3. Proteger la contraseña
                    String sal = PasswordUtils.generarSal();

                    String hash = PasswordUtils.generarHash(
                            password,
                            sal
                    );

                    // 4. Crear el objeto Usuario
                    Usuario nuevoUsuario = new Usuario();

                    nuevoUsuario.nombre = nombre;
                    nuevoUsuario.correo = correoNormalizado;
                    nuevoUsuario.passwordSalt = sal;
                    nuevoUsuario.passwordHash = hash;

                    // 5. Guardar el usuario en Room
                    usuarioDao.insertar(nuevoUsuario);

                    // 6. Volver a la interfaz
                    runOnUiThread(() -> {

                        Toast.makeText(
                                RegisterActivity.this,
                                "¡Cuenta creada correctamente! 💜",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                    });

                } catch (SQLiteConstraintException e) {

                    // Por seguridad, también controlamos
                    // si otro registro insertó el mismo correo
                    runOnUiThread(() -> {
                        btnCrearCuenta.setEnabled(true);
                        edtCorreo.setError(
                                "Este correo ya está registrado"
                        );
                        edtCorreo.requestFocus();
                    });

                } catch (GeneralSecurityException e) {

                    runOnUiThread(() -> {
                        btnCrearCuenta.setEnabled(true);

                        Toast.makeText(
                                RegisterActivity.this,
                                "No se pudo proteger la contraseña",
                                Toast.LENGTH_LONG
                        ).show();
                    });

                } catch (Exception e) {

                    runOnUiThread(() -> {
                        btnCrearCuenta.setEnabled(true);

                        Toast.makeText(
                                RegisterActivity.this,
                                "No se pudo crear la cuenta. Intenta de nuevo.",
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }

            }).start();

        });

    }
}
