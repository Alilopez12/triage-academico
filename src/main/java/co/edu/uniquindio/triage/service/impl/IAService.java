package co.edu.uniquindio.triage.service.impl;

import org.springframework.stereotype.Service;

@Service
public class IAService {

    public String generarResumen(String texto) {

        String[] lineas = texto.split("\n");

        StringBuilder resumen = new StringBuilder();
        resumen.append("Resumen inteligente:\n\n");

        for (int i = 0; i < Math.min(5, lineas.length); i++) {
            resumen.append("- ").append(lineas[i]).append("\n");
        }

        resumen.append("\nEstado general: Revisar solicitud.");

        return resumen.toString();
    }
}