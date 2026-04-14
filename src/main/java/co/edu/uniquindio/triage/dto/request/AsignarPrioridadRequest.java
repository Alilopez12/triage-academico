package co.edu.uniquindio.triage.dto.request;

import co.edu.uniquindio.triage.domain.enums.ImpactoAcademico;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AsignarPrioridadRequest {

    @NotNull(message = "El impacto académico es obligatorio.")
    private ImpactoAcademico impactoAcademico;

    @NotNull(message = "La fecha límite es obligatoria.")
    private LocalDate fechaLimite;

    public AsignarPrioridadRequest() {
    }

    public AsignarPrioridadRequest(ImpactoAcademico impactoAcademico, LocalDate fechaLimite) {
        this.impactoAcademico = impactoAcademico;
        this.fechaLimite = fechaLimite;
    }

    public ImpactoAcademico getImpactoAcademico() {
        return impactoAcademico;
    }

    public void setImpactoAcademico(ImpactoAcademico impactoAcademico) {
        this.impactoAcademico = impactoAcademico;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }
}