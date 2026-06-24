package com.example.backend.sscc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SsccStorageServiceTest {

    private SsccStorageService service;

    @BeforeEach
    void setUp() {
        service = new SsccStorageService();
    }

    @Test
    @DisplayName("getAll function returns ordered items")
    void orderedItems() {
        service.add("340123450000000017");
        service.add("000000000000000000");
        service.add("376130321109103420");

        assertEquals(3, service.getAll().size());
        assertEquals("340123450000000017", service.getAll().get(0));
        assertEquals("000000000000000000", service.getAll().get(1));
        assertEquals("376130321109103420", service.getAll().get(2));
    }

    @Test
    @DisplayName("getAll returns an unmodifiable copy")
    void unmodifiableItems() {
        service.add("340123450000000017");
        assertThrows(UnsupportedOperationException.class, () -> service.getAll().add("hack"));
    }

    @Test
    @DisplayName("Adding a new SSCC returns true and increases count")
    void addNew() {
        assertTrue(service.add("340123450000000017"));
        assertEquals(1, service.count());
        assertEquals("340123450000000017", service.getAll().get(0));
    }

    @Test
    @DisplayName("Adding a duplicate SSCC returns false and does not increase count")
    void addDuplicate() {
        service.add("340123450000000017");
        assertFalse(service.add("340123450000000017"));
        assertEquals(1, service.count());
    }

}
