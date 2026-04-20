package co.edu.uniquindio.triage.dto.request;

import co.edu.uniquindio.triage.domain.enums.ImpactoAcademico;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AsignarPrioridadRequest {

    @NotNull(message = "El impacto académico es obligatorio.")
    private ImpactoAcademico impactoAcademico;

    @NotNull(message = "La fecha límite es obligatoria.")
    private LocalDate fechaLimite;

    @NotNull(message = "La versión es obligatoria.")
    private Long version;

    public AsignarPrioridadRequest() {
    }

    public AsignarPrioridadRequest(ImpactoAcademico impactoAcademico,
                                   LocalDate fechaLimite,
                                   Long version) {
        this.impactoAcademico = impactoAcademico;
        this.fechaLimite = fechaLimite;
        this.version = version;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}