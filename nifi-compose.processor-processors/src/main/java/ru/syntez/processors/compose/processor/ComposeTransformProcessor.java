/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.syntez.processors.compose.processor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.util.StandardValidators;
import ru.syntez.processors.compose.processor.entities.ComposeDocument;
import ru.syntez.processors.compose.processor.entities.InvoiceDocument;
import ru.syntez.processors.compose.processor.entities.OrderDocument;
import ru.syntez.processors.compose.processor.entities.OutputDocumentExt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Tags({"example", "custom", "transform", "demo"})
@CapabilityDescription("Example demo processor to transform document entities to compose")
@SeeAlso({})
@ReadsAttributes({@ReadsAttribute(attribute="", description="")})
@WritesAttributes({@WritesAttribute(attribute="", description="")})
public class ComposeTransformProcessor extends AbstractProcessor {

    private static final String USE_MAP_STRUCT_NAME = "USE_MAP_STRUCT";
    private String COMPOSE_LABEL = "Ведомость документов";

    public static final PropertyDescriptor USE_MAP_STRUCT = new PropertyDescriptor
            .Builder().name(USE_MAP_STRUCT_NAME)
            .displayName("useMapStruct")
            .description("Do we need to use the map struct library for transformation")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("REL_SUCCESS")
            .description("Success relationship")
            .build();

    public static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("REL_FAILURE")
            .description("Failture relationship")
            .build();

    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    private final List<OutputDocumentExt> outputDocumentExtList = new ArrayList<>();
    private ObjectMapper xmlMapper;
    private ObjectMapper jsonMapper = new ObjectMapper();
    private Integer documentCount = 0; //Общее количество обработанных документов
    private Integer composeCount = 0;  //Общее созданных наборов

    private void initXMLMapper() {
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        xmlMapper = new XmlMapper(xmlModule);
        ((XmlMapper) xmlMapper).enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    protected void init(final ProcessorInitializationContext context) {
        final List<PropertyDescriptor> descriptors = new ArrayList<>();
        descriptors.add(USE_MAP_STRUCT);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<Relationship>();
        relationships.add(REL_SUCCESS);
        relationships.add(REL_FAILURE);
        this.relationships = Collections.unmodifiableSet(relationships);
        initXMLMapper();
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context) {

    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        FlowFile inputFlowFile = session.get();
        if ( inputFlowFile == null ) {
            return;
        }

        try (InputStream inputStream = session.read(inputFlowFile)) {
            OutputDocumentExt outputDocumentExt = xmlMapper.readValue(inputStream, OutputDocumentExt.class);
            outputDocumentExtList.add(outputDocumentExt);
        } catch (Exception ex) {
            getLogger().error("Failed to read XML string: " + ex.getMessage());
            session.write(inputFlowFile);
            session.transfer(inputFlowFile, REL_FAILURE);
        }

        // getLogger().warn("orderDocumentExtList.size(): " + orderDocumentExtList.size());

        documentCount++;
        if (outputDocumentExtList.size() > 99) {
            composeCount++;
            ComposeDocument document = transformListToCompose(outputDocumentExtList, composeCount, documentCount);
            FlowFile composeFlowFile = session.create(inputFlowFile);
            try {
                session.write(composeFlowFile, out -> out.write(jsonMapper.writeValueAsBytes(document)));
                session.transfer(composeFlowFile, REL_SUCCESS);
            } catch (Throwable ex) {
                session.remove(composeFlowFile);
                getLogger().error("Failed to send JSON string: " + ex.getLocalizedMessage());
            }
            outputDocumentExtList.clear();
        }
        session.remove(inputFlowFile);

    }

    private ComposeDocument transformListToCompose(List<OutputDocumentExt> outputDocumentExtList, int composeId, int documentCount) {
        ComposeDocument compose = new ComposeDocument();
        compose.setComposeId(composeId);
        compose.setComposeLabel(COMPOSE_LABEL + ": " + documentCount);
        List<OrderDocument> orderDocuments = new ArrayList<>();
        List<InvoiceDocument> invoiceDocuments = new ArrayList<>();
        for (OutputDocumentExt outputDocumentExt : outputDocumentExtList) {
            if (outputDocumentExt.getDocumentType().equals("order")) {
                orderDocuments.add(MapStructConverter.MAPPER.convertOrder(outputDocumentExt));
            } else if (outputDocumentExt.getDocumentType().equals("invoice")) {
                invoiceDocuments.add(MapStructConverter.MAPPER.convertInvoice(outputDocumentExt));
            }
        }
        compose.setOrderDocuments(orderDocuments);
        compose.setInvoiceDocuments(invoiceDocuments);
        return compose;
    }
}
