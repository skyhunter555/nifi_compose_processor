package ru.syntez.processors.compose.processor;

import javax.annotation.Generated;
import ru.syntez.processors.compose.processor.entities.InvoiceDocument;
import ru.syntez.processors.compose.processor.entities.OrderDocument;
import ru.syntez.processors.compose.processor.entities.OutputDocumentExt;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-15T12:45:32+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_241 (Oracle Corporation)"
)
public class MapStructConverterImpl implements MapStructConverter {

    @Override
    public OrderDocument convertOrder(OutputDocumentExt outputDocumentExt) {
        if ( outputDocumentExt == null ) {
            return null;
        }

        OrderDocument orderDocument = new OrderDocument();

        orderDocument.setOrderId( outputDocumentExt.getDocumentId() );
        if ( outputDocumentExt.getDocumentNumber() != null ) {
            orderDocument.setOrderNumber( String.valueOf( outputDocumentExt.getDocumentNumber() ) );
        }

        return orderDocument;
    }

    @Override
    public InvoiceDocument convertInvoice(OutputDocumentExt outputDocumentExt) {
        if ( outputDocumentExt == null ) {
            return null;
        }

        InvoiceDocument invoiceDocument = new InvoiceDocument();

        invoiceDocument.setInvoiceId( outputDocumentExt.getDocumentId() );
        if ( outputDocumentExt.getDocumentNumber() != null ) {
            invoiceDocument.setInvoiceNumber( String.valueOf( outputDocumentExt.getDocumentNumber() ) );
        }

        return invoiceDocument;
    }
}
