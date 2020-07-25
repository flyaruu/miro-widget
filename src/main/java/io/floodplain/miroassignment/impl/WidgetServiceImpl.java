package io.floodplain.miroassignment.impl;

import com.github.davidmoten.rtree2.Entry;
import com.github.davidmoten.rtree2.RTree;
import com.github.davidmoten.rtree2.geometry.Geometries;
import com.github.davidmoten.rtree2.geometry.Rectangle;
import io.floodplain.miroassignment.model.Widget;
import io.floodplain.miroassignment.model.WidgetService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class WidgetServiceImpl implements WidgetService {

    private final Map<String, Widget> widgets = new HashMap<>();
    private final TreeSet<Widget> zWidgetIndex = new TreeSet<>(Comparator.comparing(Widget::z));
    private final AtomicReference<RTree<Widget, Rectangle>> geoIndex = new AtomicReference<>(RTree.create());

    @Override
    public Widget insertWidget(Widget w) {
        String id = UUID.randomUUID().toString();
        Widget withId = w.withId(id);
        storeWidget(withId);
        return withId;
    }

    private synchronized void storeWidget(Widget w) {
        pushWidgetToSet(w);
        // TODO geoIndex uses doubles. Tested for rounding errors, no issues found
        geoIndex.updateAndGet(rtree -> rtree.add(w, Geometries.rectangle(w.x(), w.y(), w.x() + w.width(), w.y() + w.height())));
    }

    private synchronized boolean deleteWidget(Widget w) {
        boolean found = widgets.containsKey(w.id());
        widgets.remove(w.id());
        zWidgetIndex.remove(w);
        geoIndex.updateAndGet(rtree -> rtree.delete(w, Geometries.rectangle(w.x(), w.y(), w.x() + w.width(), w.y() + w.height())));
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
     * Recusive, but tail recursive so I expect no performance issues for large sets.
     * @param s the widget to add
     */

    private void pushWidgetToSet(Widget s) {

        boolean inserted = zWidgetIndex.add(s);
        this.widgets.put(s.id(), s);
        if (inserted) {
            return;
        }
        Widget removed = zWidgetIndex.tailSet(s, true).pollFirst();
        Assert.notNull(removed,"should not happen.");

        boolean success = zWidgetIndex.add(s); // should always work, as we just removed the offending item
        Assert.isTrue(success,"bug. should not happen");
        // now continue to find a spot for the item that was in the way, starting at z+1
        pushWidgetToSet(removed.withZIndex(removed.z() + 1));
    }

    @Override
    public Widget getWidget(String id) {
        Assert.notNull(id, "get should have a non-null id");
        return widgets.get(id);
    }

    @Override
    public void clear() {
        clearAllWidgets();
    }

    @Override
    public boolean deleteWidget(String id) {
        Assert.notNull(id, "deleteWidget should have a non-null id");
        Widget w = getWidget(id);
        Assert.notNull(id, "deleteWidget can not delete a non-existing id");
        return deleteWidget(w);
    }

    @Override
    public Widget updateWidget(Widget widget) {
        Assert.notNull(widget, "updateWidget can not update a null widget");
        Widget previousWidget = getWidget(widget.id());
        Assert.notNull(previousWidget, "updateWidget can not update a non-existing id");
        deleteWidget(previousWidget);
        storeWidget(widget);
        return widget;
    }

    @Override
    public List<Widget> listWidgets() {
        return List.copyOf(zWidgetIndex);
    }

    @Override
    public List<Widget> listPaginated(int from, Optional<Integer> count) {

        // No more than MAX_PAGINATION_COUNT, DEFAULT_PAGINATION_COUNT if unspecified
        // Chose to cap at MAX_PAGINATION_COUNT, instead of error
        int effectiveCount = count
                .map(cnt -> Math.min(cnt, MAX_PAGINATION_COUNT))
                .orElse(DEFAULT_PAGINATION_COUNT);

        Collection<Widget> widgets = listWidgets();
        return widgets.stream()
                .skip(from)
                .limit(effectiveCount)
                .collect(Collectors.toList());
    }

    @Override
    public List<Widget> listFiltered(int x, int y, int width, int height) {
        return StreamSupport
                .stream(geoIndex.get().search(Geometries.rectangle(x, y, x + width, y + height)).spliterator(), false)
                .map(Entry::value)
                // need to post-filter, as the rtree will also include partial matches
                .filter(fallsWithinBounds(x, y, width, height))
                .collect(Collectors.toList());
    }

    /**
     * The rtree implementation tests for rectangles that touch, not that completely fall within the rectangle
     * @param x x of query box
     * @param y y of query box
     * @param width width of query box
     * @param height height of query box
     * @return a function that checks a widget
     */
    private Predicate<Widget> fallsWithinBounds(int x, int y, int width, int height) {
        return widget -> widget.x() >= x &&
                widget.x() + widget.width() <= width &&
                widget.y() >= y &&
                widget.y() + widget.height() <= height;
    }
}
