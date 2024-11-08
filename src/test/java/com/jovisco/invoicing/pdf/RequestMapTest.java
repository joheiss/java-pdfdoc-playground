package com.jovisco.invoicing.pdf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequestMapTest {

    RequestMap requestMap;

    @BeforeEach
    void setUp() {
        Map<String, List<Object>> requests = new HashMap<>();
        requests.put("toBeFoundSingle", List.of("found"));
        requests.put("toBeFoundList", List.of("first item", "second item"));
        requests.put("emptyList", List.of());

        requestMap = new RequestMap(requests);
    }

    @Test
    @DisplayName("should return a string for an existing entry")
    void get_found() {
        assertEquals("found", requestMap.get("toBeFoundSingle"));
    }

    @Test
    @DisplayName("should return an empty string if entry not found")
    void get_notfound() {
        assertEquals("", requestMap.get("notToBeFoundSingle"));
    }

    @Test
    @DisplayName("should return all list items if found")
    void getList_found() {
        assertEquals(2, requestMap.getList("toBeFoundList").size());
    }

    @Test
    @DisplayName("should return an empty list if empty list is found")
    void getList_empty() {
        assertEquals(0, requestMap.getList("emptyList").size());
    }

    @Test
    @DisplayName("should return an empty list if entry is not found")
    void getList_notfound() {
        assertEquals(0, requestMap.getList("notToBeFoundList").size());
    }

    @Test
    @DisplayName("should return a list of items if found")
    void getItems_found() {
        Map<String, List<Object>> itemRequest = new HashMap<>();
        itemRequest.put(RequestMap.ITEMS, List.of(
                Map.of(
                        RequestMap.ITEM_ID, "1",
                        RequestMap.ITEM_QTY, "123,5",
                        RequestMap.ITEM_DESC, "Projektstunden im Projekt \"Rettet die Welt\"",
                        RequestMap.ITEM_UNIT_NET_AMNT, "123,45 €",
                        RequestMap.ITEM_TOTAL_NET_AMNT, "12.345,67 €"
                ),
                Map.of(
                        RequestMap.ITEM_ID, "2",
                        RequestMap.ITEM_QTY, "23",
                        RequestMap.ITEM_DESC, "Reisestunden im Projekt \"Rettet die Welt\"",
                        RequestMap.ITEM_UNIT_NET_AMNT, "66,18 €",
                        RequestMap.ITEM_TOTAL_NET_AMNT, "1.234,56 €"
                )));
        RequestMap itemMap = new RequestMap(itemRequest);
        assertEquals(2, itemMap.getItems().size());
    }

    @Test
    @DisplayName("should return a an empty list if no items found")
    void getItems_notFound() {
        assertEquals(0, requestMap.getItems().size());
    }
}