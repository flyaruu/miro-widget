package io.floodplain.miroassignment;

import io.floodplain.miroassignment.model.Widget;
import io.floodplain.miroassignment.model.WidgetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestWidgetService {
    @Autowired
    private WidgetService widgetService;

    @Test
    public void testGotAnId() {

        Widget request = new Widget(1,2,3,4, Instant.ofEpochSecond(10000));
        String id = widgetService.insertWidget(request);
        Assertions.assertNotNull(id);
    }

    @Test
    void testAddAndDeleteWidgets() {
        widgetService.clear();
        // Assert there are no widgets
        assertEquals(0,widgetService.listWidgets().size());
        // Should return null
        assertNull(widgetService.getWidget("myid"));
        // Insert one widget, assert that...
        Widget newWidget = new Widget(1,2,3,4,Instant.ofEpochSecond(10000));
        String insertedId = widgetService.insertWidget(newWidget);
        // We've gotten an id:
        assertNotNull(insertedId);
        // There is actually somethin in there:
        assertEquals(1,widgetService.listWidgets().size());
        // Retrieve, see if it is unchanged
        Widget retrieved = widgetService.getWidget(insertedId);
        assertEquals(newWidget,retrieved);
        // Test update. Update to new widget, retrieve and see if it stuck:
        Widget otherWidget = new Widget(6,7,8,9,Instant.ofEpochSecond(10000));
        widgetService.updateWidget(insertedId,otherWidget);
        // And still should be only one
        assertEquals(1,widgetService.listWidgets().size());
        // Test delete and see that is empty now
        widgetService.deleteWidget(insertedId);
        assertNull(widgetService.getWidget(insertedId));
        assertEquals(0,widgetService.listWidgets().size());
    }

    @Test
    public void testZIndex() {
        Widget firstWidget = new Widget(1,2,3,4,Instant.ofEpochSecond(10000));
        String firstWidgetId = widgetService.insertWidget(firstWidget);
        Widget secondWidget = new Widget(5,6,7,8,Instant.ofEpochSecond(20000));
        // Inserting without index, so using index 0:
        String secondWidgetId = widgetService.insertWidget(secondWidget);
        // Verify ordering:
        List<Widget> list = widgetService.listWidgets();
        assertEquals(secondWidget,list.get(0));
        assertEquals(firstWidget,list.get(1));
        // Now add at the end:
        Widget thirdWidget = new Widget(9,10,11,12,Instant.ofEpochSecond(30000));
        String thirdWidgetId = widgetService.insertWidget(thirdWidget,2);
        assertEquals(thirdWidget,list.get(2));
    }


}
