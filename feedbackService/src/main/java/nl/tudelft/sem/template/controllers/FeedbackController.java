package nl.tudelft.sem.template.controllers;

import java.net.URI;
import nl.tudelft.sem.template.domain.Response;
import nl.tudelft.sem.template.domain.dtos.requests.FeedbackRequest;
import nl.tudelft.sem.template.domain.dtos.responses.FeedbackResponse;
import nl.tudelft.sem.template.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@RestController
public class FeedbackController {
    @Autowired
    private transient FeedbackService feedbackService;

    @GetMapping("/{id}")
    public ResponseEntity<Response<FeedbackResponse>> getById(@PathVariable Long id) {
        try {
            var res = feedbackService.getById(id);

            return ResponseEntity
                .ok(new Response<>(res, null));
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(new Response<>(null, e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Response<FeedbackResponse>> create(
        @RequestBody FeedbackRequest feedbackRequest,
        ServletWebRequest servletWebRequest,
        @RequestHeader("x-user-name") String userName,
        @RequestHeader("x-user-role") String userRole
    ) {
        if (userRole == null || userRole.isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(new Response<>(null, "Missing user-role header"));
        }

        try {
            var res =
                feedbackService.create(feedbackRequest, userName, userRole);

            var uri = "http://feedback-service/" + res.second();

            return ResponseEntity
                .created(URI.create(uri))
                .body(new Response<>(res.first(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(null, e.getMessage()));
        }
    }
}
