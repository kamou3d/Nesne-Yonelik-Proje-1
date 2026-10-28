package siberolay;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileIncidentRepository implements IIncidentRepository {
    private final String filePath;

    public FileIncidentRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void saveAll(List<SecurityIncident> incidents) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (SecurityIncident inc : incidents) {
                writer.write(inc.toCsvRow());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Dosya kaydetme hatasi: " + e.getMessage());
        }
    }

    @Override
    public List<SecurityIncident> loadAll() {
        List<SecurityIncident> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");
                if (parts.length >= 9) {
                    SecurityIncident inc = IncidentFactory.createIncident(
                            parts[0], parts[1], parts[2], parts[3],
                            Integer.parseInt(parts[4]), parts[7], parts[8]
                    );
                    inc.setStatus(IncidentStatus.valueOf(parts[5]));
                    inc.setAssignedResponderName(parts[6]);
                    list.add(inc);
                }
            }
        } catch (Exception e) {
            System.err.println("Dosya okuma hatasi: " + e.getMessage());
        }
        return list;
    }
}