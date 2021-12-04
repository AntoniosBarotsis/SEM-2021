package nl.tudelft.sem.template.services;

import java.util.List;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentOfferService extends OfferService {

    @Autowired
    private transient StudentOfferRepository studentOfferRepository;

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
        List<StudentOffer> offer = studentOfferRepository.findAllByStudentId(studentId);
        if (offer.isEmpty()) {
            throw new IllegalArgumentException("No such student has made offers!");
        }
        return offer;
    }

    /**
     * Service, which updates a StudentOffer.
     *
     * @param studentOffer - The updated offer, which will be now stored.
     */
    public void updateStudentOffer(StudentOffer studentOffer) {
        StudentOffer current =  studentOfferRepository.getById(studentOffer.getId());
        if (current == null) {
            throw new IllegalArgumentException("This StudentOffer does not exist!");
        }
        if (current.getStatus() != studentOffer.getStatus()) {
            throw new IllegalArgumentException("You are not allowed to edit the Status");
        }

        super.saveOffer(studentOffer);
    }
}
