package com.notiva.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;


import android.widget.ImageButton;
import android.widget.FrameLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.view.LayoutInflater;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Locale;

import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity {

    private List<Nota> todasLasNotas = new ArrayList<>();

    // CARGAR LAS NOTAS DESDE ROOM
    private void cargarNotas() {

        SesionManager sesion = new SesionManager(this);

        int usuarioId = sesion.obtenerUsuarioId();

        if (usuarioId <= 0) {
            Toast.makeText(
                    this,
                    "No se pudo identificar al usuario",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        new Thread(() -> {

            try {
                NotivaDatabase database =
                        NotivaDatabase.obtenerInstancia(
                                getApplicationContext()
                        );

                List<Nota> notas = database.notaDao()
                        .obtenerPorUsuario(usuarioId);

                runOnUiThread(() -> {

                    if (!isFinishing() && !isDestroyed()) {

                        todasLasNotas = notas;

                        EditText edtBuscarNotas =
                                findViewById(R.id.edtBuscarNotas);

                        filtrarNotas(
                                edtBuscarNotas.getText().toString()
                        );
                    }
                });

            } catch (Exception e) {

                Log.e("NOTIVA", "Error al consultar notas", e);

                runOnUiThread(() -> {
                    Toast.makeText(
                            HomeActivity.this,
                            "No se pudieron cargar las notas",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }

        }).start();
    }

    //FILTRAR NOTAS

    private void filtrarNotas(String busqueda) {

        String textoBuscado = busqueda
                .trim()
                .toLowerCase(Locale.ROOT);

        List<Nota> resultados = new ArrayList<>();

        for (Nota nota : todasLasNotas) {

            String titulo = nota.titulo == null
                    ? ""
                    : nota.titulo.toLowerCase(Locale.ROOT);

            String contenido = nota.contenido == null
                    ? ""
                    : nota.contenido.toLowerCase(Locale.ROOT);

            // Buscar coincidencias en título o contenido
            if (titulo.contains(textoBuscado)
                    || contenido.contains(textoBuscado)) {

                resultados.add(nota);
            }
        }

        mostrarNotas(resultados);
    }


    // MOSTRAR LAS NOTAS EN TARJETAS

    private void mostrarNotas(List<Nota> notas) {

        LinearLayout listaNotas =
                findViewById(R.id.listaNotas);

        ScrollView scrollNotas =
                findViewById(R.id.scrollNotas);

        View estadoVacio =
                findViewById(R.id.estadoVacio);

        TextView txtCantidad =
                findViewById(R.id.txtCantidadNotas);

        listaNotas.removeAllViews();

        int cantidad = notas.size();

        EditText edtBuscarNotas =
                findViewById(R.id.edtBuscarNotas);

        boolean buscando = !edtBuscarNotas.getText()
                .toString().trim().isEmpty();

        if (buscando) {
            txtCantidad.setText(
                    cantidad == 1
                            ? "1 resultado"
                            : cantidad + " resultados"
            );
        } else {
            txtCantidad.setText(
                    cantidad == 1
                            ? "1 nota"
                            : cantidad + " notas"
            );
        }


        if (cantidad == 0) {

            if (buscando) {

                // Hay una búsqueda, pero ninguna coincidencia
                estadoVacio.setVisibility(View.GONE);
                scrollNotas.setVisibility(View.VISIBLE);

                TextView mensaje = new TextView(this);

                mensaje.setText(
                        "🔎 No encontramos notas con esa búsqueda.\n" +
                                "Prueba con otra palabra."
                );

                mensaje.setTextSize(15);
                mensaje.setTextColor(
                        Color.parseColor("#655477")
                );
                mensaje.setGravity(
                        android.view.Gravity.CENTER
                );
                mensaje.setPadding(
                        dp(16), dp(48), dp(16), dp(16)
                );

                listaNotas.addView(mensaje);

            } else {

                // No existen notas guardadas
                estadoVacio.setVisibility(View.VISIBLE);
                scrollNotas.setVisibility(View.GONE);
            }

            return;
        }


        estadoVacio.setVisibility(View.GONE);
        scrollNotas.setVisibility(View.VISIBLE);

        SimpleDateFormat formatoFecha =
                new SimpleDateFormat(
                        "dd MMM yyyy",
                        new Locale("es", "CO")
                );

        for (Nota nota : notas) {

            // TARJETA PRINCIPAL
            LinearLayout tarjeta = new LinearLayout(this);
            tarjeta.setOrientation(LinearLayout.VERTICAL);
            tarjeta.setPadding(dp(16), dp(12), dp(16), dp(16));

            GradientDrawable fondo = new GradientDrawable();
            fondo.setColor(Color.WHITE);
            fondo.setCornerRadius(dp(18));

            tarjeta.setBackground(fondo);

            LinearLayout.LayoutParams parametros =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            parametros.bottomMargin = dp(12);
            tarjeta.setLayoutParams(parametros);

            // FILA SUPERIOR: TÍTULO Y PAPELERA
            LinearLayout filaSuperior = new LinearLayout(this);
            filaSuperior.setOrientation(LinearLayout.HORIZONTAL);
            filaSuperior.setGravity(
                    android.view.Gravity.CENTER_VERTICAL
            );

            TextView titulo = new TextView(this);
            titulo.setText(nota.titulo);
            titulo.setTextSize(18);
            titulo.setTextColor(
                    Color.parseColor("#39235E")
            );
            titulo.setTypeface(null, Typeface.BOLD);

            LinearLayout.LayoutParams paramsTitulo =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                    );

            filaSuperior.addView(titulo, paramsTitulo);

            // BOTÓN VISIBLE PARA ELIMINAR
            ImageButton btnEliminar = new ImageButton(this);

            btnEliminar.setImageResource(
                    R.drawable.ic_papelera
            );

            btnEliminar.setBackgroundColor(
                    Color.TRANSPARENT
            );

            btnEliminar.setContentDescription(
                    "Eliminar nota " + nota.titulo
            );

            btnEliminar.setPadding(
                    dp(12), dp(12), dp(12), dp(12)
            );

            filaSuperior.addView(
                    btnEliminar,
                    new LinearLayout.LayoutParams(
                            dp(48),
                            dp(48)
                    )
            );

            tarjeta.addView(filaSuperior);

            // CONTENIDO DE LA NOTA
            TextView contenido = new TextView(this);

            contenido.setText(nota.contenido);
            contenido.setTextSize(14);
            contenido.setTextColor(
                    Color.parseColor("#655477")
            );
            contenido.setMaxLines(3);
            contenido.setEllipsize(
                    TextUtils.TruncateAt.END
            );
            contenido.setPadding(0, dp(6), 0, dp(10));

            tarjeta.addView(contenido);

            // FILA INFERIOR: EMOCIÓN Y FECHA
            LinearLayout filaInferior = new LinearLayout(this);
            filaInferior.setOrientation(
                    LinearLayout.HORIZONTAL
            );
            filaInferior.setGravity(
                    android.view.Gravity.CENTER_VERTICAL
            );

            TextView emocion = new TextView(this);
            emocion.setText(nota.emocion);
            emocion.setTextSize(13);
            emocion.setTextColor(
                    Color.parseColor("#7548C4")
            );

            filaInferior.addView(
                    emocion,
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                    )
            );

            TextView fecha = new TextView(this);
            fecha.setText(
                    formatoFecha.format(
                            new Date(nota.fechaCreacion)
                    )
            );

            fecha.setTextSize(12);
            fecha.setTextColor(
                    Color.parseColor("#988AA8")
            );

            filaInferior.addView(fecha);
            tarjeta.addView(filaInferior);

            // TOQUE NORMAL: EDITAR
            tarjeta.setOnClickListener(v -> {

                Intent intent = new Intent(
                        HomeActivity.this,
                        NotaEditorActivity.class
                );

                intent.putExtra(
                        "USUARIO_ID",
                        getIntent().getIntExtra("USUARIO_ID", -1)
                );

                intent.putExtra("NOTA_ID", nota.id);

                startActivity(intent);
            });

            // PAPELERA: MOSTRAR CONFIRMACIÓN
            btnEliminar.setOnClickListener(v ->
                    confirmarEliminacion(nota)
            );

            // MANTENER COMPATIBILIDAD CON PULSACIÓN LARGA
            tarjeta.setOnLongClickListener(v -> {
                confirmarEliminacion(nota);
                return true;
            });

            listaNotas.addView(tarjeta);
        }
    }



    private void confirmarEliminacion(Nota nota) {

        // Cargar nuestro diseño personalizado
        View vista = LayoutInflater.from(this)
                .inflate(R.layout.dialog_eliminar_nota, null);

        TextView txtMensaje =
                vista.findViewById(R.id.txtMensajeDialogo);

        TextView btnCancelar =
                vista.findViewById(R.id.btnCancelarDialogo);

        TextView btnEliminar =
                vista.findViewById(R.id.btnEliminarDialogo);

        txtMensaje.setText(
                "Se eliminará \"" + nota.titulo +
                        "\" de forma permanente. Esta acción no se puede deshacer."
        );

        // Crear el diálogo utilizando nuestro XML
        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setView(vista)
                .create();

        // Cancelar sin modificar nada
        btnCancelar.setOnClickListener(v ->
                dialogo.dismiss()
        );

        // Confirmar eliminación
        btnEliminar.setOnClickListener(v -> {
            dialogo.dismiss();
            eliminarNota(nota);
        });

        dialogo.setOnShowListener(d -> {

            if (dialogo.getWindow() != null) {

                dialogo.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                dialogo.getWindow().setLayout(
                        (int) (getResources()
                                .getDisplayMetrics().widthPixels * 0.90f),
                        android.view.WindowManager.LayoutParams.WRAP_CONTENT
                );
            }
        });

        dialogo.show();
    }




    // ELIMINAR UNA NOTA EN ROOM
    private void eliminarNota(Nota nota) {

        SesionManager sesion = new SesionManager(this);

        int usuarioId = sesion.obtenerUsuarioId();

        if (usuarioId <= 0) {
            Toast.makeText(
                    this,
                    "Usuario no válido",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        new Thread(() -> {

            try {

                NotivaDatabase database =
                        NotivaDatabase.obtenerInstancia(
                                getApplicationContext()
                        );

                int eliminadas = database.notaDao()
                        .eliminar(nota.id, usuarioId);

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (eliminadas > 0) {

                        Toast.makeText(
                                HomeActivity.this,
                                "Nota eliminada 💜",
                                Toast.LENGTH_SHORT
                        ).show();

                        cargarNotas();

                    } else {

                        Toast.makeText(
                                HomeActivity.this,
                                "No se encontró la nota",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

            } catch (Exception e) {

                Log.e("NOTIVA", "Error al eliminar nota", e);

                runOnUiThread(() -> {

                    if (!isFinishing() && !isDestroyed()) {

                        Toast.makeText(
                                HomeActivity.this,
                                "No se pudo eliminar la nota",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            }

        }).start();
    }

    // CONVERTIR DP A PÍXELES
    private int dp(int valor) {

        return (int) (
                valor * getResources()
                        .getDisplayMetrics().density + 0.5f
        );
    }

    // ACTUALIZAR LAS NOTAS AL REGRESAR AL HOME
    @Override
    protected void onResume() {
        super.onResume();
        cargarNotas();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);



        ImageButton btnCerrarSesion =
                findViewById(R.id.btnCerrarSesion);

        btnCerrarSesion.setOnClickListener(v -> {

            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("¿Cerrar sesión?")
                    .setMessage(
                            "Podrás volver a entrar con tu correo " +
                                    "y contraseña. Tus notas permanecerán guardadas."
                    )
                    .setNegativeButton("Cancelar", (dialog, which) ->
                            dialog.dismiss()
                    )
                    .setPositiveButton("Cerrar sesión", (dialog, which) -> {

                        // Eliminar la sesión guardada
                        SesionManager sesion =
                                new SesionManager(HomeActivity.this);

                        sesion.cerrarSesion();

                        // Volver a la bienvenida
                        Intent intent = new Intent(
                                HomeActivity.this,
                                MainActivity.class
                        );

                        // Limpiar las pantallas anteriores
                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);
                        finish();
                    })
                    .show();
        });



        EditText edtBuscarNotas =
                findViewById(R.id.edtBuscarNotas);

        edtBuscarNotas.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                        // No necesitamos hacer nada aquí
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {
                        filtrarNotas(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // No necesitamos hacer nada aquí
                    }
                }
        );



        // SALUDO PERSONALIZADO
        SesionManager sesion = new SesionManager(this);

        String nombre = sesion.obtenerNombre();

        TextView txtSaludo =
                findViewById(R.id.txtSaludo);

        if (nombre != null && !nombre.isEmpty()) {
            txtSaludo.setText("¡Hola, " + nombre + "! 💜");
        } else {
            txtSaludo.setText("¡Hola! 💜");
        }

        // BOTÓN PARA CREAR NOTAS
        Button btnNuevaNota =
                findViewById(R.id.btnNuevaNota);

        btnNuevaNota.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    NotaEditorActivity.class
            );

            intent.putExtra(
                    "USUARIO_ID",
                    getIntent().getIntExtra("USUARIO_ID", -1)
            );

            startActivity(intent);
        });

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
    }
}
