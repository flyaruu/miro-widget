package io.floodplain.miroassignment.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WidgetService {

    /**
     * Max page size
     */
    public static final int MAX_PAGINATION_COUNT = 500;
    /**
     * Default page size
     */
    public static final int DEFAULT_PAGINATION_COUNT = 10;

    /**
     * Inserts widgets with z coordinate at the level 0.
     * @param w Widget to insert
     * @return Created key
     */
    Widget insertWidget(Widget w);

    /**
     * Find a widget and return it
     * @param id the id
     * @return widget, or null if not found
     */
    Widget getWidget(String id);

    /**
     * Delete a widget
     * @param id
     * @return true if found, false otherwise
     */
    boolean deleteWidget(String id);

    /**
     * Replace a widget with the supplied widget
     * @param widget new widget. The widget should have an id pointing to an existing widget
     * @return the new updated widget.
     */
    Widget updateWidget(Widget widget);

    /**
     * Remove all widgets
     */
    void clear();

    /**
     * @return List widgets in z-order
     */
    List<Widget> listWidgets();

    /**
     * List widgets within the
     * @param from start index
     * @param count optionally number of elements. If empty, default number of widgets will be returned (maxed by MAX_PAGINATION_COUNT)
     * @return the selected list
     */
    List<Widget> listPaginated(int from, Optional<Integer> count);

    List<Widget> listFiltered(int x, int y, int width, int height);

}
