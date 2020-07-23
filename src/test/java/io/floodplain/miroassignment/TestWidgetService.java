package io.floodplain.miroassignment;

import io.floodplain.miroassignment.model.Widget;
import io.floodplain.miroassignment.model.WidgetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TestWidgetService {
    @Autowired
    private WidgetService widgetService;

    @Test
    public void testAddingIdShouldBeNull() {

        Widget request = new Widget(1,2,3,4,5, Instant.ofEpochSecond(10000));
        String id = widgetService.insertWidget(request);
        Assertions.assertNotNull(id);
    }

    @Test
    void testAddAndDeleteWidgets() {
        // Assert there are no widgets
        assertEquals(0,widgetService.listWidgets().size());
        // Should return null
        Assertions.assertNull(widgetService.getWidget("myid"));
        // Insert one widget, assert that...
        Widget newWidget = new Widget(1,2,3,4,5,Instant.ofEpochSecond(10000));
        String insertedId = widgetService.insertWidget(newWidget);
        // We've gotten an id:
        assertNotNull(insertedId);
        // There is actually somethin in there:
        assertEquals(1,widgetService.listWidgets().size());
        // Retrieve, see if it is unchanged
        Widget retrieved = widgetService.getWidget(insertedId);
        assertEquals(newWidget,retrieved);
        // Test update. Update to new widget, retrieve and see if it stuck:
        Widget otherWidget = new Widget(6,7,8,9,10,Instant.ofEpochSecond(10000));
        widgetService.updateWidget(insertedId,otherWidget);
        Widget updated = widgetService.getWidget(insertedId);
        assertEquals(otherWidget,updated);
        // And still should be only one
        assertEquals(1,widgetService.listWidgets().size());

        // Test delete and see that is empty now
        widgetService.deleteWidget(insertedId);
        Assertions.assertNull(widgetService.getWidget(insertedId));
        assertEquals(0,widgetService.listWidgets().size());
    }

    public void testZIndex() {
        Widget newWidget = new Widget(1,2,3,4,5,Instant.ofEpochSecond(10000));
        String insertedId = widgetService.insertWidget(newWidget);
        // check that the z index is still at 3 (inserting at a certain z should always work)
        assertEquals(3,widgetService.getWidget(insertedId).z());

        Widget anotherWidget = new Widget(6,7,3,8,9,Instant.ofEpochSecond(20000));
        String otherId = widgetService.insertWidget(anotherWidget);
        // inserted another widget, also at z index 3. Original widget should be at 4 now
        assertEquals(3,widgetService.getWidget(otherId).z());
        assertEquals(4,widgetService.getWidget(insertedId).z());


        // inserted
    }


}
