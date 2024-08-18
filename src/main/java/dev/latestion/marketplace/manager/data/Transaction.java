package dev.latestion.marketplace.manager.data;

import java.time.LocalDateTime;

public record Transaction(int quantity, long price, LocalDateTime time, String itemName, String uuid) {

}
