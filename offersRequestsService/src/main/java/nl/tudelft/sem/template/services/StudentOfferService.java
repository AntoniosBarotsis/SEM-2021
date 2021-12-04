package nl.tudelft.sem.template.services;

import java.util.List;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StudentOfferService extends OfferService {

    @Autowired
    private transient StudentOfferRepository studentOfferRepository;
    @Autowired
    private transient RestTemplate restTemplate;
    @Autowired
    private transient Utility utility;

    /**
     * Service, which returns all active StudentOffers, which are stored in the repository.
     *
     * @return - A list of Pending Student Offers.
     */
    public List<StudentOffer> getOffers() {
        return studentOfferRepository.findAllActive();
    }

    /**
     * Service, which returns all offers created by a Student.
     *
     * @param studentId - the ID of the Student.
     * @return - A list of the Student's Offers.
     */
    public List<StudentOffer> getOffersById(String studentId) {
        utility.userExists(studentId, restTemplate);


        List<StudentOffer> offer = studentOfferRepository.findAllByStudentId(studentId);
        if (offer.isEmpty()) {
            throw new IllegalArgumentException("No such student has made offers!");
        }
        return offer;
    }
}
