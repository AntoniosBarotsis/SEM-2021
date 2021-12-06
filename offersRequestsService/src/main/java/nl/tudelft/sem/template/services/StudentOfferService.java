package nl.tudelft.sem.template.services;

import java.util.List;
import java.util.Optional;
import javax.naming.NoPermissionException;
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
     * @param userName - the id of the Student, who wants to accept the offer.
     * @param userRole - the role of the Student.
     * @param targetedCompanyOfferId - the id of the accepted offer.
     * @throws NoPermissionException - is thrown
     *      if the user doesn't have permission to accept the offer.
     */
    public void acceptOffer(
            String userName, String userRole, Long targetedCompanyOfferId)
            throws NoPermissionException {
        Optional<TargetedCompanyOffer> targetedCompanyOffer =
                targetedCompanyOfferRepository.findById(targetedCompanyOfferId);
        if (targetedCompanyOffer.isEmpty()) {
            throw new IllegalArgumentException("ID is not valid!");
        }
        StudentOffer offer = targetedCompanyOffer.get().getStudentOffer();
        if (offer
                .getStudentId()
                .equals(userName) || !userRole.equals("STUDENT")) {
            throw new NoPermissionException("User not allowed to accept this TargetedOffer");
        }
        if (offer.getStatus() != Status.PENDING
                || targetedCompanyOffer.get().getStatus() != Status.PENDING) {
            throw new IllegalArgumentException(
                    "The StudentOffer or TargetedRequest is not active anymore!");
        }

        for (TargetedCompanyOffer t : offer.getTargetedCompanyOffers()) {
            if (!t.equals(targetedCompanyOffer.get())) {
                t.setStatus(Status.DECLINED);
            } else {
                t.setStatus(Status.ACCEPTED);
            }
            targetedCompanyOfferRepository.save(t);
        }

        offer.setStatus(Status.DISABLED);
        studentOfferRepository.save(offer);
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
