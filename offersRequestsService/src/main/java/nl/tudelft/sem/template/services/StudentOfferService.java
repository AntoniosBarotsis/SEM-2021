package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.StudentOffer;
import nl.tudelft.sem.template.repositories.StudentOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentOfferService extends OfferService {

    @Autowired
    private transient StudentOfferRepository studentOfferRepository;

    public List<StudentOffer> getOffers(){
        return studentOfferRepository.findAll();
    }

    public List<StudentOffer> getOffersById(String studentId){
        if(studentId.length() != 7){
            throw new IllegalArgumentException("An invalid NetId has been entered!");
        }
        List<StudentOffer> offer = studentOfferRepository.findAllByStudentId(studentId);
        if(offer.size() == 0){
            throw new IllegalArgumentException("No such student has made offers!");
        }
        return offer;
    }
}
