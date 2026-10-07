package com.example.demospringpedidos.config;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClockConfigTest {
    @Test
    void clockBeanUsesUtc() {
        Clock clock = new ClockConfig().clock();

        assertEquals(ZoneOffset.UTC, clock.getZone());
    }
}
