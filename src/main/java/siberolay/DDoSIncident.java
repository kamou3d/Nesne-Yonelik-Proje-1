package siberolay;

public class DDoSIncident extends SecurityIncident {
    private int requestsPerSecond;
    private double bandwidthGbps;

    public DDoSIncident(String id, String sourceIp, String description, int waitingMinutes, int requestsPerSecond, double bandwidthGbps) {
        super(id, sourceIp, description, waitingMinutes);
        if (requestsPerSecond < 0 || bandwidthGbps < 0) {
            throw new IllegalArgumentException("DDoS trafik degerleri negatif olamaz!");
        }
        this.requestsPerSecond = requestsPerSecond;
        this.bandwidthGbps = bandwidthGbps;
    }

    @Override
    public double calculateBaseRiskScore() {
        double rpsScore = Math.min(60.0, requestsPerSecond / 1000.0);
        double bwScore = Math.min(40.0, bandwidthGbps * 4.0);
        return Math.min(100.0, rpsScore + bwScore);
    }

    @Override
    public String getIncidentType() { return "DDOS"; }

    @Override
    public String getResponsePlaybook() {
        return "1. Rate Limiting aktif et | 2. Kaynak IP'yi WAF uzerinden blokla | 3. Trafik yikama (Scrubbing) merkezine yonlendir.";
    }

    @Override
    public String toCsvRow() {
        return String.join(";", "DDOS", getId(), getSourceIp(), getDescription(),
                String.valueOf(getWaitingMinutes()), getStatus().name(), getAssignedResponderName(),
                String.valueOf(requestsPerSecond), String.valueOf(bandwidthGbps));
    }
}