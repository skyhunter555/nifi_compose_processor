package ru.syntez.processors.compose.processor.entities;

import lombok.Data;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * OrderDocumentExt model from outputDocumentQueue
 *
 * @author Skyhunter
 * @date 10.02.2021
 */
@XmlRootElement(name = "OutputDocumentExt")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class OutputDocumentExt {
    private int documentId;
    private String documentType;
    private Integer documentNumber;
}
