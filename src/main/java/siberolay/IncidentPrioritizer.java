package siberolay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class IncidentPrioritizer {
    private static final double AGING_FACTOR = 0.35; // Dakika basina yaslandirma katsayisi

    // Dinamik Oncelik Skoru = Baz Risk Skoru + (Bekleme Suresi * Aging Katsayisi)
    public double calculateDynamicPriorityScore(SecurityIncident incident) {
        return incident.calculateBaseRiskScore() + (incident.getWaitingMinutes() * AGING_FACTOR);
    }

    // Max-Heap (PriorityQueue) kullanarak olaylari dinamik skora gore buyukten kucuge siralar
    public List<SecurityIncident> prioritizeIncidents(List<SecurityIncident> incidents) {
        PriorityQueue<SecurityIncident> maxHeap = new PriorityQueue<>(
                Comparator.comparingDouble(this::calculateDynamicPriorityScore).reversed()
        );

        for (SecurityIncident incident : incidents) {
            if (incident.getStatus() != IncidentStatus.COZULDU) {
                maxHeap.offer(incident);
            }
        }

        List<SecurityIncident> sortedList = new ArrayList<>();
        while (!maxHeap.isEmpty()) {
            sortedList.add(maxHeap.poll());
        }
        return sortedList;
    }

    // Greedy (Acgozlu) Yaklasimla En Uygun Analisti Secme Algoritmasi
    public IncidentResponder findBestResponder(SecurityIncident incident, List<IncidentResponder> responders) {
        return responders.stream()
                .min(Comparator.comparingInt((IncidentResponder r) ->
                        r.getSpecialization().equalsIgnoreCase(incident.getIncidentType()) ? 0 : 1)
                        .thenComparingInt(IncidentResponder::getActiveIncidentCount))
                .orElse(null);
    }
}