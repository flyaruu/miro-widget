package io.floodplain.miroassignment;

import io.floodplain.miroassignment.model.RateLimitResponse;
import io.floodplain.miroassignment.model.RateLimiter;
import io.floodplain.miroassignment.model.Widget;
import io.floodplain.miroassignment.model.WidgetService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("widgetController")
public class WidgetController {

    public static final int LIST_WIDGET_MODIFIER = 5;
    private final WidgetService service;
    private final RateLimiter rateLimiter;
    WidgetController(WidgetService service, RateLimiter rateLimiter) {
        this.service = service;
        this.rateLimiter = rateLimiter;
    }

    // TODO using param for pagination
    @GetMapping("/widget")
    public ResponseEntity<List<Widget>> getWidget(@RequestParam(required = false) Integer from, @RequestParam(required = false, defaultValue = "500") int count) {
        // Require 5 tokens, making listing 5x more expensive than regular calls.
        // Potentially subjective interpretation of the spec
        RateLimitResponse rateLimitResponse = rateLimiter.request(LIST_WIDGET_MODIFIER);
        if(!rateLimitResponse.success()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(parseRateLimitHeaders(rateLimitResponse)).build();
        }

        // 'From' is nullable, if present paginate, otherwise don't
        if(from!=null) {
            return ResponseEntity.ok(service.listPaginated(from,count));
        }
        return ResponseEntity.ok(service.listWidgets());
    }

    // TODO using param for pagination
//    @GetMapping("/widget")
//    public List<Widget> getWidgets(@RequestParam(required = false) Integer from, @RequestParam(required = false, defaultValue = "500") int count) {
//        // Require 5 tokens, making listing 5x more expensive than regular calls.
//        // Potentially subjective interpretation of the spec
//        RateLimitResponse rateLimitResponse = rateLimiter.request(LIST_WIDGET_MODIFIER);
//        if(!rateLimitResponse.success()) {
//            return new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
//        }
//        return service.listWidgets();
//    }

    @PostMapping("/widget")
    public ResponseEntity<String> insertWidget(@RequestBody Widget widget) {
        // TODO Any restrictions on widgets? Things like negative height / width? Weird time stamps?
        RateLimitResponse rateLimitResponse = rateLimiter.request(1);
        if(!rateLimitResponse.success()) {
            return new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
        }
        String id = service.insertWidget(widget);
        return ResponseEntity.status(HttpStatus.OK)
                .headers(parseRateLimitHeaders(rateLimitResponse))
                .body(id);
    }

    @DeleteMapping("/widget/{id}")
    public ResponseEntity deleteWidget(@PathVariable String id) {
        RateLimitResponse rateLimitResponse = rateLimiter.request(1);
        if(!rateLimitResponse.success()) {
            return new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
        }
        service.deleteWidget(id);
        return ResponseEntity.status(HttpStatus.OK)
            .headers(parseRateLimitHeaders(rateLimitResponse))
            .build();
    }

    @PutMapping("/widget/{id}")
    public void updateWidget(@PathVariable String id, @RequestBody Widget widget) {
        service.updateWidget(id,widget);
    }

    @GetMapping("/widget/{id}")
    public Widget getWidget(@PathVariable String id) {

        return service.getWidget(id);
    }

    private HttpHeaders parseRateLimitHeaders(RateLimitResponse rateLimitReponse) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("RateLimit-Remaining",List.of(Long.toString(rateLimitReponse.rateLimit())));
        httpHeaders.put("RateLimit-Reset",List.of(Long.toString(rateLimitReponse.untilNextReset().toSeconds())));
        return httpHeaders;
    }

}
