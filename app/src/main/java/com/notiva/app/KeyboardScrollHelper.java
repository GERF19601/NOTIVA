
package com.notiva.app;

import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class KeyboardScrollHelper {

    public static void configurar(
            ScrollView scroll,
            EditText... campos) {

        // Ajustar el espacio cuando aparece el teclado
        ViewCompat.setOnApplyWindowInsetsListener(
                scroll, (vista, insets) -> {

                    Insets barras = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    Insets teclado = insets.getInsets(
                            WindowInsetsCompat.Type.ime()
                    );

                    int espacioInferior = Math.max(
                            barras.bottom,
                            teclado.bottom
                    );

                    scroll.setPadding(
                            barras.left,
                            barras.top,
                            barras.right,
                            espacioInferior
                    );

                    if (insets.isVisible(
                            WindowInsetsCompat.Type.ime())) {

                        scroll.post(() -> moverAlCampo(scroll));
                    }

                    return insets;
                }
        );

        // Detectar cuando seleccionamos un campo
        for (EditText campo : campos) {

            campo.setOnFocusChangeListener(
                    (vista, tieneFoco) -> {

                        if (tieneFoco) {

                            campo.postDelayed(
                                    () -> moverAlCampo(scroll),
                                    200
                            );
                        }
                    }
            );
        }

        // Detectar cambios en el espacio visible
        scroll.getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        () -> moverAlCampo(scroll)
                );
    }

    private static void moverAlCampo(ScrollView scroll) {

        WindowInsetsCompat insets =
                ViewCompat.getRootWindowInsets(scroll);

        if (insets == null) {
            return;
        }

        // No necesitamos mover nada si el teclado está cerrado
        if (!insets.isVisible(
                WindowInsetsCompat.Type.ime())) {
            return;
        }

        View campo = scroll.findFocus();

        if (!(campo instanceof EditText)) {
            return;
        }

        // Obtener las posiciones en pantalla
        int[] posicionCampo = new int[2];
        int[] posicionScroll = new int[2];

        campo.getLocationOnScreen(posicionCampo);
        scroll.getLocationOnScreen(posicionScroll);

        int margen = (int) (
                16 * scroll.getResources()
                        .getDisplayMetrics().density
        );

        int fondoCampo =
                posicionCampo[1] + campo.getHeight();

        int fondoVisible =
                posicionScroll[1]
                        + scroll.getHeight()
                        - scroll.getPaddingBottom();

        int diferencia =
                fondoCampo + margen - fondoVisible;

        // Subir solamente si el teclado tapa el campo
        if (diferencia > 0) {

            scroll.scrollBy(0, diferencia);
        }
    }
}
