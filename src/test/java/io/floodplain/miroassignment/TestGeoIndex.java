package io.floodplain.miroassignment;

import io.floodplain.miroassignment.model.Widget;
import io.floodplain.miroassignment.model.WidgetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestGeoIndex {
    @Autowired
    private WidgetService widgetService;

    @BeforeEach
    public void initialize() {
        widgetService.clear();
    }

    @Test
    public void testEasyWithGaps() {
        widgetService.insertWidget(Widget.create(10,10,10,10,10));
        widgetService.insertWidget(Widget.create(30,10,10,10,10));
        widgetService.insertWidget(Widget.create(50,10,10,10,10));

        // Test with margins
        Assertions.assertEquals(3,widgetService.listFiltered(0,0,70,20).size());
        // Test tight box
        Assertions.assertEquals(3,widgetService.listFiltered(10,10,60,20).size());
        // Test cut off
        Assertions.assertEquals(2,widgetService.listFiltered(10,10,55,20).size());

        widgetService.insertWidget(Widget.create(15,10,10,10,10));
        // test overlap
        Assertions.assertEquals(4,widgetService.listFiltered(10,10,60,20).size());

    }

}
