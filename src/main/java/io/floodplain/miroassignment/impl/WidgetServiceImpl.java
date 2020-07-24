package io.floodplain.miroassignment.impl;

import com.github.davidmoten.rtree2.Entry;
import com.github.davidmoten.rtree2.RTree;
import com.github.davidmoten.rtree2.geometry.Geometries;
import com.github.davidmoten.rtree2.geometry.Rectangle;
import io.floodplain.miroassignment.model.Widget;
import io.floodplain.miroassignment.model.WidgetService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class WidgetServiceImpl implements WidgetService {

    private final Map<String,Widget> widgets = new HashMap<>();
    private final LinkedList<Widget> zWidgetIndex = new LinkedList<>();

    private final AtomicReference<RTree<Widget, Rectangle>> geoIndex = new AtomicReference<>(RTree.create());

    // (thought) debating whether to create a separate type for this request (basically a widget without id)
    // I can't extend records though, so I'll end up copy/pasting most properties. Also not nice.
    @Override
    public Widget insertWidget(Widget w) {
        return insertWidget(w,0);
    }

    @Override
    public Widget insertWidget(Widget w, int index) {
        String id = UUID.randomUUID().toString();
        Widget withId = w.withId(id);
        storeWidget(id,withId,index);
        return withId;
    }

    private synchronized void storeWidget(String id, Widget w, int index) {
        widgets.put(id,w);
        // TODO geoIndex uses doubles. Test for rounding errors
        insertZIndex(w,index);
        geoIndex.updateAndGet(rtree->rtree.add(w, Geometries.rectangle(w.x(),w.y(),w.x()+w.width(),w.y() + w.height())));
       ;
    }

    private synchronized boolean deleteWidget(String id, Widget w) {
        boolean found = widgets.containsKey(id);
        widgets.remove(id);
        zWidgetIndex.remove(w);
        geoIndex.updateAndGet(rtree->rtree.delete(w, Geometries.rectangle(w.x(),w.y(),w.x()+w.width(),w.y()+w.height())));
        return found;
//        zWidgetIndex.remove(w.z());
    }

    private synchronized void clearAllWidgets() {
        zWidgetIndex.clear();
        widgets.clear();
        geoIndex.set(RTree.create());
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
    public List<Widget> listPaginated(int from, Optional<Integer> count) {

        // No more than MAX_PAGINATION_COUNT, DEFAULT_PAGINATION_COUNT if unspecified
        int effectiveCount = count
                .map(cnt->Math.min(cnt, MAX_PAGINATION_COUNT))
                .orElse(DEFAULT_PAGINATION_COUNT);

        List<Widget> widgets = listWidgets();
        return widgets.stream()
                .skip(from)
                .limit(effectiveCount)
                .collect(Collectors.toList());
    }

    @Override
    public List<Widget> listFiltered(int x, int y, int width, int height) {
        return StreamSupport
                .stream(geoIndex.get().search(Geometries.rectangle(x,y,x+width,y+height)).spliterator(), false)
                .map(entry->entry.value())
                // need to post-filter, as the rtree will also include partial matches
                .filter(fallsWithinBounds(x,y,width,height))
                .collect(Collectors.toList());
    }

    private Predicate<Widget> fallsWithinBounds(int x, int y, int width, int height) {
        return widget-> widget.x() >= x &&
                widget.x() + widget.width() <= width &&
                widget.y() >= y &&
                widget.y() + widget.height() <= height;
    }
}
