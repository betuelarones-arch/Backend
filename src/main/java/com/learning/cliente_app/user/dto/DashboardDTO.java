package com.learning.cliente_app.user.dto;

import java.util.List;

public class DashboardDTO {
    private ProgresoSemanalDTO progresoSemanal;
    private EstadisticasDTO estadisticas;
    private List<ActividadRecienteDTO> actividadReciente;

    public DashboardDTO(ProgresoSemanalDTO progresoSemanal, EstadisticasDTO estadisticas,
            List<ActividadRecienteDTO> actividadReciente) {
        this.progresoSemanal = progresoSemanal;
        this.estadisticas = estadisticas;
        this.actividadReciente = actividadReciente;
    }

    public List<ActividadRecienteDTO> getActividadReciente() {
        return actividadReciente;
    }

    public void setActividadReciente(List<ActividadRecienteDTO> actividadReciente) {
        this.actividadReciente = actividadReciente;
    }

    public ProgresoSemanalDTO getProgresoSemanal() {
        return progresoSemanal;
    }

    public void setProgresoSemanal(ProgresoSemanalDTO progresoSemanal) {
        this.progresoSemanal = progresoSemanal;
    }

    public EstadisticasDTO getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(EstadisticasDTO estadisticas) {
        this.estadisticas = estadisticas;
    }

    public static class ProgresoSemanalDTO {
        private double horasActuales;
        private double horasObjetivo;
        private String porcentajeCambio;
        private List<DiaProgresoDTO> dias;

        public ProgresoSemanalDTO(double horasActuales, double horasObjetivo, String porcentajeCambio,
                List<DiaProgresoDTO> dias) {
            this.horasActuales = horasActuales;
            this.horasObjetivo = horasObjetivo;
            this.porcentajeCambio = porcentajeCambio;
            this.dias = dias;
        }

        // Getters and Setters
        public double getHorasActuales() {
            return horasActuales;
        }

        public void setHorasActuales(double horasActuales) {
            this.horasActuales = horasActuales;
        }

        public double getHorasObjetivo() {
            return horasObjetivo;
        }

        public void setHorasObjetivo(double horasObjetivo) {
            this.horasObjetivo = horasObjetivo;
        }

        public String getPorcentajeCambio() {
            return porcentajeCambio;
        }

        public void setPorcentajeCambio(String porcentajeCambio) {
            this.porcentajeCambio = porcentajeCambio;
        }

        public List<DiaProgresoDTO> getDias() {
            return dias;
        }

        public void setDias(List<DiaProgresoDTO> dias) {
            this.dias = dias;
        }
    }

    public static class DiaProgresoDTO {
        private String dia; // L, M, X, J, V, S, D
        private double horas;

        public DiaProgresoDTO(String dia, double horas) {
            this.dia = dia;
            this.horas = horas;
        }

        // Getters and Setters
        public String getDia() {
            return dia;
        }

        public void setDia(String dia) {
            this.dia = dia;
        }

        public double getHoras() {
            return horas;
        }

        public void setHoras(double horas) {
            this.horas = horas;
        }
    }

    public static class EstadisticasDTO {
        private long resumenes;
        private long evaluaciones;
        private long chatsIA;
        private int diasSeguidos;

        public EstadisticasDTO(long resumenes, long evaluaciones, long chatsIA, int diasSeguidos) {
            this.resumenes = resumenes;
            this.evaluaciones = evaluaciones;
            this.chatsIA = chatsIA;
            this.diasSeguidos = diasSeguidos;
        }

        // Getters and Setters
        public long getResumenes() {
            return resumenes;
        }

        public void setResumenes(long resumenes) {
            this.resumenes = resumenes;
        }

        public long getEvaluaciones() {
            return evaluaciones;
        }

        public void setEvaluaciones(long evaluaciones) {
            this.evaluaciones = evaluaciones;
        }

        public long getChatsIA() {
            return chatsIA;
        }

        public void setChatsIA(long chatsIA) {
            this.chatsIA = chatsIA;
        }

        public int getDiasSeguidos() {
            return diasSeguidos;
        }

        public void setDiasSeguidos(int diasSeguidos) {
            this.diasSeguidos = diasSeguidos;
        }
    }
}
