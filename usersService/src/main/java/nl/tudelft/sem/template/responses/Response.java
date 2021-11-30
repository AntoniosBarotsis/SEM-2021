package nl.tudelft.sem.template.responses;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response<E> {
    private E data;
    private String errorMessage;
}

