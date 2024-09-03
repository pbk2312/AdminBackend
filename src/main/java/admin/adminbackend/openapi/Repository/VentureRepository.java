package admin.adminbackend.openapi.Repository;

import admin.adminbackend.openapi.domain.Venture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentureRepository extends JpaRepository<Venture, Long> {
    /*private final Map<Long, Venture> store = new HashMap<>(); //저장소
    private long sequence = 0L;

    public Venture save(Venture venture) {
        venture.setId(++sequence);
        store.put(venture.getId(), venture);
        return venture;
    }

    public Venture findById(Long id) {
        return store.get(id);*/
}
