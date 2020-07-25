package io.floodplain.miroassignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record Widget(
        @JsonProperty("id")String id,
        @JsonProperty("x")int x,
        @JsonProperty("y")int y,
        @JsonProperty("z")int z,
        @JsonProperty("width")int width,
        @JsonProperty("height")int height,
        @JsonProperty("lastModified")Instant lastModified) {

    public static Widget create(int x, int y, int z, int width, int height) {
        return new Widget(null, x, y, z, width, height, Instant.ofEpochSecond(0));
    }

    /**
     * Copy widget, but with a new id
     * @param id
     * @return a new widget
     */
    public Widget withId(String id) {
        return new Widget(id,
                this.x(),
                this.y(),
                this.z(),
                this.width(),
                this.height(),
                this.lastModified()
        );
    }

    /**
     * Copy widget, but with a new z-index
     * @param z the new z-index
     * @return a new widget
     */
    public Widget withZIndex(int z) {
        return new Widget(this.id(),
                this.x(),
                this.y(),
                z,
                this.width(),
                this.height(),
                this.lastModified()
        );
    }

}

