package nl.tudelft.sem.template.repositories;

import java.util.List;
import nl.tudelft.sem.template.entities.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> getAll();
}
