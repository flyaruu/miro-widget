package io.floodplain.miroassignment;

import io.floodplain.miroassignment.model.WidgetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MiroassignmentApplicationTests {

    @Autowired
    private WidgetService widgetService;

    @Test
    void contextLoads() {
    }


}
