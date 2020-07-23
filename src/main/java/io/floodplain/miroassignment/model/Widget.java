package io.floodplain.miroassignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record Widget(
//        @JsonProperty("id") String id,
        @JsonProperty("x") int x,
        @JsonProperty("y") int y,
        @JsonProperty("z") int z,
        @JsonProperty("width") int width,
        @JsonProperty("height") int height,
        @JsonProperty("lastModified") Instant lastModified) {

    // TODO create prettier constructor?

//    public Widget withId(String id) {
//        return new Widget(id,x,y,z,width,height,lastModified);
//    }
}

