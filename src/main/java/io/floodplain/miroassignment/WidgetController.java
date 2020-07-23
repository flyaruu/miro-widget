package io.floodplain.miroassignment;

import io.floodplain.miroassignment.model.RateLimiter;
import io.floodplain.miroassignment.model.Widget;
import io.floodplain.miroassignment.model.WidgetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("widgetController")
public class WidgetController {

    private final WidgetService service;
    private final RateLimiter rateLimiter;
    WidgetController(WidgetService service, RateLimiter rateLimiter) {
        this.service = service;
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/widget")
    public List<Widget> getWidgets(@RequestParam(required = false) Integer from, @RequestParam(required = false, defaultValue = "500") int count) {
        // From is nullable, if present paginate, otherwise don't
//        rateLimiter.request(5).
        if(from!=null) {
            return service.listPaginated(from,count);
        }
        return service.listWidgets();
    }

    @PostMapping("/widget")
    public String addWidget(@RequestBody Widget widget) {
        return service.insertWidget(widget);
    }

    @DeleteMapping("/widget/{id}")
    public void deleteWidget(@PathVariable String id) {
        service.deleteWidget(id);
    }

    @PutMapping("/widget/{id}")
    public void updateWidget(@PathVariable String id, @RequestBody Widget widget) {
        service.updateWidget(id,widget);
    }

    @GetMapping("/widget/{id}")
    public Widget getWidget(@PathVariable String id) {
        return service.getWidget(id);
    }

}
