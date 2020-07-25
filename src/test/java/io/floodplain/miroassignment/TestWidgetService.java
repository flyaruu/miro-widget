package io.floodplain.miroassignment;

import io.floodplain.miroassignment.model.Widget;
import io.floodplain.miroassignment.model.WidgetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestWidgetService {
    @Autowired
    private WidgetService widgetService;

    @BeforeEach
    public void clean() {
        widgetService.clear();
    }

    @Test
    public void testGotAnId() {

        Widget request = TestingUtilities.createRandomWidget();
        Widget inserted = widgetService.insertWidget(request);
        Assertions.assertNotNull(inserted.id());
    }

    @Test
    void testAddAndDeleteWidgets() {
        // Assert there are no widgets
        assertEquals(0, widgetService.listWidgets().size());
        // Should return null
        assertNull(widgetService.getWidget("myid"));
        // Insert one widget, assert that...
        Widget newWidget = TestingUtilities.createRandomWidget();
        Widget insertedWidget = widgetService.insertWidget(newWidget);
        // We've gotten an id:
        assertNotNull(insertedWidget.id());
        // There is actually something in the widget list:
        assertEquals(1, widgetService.listWidgets().size());
        // Retrieve, see if it is unchanged
        Widget retrieved = widgetService.getWidget(insertedWidget.id());
        assertEquals(insertedWidget, retrieved);
        // Test update. Update to new widget, retrieve and see if it stuck:
        Widget otherWidget = TestingUtilities.createRandomWidget();
        widgetService.updateWidget(otherWidget.withId(insertedWidget.id()));
        // And still should be only one
        assertEquals(1, widgetService.listWidgets().size());
        // Test delete and see that is empty now
        widgetService.deleteWidget(insertedWidget.id());
        assertNull(widgetService.getWidget(insertedWidget.id()));
        assertEquals(0, widgetService.listWidgets().size());
    }

    @Test
    public void testZIndex() {
        Widget firstWidget = widgetService.insertWidget(TestingUtilities.createTestWidget(1).withZIndex(10));
        Widget secondWidget = widgetService.insertWidget(TestingUtilities.createTestWidget(2).withZIndex(11));
        // Verify ordering:
        List<Widget> list = widgetService.listWidgets();
        assertEquals(firstWidget, list.get(0));
        assertEquals(secondWidget, list.get(1));
        // assert unchanged z:
        assertEquals(10, firstWidget.z());
        assertEquals(11, secondWidget.z());

        Widget thirdWidget = widgetService.insertWidget(TestingUtilities.createTestWidget(3).withZIndex(10));
        // add another at z=10. First should be at eleven now, second at twelve
        assertEquals(10, thirdWidget.z());
        assertEquals(11, widgetService.getWidget(firstWidget.id()).z());
        assertEquals(12, widgetService.getWidget(secondWidget.id()).z());
    }

    @Test
    public void testPagination() {
        // add 501 items
        IntStream.range(0, 501).forEach(i -> {
            widgetService.insertWidget(TestingUtilities.createRandomWidget());
        });
        // Sanitycheck, are there actually 501 items?
        assertEquals(501, widgetService.listWidgets().size());


        List<Widget> paginatedFromZero = widgetService.listPaginated(0, Optional.of(3));
        List<Widget> paginatedFromOne = widgetService.listPaginated(1, Optional.of(3));
        assertEquals(3, paginatedFromZero.size());
        assertEquals(3, paginatedFromOne.size());
        // Check if they are actually shifted by one
        assertEquals(paginatedFromOne.get(0), paginatedFromZero.get(1));

        // Test default page size:
        assertEquals(10, widgetService.listPaginated(0, Optional.empty()).size());

        // test that default max is observed:
        assertEquals(500, widgetService.listPaginated(0, Optional.of(Integer.MAX_VALUE)).size());

    }

}

