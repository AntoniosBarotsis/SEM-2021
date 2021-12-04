package nl.tudelft.sem.template.services;

import java.util.List;
import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.entities.TargetedCompanyOffer;
import nl.tudelft.sem.template.enums.Status;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import nl.tudelft.sem.template.repositories.TargetedCompanyOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentOfferService extends OfferService {

    @Autowired
    private transient StudentOfferRepository studentOfferRepository;
    @Autowired
    private transient TargetedCompanyOfferRepository targetedCompanyOfferRepository;

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
     * Service, which accepts the given targeted offer and declines all others.
     *
     * @param targetedCompanyOffer - the offer, which will be accepted.
     */
    public void acceptOffer(TargetedCompanyOffer targetedCompanyOffer) {
        StudentOffer offer = studentOfferRepository
                .getById(targetedCompanyOffer
                        .getStudentOffer().getId());
        if (offer == null) {
            throw new IllegalArgumentException("Offer is not valid!");
        }
        if (!offer.getTargetedCompanyOffers().contains(targetedCompanyOffer)) {
            throw new IllegalArgumentException(
                    "Student Offer does not contain this Targeted Offer");
        }
        for (TargetedCompanyOffer t : offer.getTargetedCompanyOffers()) {
            if (!t.equals(targetedCompanyOffer)) {
                t.setStatus(Status.DECLINED);
            } else {
                t.setStatus(Status.ACCEPTED);
            }
            targetedCompanyOfferRepository.save(t);
        }

        offer.setStatus(Status.DISABLED);
        studentOfferRepository.save(offer);
    }
}
