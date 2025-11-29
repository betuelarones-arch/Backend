package com.learning.cliente_app.user.service;

import com.learning.cliente_app.chatbot.service.ChatbotService;
import com.learning.cliente_app.lecciones.repository.EvaluacionRepository;
import com.learning.cliente_app.resumen.repository.ResumenRepository;
import com.learning.cliente_app.user.dto.DashboardDTO;
import com.learning.cliente_app.user.model.ProgresoDiario;
import com.learning.cliente_app.user.repository.ProgresoDiarioRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

        private final ProgresoDiarioRepository progresoRepository;
        private final ResumenRepository resumenRepository;
        private final EvaluacionRepository evaluacionRepository;
        private final ChatbotService chatbotService;

        public DashboardService(ProgresoDiarioRepository progresoRepository,
                        ResumenRepository resumenRepository,
                        EvaluacionRepository evaluacionRepository,
                        ChatbotService chatbotService) {
                this.progresoRepository = progresoRepository;
                this.resumenRepository = resumenRepository;
                this.evaluacionRepository = evaluacionRepository;
                this.chatbotService = chatbotService;
        }

        public DashboardDTO getDashboardData(Long userId) {
                // 1. Calculate Weekly Progress
                LocalDate now = LocalDate.now();
                LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                List<ProgresoDiario> weeklyProgress = progresoRepository.findByUsuarioIdAndFechaBetween(userId,
                                startOfWeek,
                                endOfWeek);

                double totalHours = weeklyProgress.stream()
                                .mapToInt(ProgresoDiario::getMinutosEstudio)
                                .sum() / 60.0;

                double targetHours = 30.0; // Example target
                String percentageChange = "+15% esta semana"; // Placeholder logic for now

                List<DashboardDTO.DiaProgresoDTO> days = new ArrayList<>();
                Map<DayOfWeek, Double> hoursByDay = weeklyProgress.stream()
                                .collect(Collectors.toMap(
                                                p -> p.getFecha().getDayOfWeek(),
                                                p -> p.getMinutosEstudio() / 60.0));

                String[] dayLabels = { "L", "M", "X", "J", "V", "S", "D" };
                DayOfWeek[] dayOfWeeks = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY };

                for (int i = 0; i < 7; i++) {
                        days.add(new DashboardDTO.DiaProgresoDTO(dayLabels[i],
                                        hoursByDay.getOrDefault(dayOfWeeks[i], 0.0)));
                }

                DashboardDTO.ProgresoSemanalDTO progresoSemanal = new DashboardDTO.ProgresoSemanalDTO(
                                totalHours, targetHours, percentageChange, days);

                // 2. Calculate Stats
                long resumenesCount = resumenRepository.countByUsuarioId(userId);
                long evaluacionesCount = evaluacionRepository.countByUsuarioId(userId);
                long chatsCount = chatbotService.countChatsByUserId(userId);
                int streak = calculateStreak(userId);

                DashboardDTO.EstadisticasDTO estadisticas = new DashboardDTO.EstadisticasDTO(
                                resumenesCount, evaluacionesCount, chatsCount, streak);

                // 3. Recent Activity
                List<com.learning.cliente_app.user.dto.ActividadRecienteDTO> recentActivity = getRecentActivity(userId);

                return new DashboardDTO(progresoSemanal, estadisticas, recentActivity);
        }

        private List<com.learning.cliente_app.user.dto.ActividadRecienteDTO> getRecentActivity(Long userId) {
                List<com.learning.cliente_app.user.dto.ActividadRecienteDTO> activities = new ArrayList<>();

                // Summaries
                resumenRepository.findTop5ByUsuarioIdOrderByFechaCreacionDesc(userId).forEach(r -> activities
                                .add(new com.learning.cliente_app.user.dto.ActividadRecienteDTO(
                                                "RES-" + r.getId(),
                                                "Resumen",
                                                "Resumen: " + (r.getTitulo() != null ? r.getTitulo() : "Sin título"),
                                                "Hace " + calculateTimeAgo(r.getFechaCreacion().toLocalDateTime()),
                                                r.getFechaCreacion().toLocalDateTime())));

                // Evaluations
                evaluacionRepository.findTop5ByUsuarioIdOrderByFechaRealizacionDesc(userId).forEach(e -> activities
                                .add(new com.learning.cliente_app.user.dto.ActividadRecienteDTO(
                                                "EVAL-" + e.getId(),
                                                "Evaluación",
                                                "Evaluación: " + e.getLeccion().getTitulo() + " (" + e.getNota() + "%)",
                                                "Hace " + calculateTimeAgo(e.getFechaRealizacion().toLocalDateTime()),
                                                e.getFechaRealizacion().toLocalDateTime())));

                // Chats
                chatbotService.getRecentChats(userId).forEach(c -> {
                        String lastMsg = c.getMensajes().isEmpty() ? "Nueva conversación"
                                        : c.getMensajes().get(c.getMensajes().size() - 1).getContenido();
                        if (lastMsg.length() > 30)
                                lastMsg = lastMsg.substring(0, 27) + "...";

                        activities.add(new com.learning.cliente_app.user.dto.ActividadRecienteDTO(
                                        "CHAT-" + c.getId(),
                                        "Chat",
                                        "Chat: " + lastMsg,
                                        "Hace " + calculateTimeAgo(c.getLastActivity()),
                                        c.getLastActivity()));
                });

                // Sort and limit
                return activities.stream()
                                .sorted((a1, a2) -> a2.getFecha().compareTo(a1.getFecha()))
                                .limit(5)
                                .collect(Collectors.toList());
        }

        private String calculateTimeAgo(java.time.LocalDateTime dateTime) {
                java.time.Duration duration = java.time.Duration.between(dateTime, java.time.LocalDateTime.now());
                long minutes = duration.toMinutes();
                if (minutes < 60)
                        return minutes + " min";
                long hours = duration.toHours();
                if (hours < 24)
                        return hours + "h";
                long days = duration.toDays();
                return days + " días";
        }

        private int calculateStreak(Long userId) {
                // Simple streak calculation based on consecutive days with activity
                // This is a simplified version. Real version would query past days.
                return 5; // Placeholder
        }
}
