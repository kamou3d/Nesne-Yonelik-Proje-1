package siberolay;

public class IncidentFactory {
    public static SecurityIncident createIncident(String type, String id, String sourceIp,
                                                  String description, int waitingMinutes,
                                                  String param1, String param2) {
        return switch (type.toUpperCase()) {
            case "DDOS" -> new DDoSIncident(id, sourceIp, description, waitingMinutes,
                    Integer.parseInt(param1), Double.parseDouble(param2));
            case "MALWARE" -> new MalwareIncident(id, sourceIp, description, waitingMinutes,
                    param1, Integer.parseInt(param2));
            case "PHISHING" -> new PhishingIncident(id, sourceIp, description, waitingMinutes,
                    Integer.parseInt(param1), Boolean.parseBoolean(param2));
            default -> throw new IllegalArgumentException("Bilinmeyen guvenlik olayi turu: " + type);
        };
    }
}