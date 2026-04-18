package co.edu.uniquindio.triage.service.impl;

import org.springframework.stereotype.Service;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.dto.response.SugerenciaClasificacionResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class IAService {

    public String generarResumen(String texto) {

        if (texto == null || texto.isBlank()) {
            return "Resumen automático:\n\nNo hay información suficiente para generar un resumen.";
        }

        String textoLimpio = limpiarTexto(texto);
        String[] lineas = textoLimpio.split("\n");

        String tipo = extraerValor(lineas, "Tipo:");
        String estado = extraerValor(lineas, "Estado:");
        String prioridad = extraerValor(lineas, "Prioridad:");
        String canal = extraerValor(lineas, "Canal:");
        String fechaRegistro = extraerValor(lineas, "Fecha de registro:");
        String descripcion = extraerValor(lineas, "Descripción:");
        String responsable = extraerValor(lineas, "Responsable:");

        List<String> eventosHistorial = extraerHistorial(lineas);

        StringBuilder resumen = new StringBuilder();
        resumen.append("Resumen automático de la solicitud\n\n");

        // 1. Información general
        resumen.append("Información general:\n");

        if (tipo != null) {
            resumen.append("- Tipo: ").append(tipo).append("\n");
        }
        if (estado != null) {
            resumen.append("- Estado actual: ").append(estado).append("\n");
        }
        if (prioridad != null) {
            resumen.append("- Prioridad: ").append(prioridad).append("\n");
        }
        if (canal != null) {
            resumen.append("- Canal de origen: ").append(canal).append("\n");
        }
        if (fechaRegistro != null) {
            resumen.append("- Fecha de registro: ").append(fechaRegistro).append("\n");
        }
        if (responsable != null && !responsable.equalsIgnoreCase("null")) {
            resumen.append("- Responsable asignado: ").append(responsable).append("\n");
        }

        // 2. Descripción resumida
        resumen.append("\nDescripción general:\n");
        if (descripcion != null && !descripcion.isBlank()) {
            resumen.append(resumirDescripcion(descripcion)).append("\n");
        } else {
            resumen.append("No se encontró una descripción clara de la solicitud.\n");
        }

        // 3. Historial resumido
        resumen.append("\nTrazabilidad:\n");
        if (eventosHistorial.isEmpty()) {
            resumen.append("La solicitud no registra eventos de historial relevantes.\n");
        } else {
            resumen.append(generarResumenHistorial(eventosHistorial)).append("\n");
        }

        // 4. Conclusión automática
        resumen.append("\nConclusión:\n");
        resumen.append(generarConclusion(estado, prioridad));

        return resumen.toString();
    }

    public SugerenciaClasificacionResponse sugerirClasificacion(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            return new SugerenciaClasificacionResponse(
                    TipoSolicitud.CONSULTA_ACADEMICA,
                    Prioridad.BAJA,
                    "No fue posible generar una sugerencia clara porque la descripción está vacía."
            );
        }

        String texto = descripcion.toLowerCase();

        TipoSolicitud tipoSugerido;
        Prioridad prioridadSugerida;
        String justificacion;

        // 1. Sugerencia de tipo según palabras clave
        if (texto.contains("homolog") || texto.contains("equivalencia")) {
            tipoSugerido = TipoSolicitud.HOMOLOGACION;
        } else if (texto.contains("cancel") || texto.contains("retirar")) {
            tipoSugerido = TipoSolicitud.CANCELACION_ASIGNATURAS;
        } else if (texto.contains("cupo") || texto.contains("grupo lleno") || texto.contains("sin cupo")) {
            tipoSugerido = TipoSolicitud.SOLICITUD_CUPOS;
        } else if (texto.contains("registro") || texto.contains("matrícula") || texto.contains("asignatura")) {
            tipoSugerido = TipoSolicitud.REGISTRO_ASIGNATURAS;
        } else {
            tipoSugerido = TipoSolicitud.CONSULTA_ACADEMICA;
        }

        // 2. Sugerencia de prioridad según urgencia detectada en texto
        if (texto.contains("urgente") || texto.contains("hoy") || texto.contains("vence mañana")
                || texto.contains("último día") || texto.contains("inmediato")) {
            prioridadSugerida = Prioridad.CRITICA;
        } else if (texto.contains("pronto") || texto.contains("esta semana") || texto.contains("rápido")) {
            prioridadSugerida = Prioridad.ALTA;
        } else if (texto.contains("solicito") || texto.contains("necesito") || texto.contains("quisiera")) {
            prioridadSugerida = Prioridad.MEDIA;
        } else {
            prioridadSugerida = Prioridad.BAJA;
        }

        // 3. Justificación explicativa
        justificacion = "Sugerencia generada automáticamente a partir de palabras clave encontradas en la descripción. "
                + "Tipo sugerido: " + tipoSugerido + ". "
                + "Prioridad sugerida: " + prioridadSugerida + ". "
                + "Esta sugerencia debe ser confirmada o ajustada por un usuario.";

        return new SugerenciaClasificacionResponse(tipoSugerido, prioridadSugerida, justificacion);
    }

    private String limpiarTexto(String texto) {
        return texto
                .replace("\r", "")
                .replaceAll("[ \t]+", " ")
                .replaceAll("\n{2,}", "\n")
                .trim();
    }

    private String extraerValor(String[] lineas, String prefijo) {
        for (String linea : lineas) {
            if (linea.startsWith(prefijo)) {
                return linea.substring(prefijo.length()).trim();
            }
        }
        return null;
    }

    private List<String> extraerHistorial(String[] lineas) {
        List<String> historial = new ArrayList<>();

        for (String linea : lineas) {
            String limpia = linea.trim();

            if (limpia.startsWith("-") || limpia.startsWith("*")) {
                historial.add(limpia.substring(1).trim());
            } else if (limpia.toUpperCase().contains("REGISTRADA")
                    || limpia.toUpperCase().contains("CLASIFICADA")
                    || limpia.toUpperCase().contains("EN_ATENCION")
                    || limpia.toUpperCase().contains("ATENDIDA")
                    || limpia.toUpperCase().contains("CERRADA")
                    || limpia.toUpperCase().contains("CLASIFICACION")
                    || limpia.toUpperCase().contains("ASIGNACION")
                    || limpia.toUpperCase().contains("CAMBIO_ESTADO")
                    || limpia.toUpperCase().contains("PRIORIDAD")) {
                historial.add(limpia);
            }
        }

        return historial;
    }

    private String resumirDescripcion(String descripcion) {
        String limpia = descripcion.replaceAll("\\s+", " ").trim();

        if (limpia.length() <= 220) {
            return limpia;
        }

        return limpia.substring(0, 220).trim() + "...";
    }

    private String generarResumenHistorial(List<String> eventos) {
        if (eventos.isEmpty()) {
            return "No hay eventos para resumir.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("La solicitud registra ").append(eventos.size()).append(" evento(s) relevante(s). ");

        List<String> eventosClave = eventos.stream().limit(4).toList();

        sb.append("Eventos destacados: ");
        for (int i = 0; i < eventosClave.size(); i++) {
            sb.append(eventosClave.get(i));
            if (i < eventosClave.size() - 1) {
                sb.append(" | ");
            }
        }

        if (eventos.size() > 4) {
            sb.append(" | ...");
        }

        return sb.toString();
    }

    private String generarConclusion(String estado, String prioridad) {
        String estadoSeguro = estado == null ? "" : estado.toUpperCase();
        String prioridadSegura = prioridad == null ? "" : prioridad.toUpperCase();

        String conclusionEstado;

        switch (estadoSeguro) {
            case "REGISTRADA" ->
                    conclusionEstado = "La solicitud fue registrada, pero todavía no completa su flujo de gestión.";
            case "CLASIFICADA" ->
                    conclusionEstado = "La solicitud ya fue clasificada y está lista para continuar su atención operativa.";
            case "EN_ATENCION" ->
                    conclusionEstado = "La solicitud se encuentra en gestión activa por parte del área responsable.";
            case "ATENDIDA" ->
                    conclusionEstado = "La solicitud ya fue atendida y está pendiente de cierre formal.";
            case "CERRADA" ->
                    conclusionEstado = "La solicitud finalizó su ciclo de vida y no debería admitir modificaciones adicionales.";
            default ->
                    conclusionEstado = "No fue posible determinar con precisión el estado actual de la solicitud.";
        }

        String conclusionPrioridad;

        switch (prioridadSegura) {
            case "CRITICA" ->
                    conclusionPrioridad = " Requiere seguimiento inmediato por su criticidad.";
            case "ALTA" ->
                    conclusionPrioridad = " Requiere atención prioritaria.";
            case "MEDIA" ->
                    conclusionPrioridad = " Requiere seguimiento normal dentro del flujo definido.";
            case "BAJA" ->
                    conclusionPrioridad = " No presenta urgencia inmediata.";
            default ->
                    conclusionPrioridad = "";
        }

        return conclusionEstado + conclusionPrioridad;
    }
}