package io.floodplain.miroassignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record Widget(
//        @JsonProperty("id") String id,
        @JsonProperty("x") int x,
        @JsonProperty("y") int y,
        @JsonProperty("width") int width,
        @JsonProperty("height") int height,
        @JsonProperty("lastModified") Instant lastModified) {

}

