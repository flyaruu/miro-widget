package io.floodplain.miroassignment.impl;

import io.floodplain.miroassignment.model.Widget;
import io.floodplain.miroassignment.model.WidgetService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WidgetServiceImpl implements WidgetService {

    private final Map<String,Widget> widgets = new HashMap<>();
    private final LinkedList<Widget> zWidgetIndex = new LinkedList<>();

    // (thought) debating whether to create a separate type for this request (basically a widget without id)
    // I can't extend records though, so I'll end up copy/pasting most properties. Also not nice.
    @Override
    public String insertWidget(Widget w) {
        String id = UUID.randomUUID().toString();
        storeWidget(id,w);
        return id;
    }

    private synchronized void storeWidget(String id, Widget w) {
        widgets.put(id,w);
        insertZIndex(w);
    }

    private synchronized void deleteWidget(String id, Widget w) {
        widgets.remove(id);
//        zWidgetIndex.remove(w.z());
    }

    private synchronized void clearAllWidgets() {
        zWidgetIndex.clear();
        widgets.clear();
    }

    /**
     * Will insert the widget into the z-index linked list, might end up moving up others if they are in the way.
     * (thought) Possibly expensive. Could create bigger gaps so shifting the list is less frequent.
     *
     * @param w
     */
    private void insertZIndex(Widget w) {
        zWidgetIndex.add(0,w);
    }

    @Override
    public Widget getWidget(String id) {
        return widgets.get(id);
    }

    @Override
    public void clear() {
        clearAllWidgets();
    }

    @Override
    public void deleteWidget(String id) {
        Widget w = getWidget(id);
        deleteWidget(id,w);

    }

    @Override
    public void updateWidget(String id, Widget widget) {
        deleteWidget(id,widget);
        storeWidget(id,widget);
    }

    @Override
    public List<Widget> listWidgets() {
        return Collections.unmodifiableList(this.zWidgetIndex);
    }

    @Override
    public List<Widget> listPaginated(int from, int count) {
        return null;
    }


}
