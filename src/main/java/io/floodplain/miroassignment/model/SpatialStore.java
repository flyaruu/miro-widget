package io.floodplain.miroassignment.model;

import java.util.List;

public interface SpatialStore {
    List<Widget> findWidgetsWithin(int x, int y, int w, int h);
    void addWidget(Widget widget);
    void removeWidget(Widget widget);
}
