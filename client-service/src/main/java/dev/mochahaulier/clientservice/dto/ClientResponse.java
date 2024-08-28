package dev.mochahaulier.clientservice.dto;

import java.util.List;

public record ClientResponse(List<ClientItem> clients) {
    public static record ClientItem(
        Long id, String firstName, String lastName, String email, String phone) {
    }
}
