package ru.syntez.processors.compose.processor.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * ComposeDocument model for outputDocumentListQueue
 *
 * @author Skyhunter
 * @date 10.02.2021
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ComposeDocument {
    private int composeId;
    private String composeLabel;
    private List<OrderDocument> orderDocuments;
    private List<InvoiceDocument> invoiceDocuments;
}
