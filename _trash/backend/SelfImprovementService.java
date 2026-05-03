package com.example.backend.ai;

import com.example.backend.model.AiAuditLog;
import com.example.backend.repository.AiAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelfImprovementService {

    private final AiAuditLogRepository auditLogRepository;

    /**
     * Periodically analyzes audit logs to identify performance bottlenecks 
     * or reasoning failures.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void analyzeLogs() {
        log.info("Starting automated AI performance analysis...");
        
        List<AiAuditLog> failures = auditLogRepository.findAll().stream()
                .filter(l -> "FAILURE".equals(l.getStatus()))
                .collect(Collectors.toList());

        if (!failures.isEmpty()) {
            log.warn("Detected {} AI failures in the last period.", failures.size());
            // In a real production system, this would send an alert or 
            // trigger a prompt adjustment workflow.
        }

        double avgLatency = auditLogRepository.findAll().stream()
                .mapToLong(AiAuditLog::getLatencyMs)
                .average()
                .orElse(0);

        log.info("Average AI Latency: {}ms", String.format("%.2f", avgLatency));
    }
}
