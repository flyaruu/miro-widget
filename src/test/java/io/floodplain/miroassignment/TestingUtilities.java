package io.floodplain.miroassignment;

import io.floodplain.miroassignment.model.Widget;

import java.time.Instant;
import java.util.Random;

public class TestingUtilities {
    private static final Random random = new Random();


    static Widget createRandomWidget() {
        return new Widget(null,
                random.nextInt(10000) - 5000,
                random.nextInt(10000) - 5000,
                random.nextInt(1000),
                random.nextInt(1000),
                random.nextInt(1000),
                Instant.ofEpochSecond(random.nextInt(10000000))
        );
    }

    static Widget createTestWidget(int number) {
        return new Widget(null, number, number, number, number, number, Instant.ofEpochSecond(number));
    }
}
