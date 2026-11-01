package com.notiva.app;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NotaEditorActivity extends AppCompatActivity {

    // Guarda la emoción elegida
    private String emocionSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nota_editor);

        // DATOS RECIBIDOS DESDE HOME
        SesionManager sesion = new SesionManager(this);

        int usuarioId = sesion.obtenerUsuarioId();
        int notaId = getIntent().getIntExtra("NOTA_ID", -1);

        boolean modoEdicion = notaId > 0;

        // COMPONENTES DEL XML
        TextView txtTituloEditor =
                findViewById(R.id.txtTituloEditor);

        EditText edtTituloNota =
                findViewById(R.id.edtTituloNota);

        EditText edtContenidoNota =
                findViewById(R.id.edtContenidoNota);

        Button btnGuardarNota =
                findViewById(R.id.btnGuardarNota);

        ImageButton btnVolverNota =
                findViewById(R.id.btnVolverNota);

        GridLayout gridEmociones =
                findViewById(R.id.gridEmociones);

        // FLECHA PARA REGRESAR
        btnVolverNota.setOnClickListener(v -> finish());

        // CONFIGURAR EL TÍTULO SEGÚN EL MODO
        if (modoEdicion) {
            txtTituloEditor.setText("Editar nota");
            btnGuardarNota.setText("Guardar cambios");
        } else {
            txtTituloEditor.setText("Nueva nota");
            btnGuardarNota.setText("Guardar nota");
        }

        // CREAR EL SELECTOR DE EMOCIONES
        String[] emociones = {
                "😊 Feliz",
                "😌 Tranquilo",
                "🥹 Emotivo",
                "😢 Triste",
                "😰 Ansioso",
                "✨ Motivado"
        };

        for (String emocion : emociones) {

            TextView opcion = new TextView(this);

            opcion.setText(emocion);
            opcion.setTextSize(13);
            opcion.setTextColor(
                    Color.parseColor("#39235E")
            );
            opcion.setGravity(Gravity.CENTER);
            opcion.setPadding(8, 24, 8, 24);

            GradientDrawable fondo =
                    new GradientDrawable();

            fondo.setColor(Color.WHITE);
            fondo.setCornerRadius(24);

            opcion.setBackground(fondo);

            GridLayout.LayoutParams parametros =
                    new GridLayout.LayoutParams();

            parametros.width = 0;
            parametros.height =
                    GridLayout.LayoutParams.WRAP_CONTENT;

            parametros.columnSpec =
                    GridLayout.spec(GridLayout.UNDEFINED, 1f);

            parametros.setMargins(6, 6, 6, 6);

            opcion.setLayoutParams(parametros);

            // SELECCIONAR UNA EMOCIÓN
            opcion.setOnClickListener(v -> {

                emocionSeleccionada = emocion;

                for (int i = 0;
                     i < gridEmociones.getChildCount();
                     i++) {

                    TextView otraOpcion =
                            (TextView) gridEmociones.getChildAt(i);

                    boolean seleccionada =
                            otraOpcion == opcion;

                    GradientDrawable nuevoFondo =
                            new GradientDrawable();

                    nuevoFondo.setColor(
                            Color.parseColor(
                                    seleccionada
                                            ? "#E8D9FF"
                                            : "#FFFFFF"
                            )
                    );

                    nuevoFondo.setCornerRadius(24);

                    if (seleccionada) {

                        nuevoFondo.setStroke(
                                3,
                                Color.parseColor("#7548C4")
                        );
                    }

                    otraOpcion.setBackground(nuevoFondo);

                    otraOpcion.setTypeface(
                            null,
                            seleccionada
                                    ? Typeface.BOLD
                                    : Typeface.NORMAL
                    );
                }
            });

            gridEmociones.addView(opcion);
        }

        // VALIDAR QUE EXISTA UN USUARIO
        if (usuarioId <= 0) {

            Toast.makeText(
                    this,
                    "No se pudo identificar al usuario",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // SI ESTAMOS EDITANDO, CARGAR LA NOTA ORIGINAL
        if (modoEdicion) {

            btnGuardarNota.setEnabled(false);

            new Thread(() -> {

                try {

                    NotivaDatabase database =
                            NotivaDatabase.obtenerInstancia(
                                    getApplicationContext()
                            );

                    Nota nota = database.notaDao()
                            .buscarPorId(notaId, usuarioId);

                    runOnUiThread(() -> {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        if (nota == null) {

                            Toast.makeText(
                                    NotaEditorActivity.this,
                                    "No se encontró la nota",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();
                            return;
                        }

                        // Rellenar título y contenido
                        edtTituloNota.setText(nota.titulo);
                        edtContenidoNota.setText(nota.contenido);

                        // Marcar la emoción que tenía la nota
                        for (int i = 0;
                             i < gridEmociones.getChildCount();
                             i++) {

                            TextView opcion =
                                    (TextView) gridEmociones
                                            .getChildAt(i);

                            if (opcion.getText().toString()
                                    .equals(nota.emocion)) {

                                opcion.performClick();
                                break;
                            }
                        }

                        btnGuardarNota.setEnabled(true);
                    });

                } catch (Exception e) {

                    android.util.Log.e(
                            "NOTIVA",
                            "Error al cargar la nota",
                            e
                    );

                    runOnUiThread(() -> {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        Toast.makeText(
                                NotaEditorActivity.this,
                                "No se pudo cargar la nota",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                    });
                }

            }).start();
        }

        // BOTÓN GUARDAR
        btnGuardarNota.setOnClickListener(v -> {

            String titulo = edtTituloNota.getText()
                    .toString().trim();

            String contenido = edtContenidoNota.getText()
                    .toString().trim();

            // VALIDAR TÍTULO
            if (titulo.isEmpty()) {

                edtTituloNota.setError("Escribe un título");
                edtTituloNota.requestFocus();
                return;
            }

            // VALIDAR CONTENIDO
            if (contenido.isEmpty()) {

                edtContenidoNota.setError(
                        "Escribe el contenido de tu nota"
                );

                edtContenidoNota.requestFocus();
                return;
            }

            // VALIDAR EMOCIÓN
            if (emocionSeleccionada == null) {

                Toast.makeText(
                        NotaEditorActivity.this,
                        "Selecciona cómo te sientes 💜",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Capturamos la emoción antes del hilo
            String emocion = emocionSeleccionada;

            btnGuardarNota.setEnabled(false);

            // GUARDAR EN SEGUNDO PLANO
            new Thread(() -> {

                try {

                    NotivaDatabase database =
                            NotivaDatabase.obtenerInstancia(
                                    getApplicationContext()
                            );

                    String mensaje;

                    if (modoEdicion) {

                        // UPDATE: modificar nota existente
                        int filasActualizadas =
                                database.notaDao().actualizar(
                                        notaId,
                                        usuarioId,
                                        titulo,
                                        contenido,
                                        emocion
                                );

                        if (filasActualizadas == 0) {
                            throw new IllegalStateException(
                                    "La nota no existe o no pertenece al usuario"
                            );
                        }

                        mensaje = "¡Nota actualizada correctamente! 💜";

                    } else {

                        // CREATE: guardar nota nueva
                        Nota nuevaNota = new Nota();

                        nuevaNota.usuarioId = usuarioId;
                        nuevaNota.titulo = titulo;
                        nuevaNota.contenido = contenido;
                        nuevaNota.emocion = emocion;
                        nuevaNota.fechaCreacion =
                                System.currentTimeMillis();

                        database.notaDao().insertar(nuevaNota);

                        mensaje = "¡Nota guardada correctamente! 💜";
                    }

                    runOnUiThread(() -> {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        Toast.makeText(
                                NotaEditorActivity.this,
                                mensaje,
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                    });

                } catch (Exception e) {

                    android.util.Log.e(
                            "NOTIVA",
                            "Error al guardar la nota",
                            e
                    );

                    runOnUiThread(() -> {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        btnGuardarNota.setEnabled(true);

                        Toast.makeText(
                                NotaEditorActivity.this,
                                "No se pudo guardar la nota",
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }

            }).start();
        });

        // RESPETAR LAS BARRAS DE ANDROID
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
    }
}
