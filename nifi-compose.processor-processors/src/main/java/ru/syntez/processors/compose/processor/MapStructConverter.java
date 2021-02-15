package ru.syntez.processors.compose.processor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ru.syntez.processors.compose.processor.entities.InvoiceDocument;
import ru.syntez.processors.compose.processor.entities.OrderDocument;
import ru.syntez.processors.compose.processor.entities.OutputDocumentExt;

@Mapper
public interface MapStructConverter {

     MapStructConverter MAPPER = Mappers.getMapper(MapStructConverter.class);

     @Mappings({
             @Mapping(source="documentId",     target="orderId"),
             @Mapping(source="documentNumber", target="orderNumber")
     })
     OrderDocument convertOrder(OutputDocumentExt outputDocumentExt);

     @Mappings({
             @Mapping(source="documentId",     target="invoiceId"),
             @Mapping(source="documentNumber", target="invoiceNumber")
     })
     InvoiceDocument convertInvoice(OutputDocumentExt outputDocumentExt);

}
