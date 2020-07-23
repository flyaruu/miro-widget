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
        return insertWidget(w,0);
    }

    @Override
    public String insertWidget(Widget w, int index) {
        String id = UUID.randomUUID().toString();
        storeWidget(id,w,index);
        return id;
    }

    private synchronized void storeWidget(String id, Widget w, int index) {
        widgets.put(id,w);
        insertZIndex(w,index);
    }

    private synchronized boolean deleteWidget(String id, Widget w) {
        boolean found = widgets.containsKey(id);
        widgets.remove(id);
        zWidgetIndex.remove(w);
        return found;
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
    private void insertZIndex(Widget w, int index) {
        zWidgetIndex.add(index,w);
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
    public boolean deleteWidget(String id) {
        Widget w = getWidget(id);
        return deleteWidget(id,w);
    }

    @Override
    public boolean updateWidget(String id, Widget widget) {
        Widget previousWidget = getWidget(id);
        if(previousWidget==null) {
            return false;
        }
        int previousIndex = zWidgetIndex.indexOf(previousWidget);
        deleteWidget(id,previousWidget);
        storeWidget(id,widget, previousIndex);
        return true;
    }

    @Override
    public List<Widget> listWidgets() {
        return Collections.unmodifiableList(this.zWidgetIndex);
    }

    @Override
    public List<Widget> listPaginated(int from, int count) {
        List<Widget> widgets = listWidgets();
        return widgets.subList(from, Math.min(from+count,widgets.size()));
    }
}
