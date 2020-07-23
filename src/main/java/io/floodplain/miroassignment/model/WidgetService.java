package io.floodplain.miroassignment.model;

import java.util.List;

public interface WidgetService {
    String insertWidget(Widget w);
    Widget getWidget(String id);
    void deleteWidget(String id);
    void updateWidget(String id, Widget widget);
    List<Widget> listWidgets();
    void clear();

    List<Widget> listPaginated(int from, int count);
}
