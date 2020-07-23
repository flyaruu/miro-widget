package io.floodplain.miroassignment.model;

import java.util.List;

public interface WidgetService {
    /**
     * Inserts widgets with z coordinate at the level 0.
     * @param w Widget to insert
     * @return Created key
     */
    String insertWidget(Widget w);

    /**
     * Inserts widgets with z coordinate at the required level.
     * @param w Widget to insert
     * @param index
     * @return Created key
     */
    String insertWidget(Widget w, int index);

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
     * @param id id to replace
     * @param widget new widget
     * @return true if found, false otherwise
     */
    boolean updateWidget(String id, Widget widget);

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
     * @param count number of
     * @return
     */
    List<Widget> listPaginated(int from, int count);
}
