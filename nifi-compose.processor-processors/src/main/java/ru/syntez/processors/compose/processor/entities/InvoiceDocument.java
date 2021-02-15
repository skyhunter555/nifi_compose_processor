package ru.syntez.processors.compose.processor.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InvoiceDocument {
    private int invoiceId;
    private String invoiceNumber;
}
