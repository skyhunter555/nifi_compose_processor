package ru.syntez.processors.compose.processor.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OrderDocument {
    private int orderId;
    private String orderNumber;
}
