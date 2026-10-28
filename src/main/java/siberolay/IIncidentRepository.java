package siberolay;

import java.util.List;

public interface IIncidentRepository {
    void saveAll(List<SecurityIncident> incidents);
    List<SecurityIncident> loadAll();
}