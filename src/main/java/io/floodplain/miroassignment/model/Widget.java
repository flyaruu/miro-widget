package io.floodplain.miroassignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record Widget(
        @JsonProperty("id") String id,
        @JsonProperty("x") int x,
        @JsonProperty("y") int y,
        @JsonProperty("z") Integer z, // nullable?
        @JsonProperty("width") int width,
        @JsonProperty("height") int height,
        @JsonProperty("lastModified") Instant lastModified) {

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

    public static Widget create(int x, int y, int width, int height) {
        return new Widget(null,x,y,null,width,height, Instant.ofEpochSecond(0));
    }

    public static Widget create(int x, int y, int width, int height, Instant lastModified) {
        return new Widget(null,x,y,null,width,height, lastModified);
    }

}

