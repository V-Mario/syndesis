/*
 * Copyright (C) 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.syndesis.connector.sheets;

import com.google.api.services.sheets.v4.model.ValueRange;
import io.syndesis.connector.sheets.model.GoogleValueRange;
import org.apache.camel.Exchange;
import org.apache.camel.component.google.sheets.internal.GoogleSheetsApiCollection;
import org.apache.camel.component.google.sheets.internal.SheetsSpreadsheetsValuesApiMethod;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class GoogleSheetsAppendValuesCustomizerTest extends AbstractGoogleSheetsCustomizerTestSupport {

    private GoogleSheetsAppendValuesCustomizer customizer;

    @Before
    public void setupCustomizer() {
        customizer = new GoogleSheetsAppendValuesCustomizer();
    }

    @Test
    public void testBeforeProducerFromOptions() throws Exception {
        Map<String, Object> options = new HashMap<>();
        options.put("spreadsheetId", getSpreadsheetId());
        options.put("range", "A1");
        options.put("values", "a1");
        options.put("valueInputOption", "RAW");

        customizer.customize(getComponent(), options);

        Exchange inbound = new DefaultExchange(createCamelContext());
        getComponent().getBeforeProducer().process(inbound);

        Assert.assertEquals(GoogleSheetsApiCollection.getCollection().getApiName(SheetsSpreadsheetsValuesApiMethod.class).getName(), options.get("apiName"));
        Assert.assertEquals("append", options.get("methodName"));

        Assert.assertEquals(getSpreadsheetId(), inbound.getIn().getHeader("CamelGoogleSheets.spreadsheetId"));
        Assert.assertEquals("A1", inbound.getIn().getHeader("CamelGoogleSheets.range"));
        Assert.assertEquals("RAW", inbound.getIn().getHeader("CamelGoogleSheets.valueInputOption"));

        ValueRange valueRange = (ValueRange) inbound.getIn().getHeader("CamelGoogleSheets.values");
        Assert.assertEquals(1L, valueRange.getValues().size());
        Assert.assertEquals("a1", valueRange.getValues().get(0).get(0));
    }

    @Test
    public void testBeforeProducerFromModel() throws Exception {
        Map<String, Object> options = new HashMap<>();
        customizer.customize(getComponent(), options);

        Exchange inbound = new DefaultExchange(createCamelContext());

        GoogleValueRange model = new GoogleValueRange();
        model.setSpreadsheetId(getSpreadsheetId());
        model.setRange("A1:B1");
        model.setValues("a1,b1");
        inbound.getIn().setBody(model);

        getComponent().getBeforeProducer().process(inbound);

        Assert.assertEquals(GoogleSheetsApiCollection.getCollection().getApiName(SheetsSpreadsheetsValuesApiMethod.class).getName(), options.get("apiName"));
        Assert.assertEquals("append", options.get("methodName"));

        Assert.assertEquals(getSpreadsheetId(), inbound.getIn().getHeader("CamelGoogleSheets.spreadsheetId"));
        Assert.assertEquals("A1:B1", inbound.getIn().getHeader("CamelGoogleSheets.range"));
        Assert.assertEquals("USER_ENTERED", inbound.getIn().getHeader("CamelGoogleSheets.valueInputOption"));

        ValueRange valueRange = (ValueRange) inbound.getIn().getHeader("CamelGoogleSheets.values");
        Assert.assertEquals(1L, valueRange.getValues().size());
        Assert.assertEquals("a1", valueRange.getValues().get(0).get(0));
        Assert.assertEquals("b1", valueRange.getValues().get(0).get(1));
    }

    @Test
    public void testBeforeProducerMultipleRows() throws Exception {
        Map<String, Object> options = new HashMap<>();
        customizer.customize(getComponent(), options);

        Exchange inbound = new DefaultExchange(createCamelContext());

        GoogleValueRange model = new GoogleValueRange();
        model.setSpreadsheetId(getSpreadsheetId());
        model.setRange("A1:B2");
        model.setValues("[[\"a1\",\"b1\"],[\"a2\",\"b2\"]]");
        inbound.getIn().setBody(model);

        getComponent().getBeforeProducer().process(inbound);

        Assert.assertEquals("A1:B2", inbound.getIn().getHeader("CamelGoogleSheets.range"));
        Assert.assertEquals("USER_ENTERED", inbound.getIn().getHeader("CamelGoogleSheets.valueInputOption"));

        ValueRange valueRange = (ValueRange) inbound.getIn().getHeader("CamelGoogleSheets.values");
        Assert.assertEquals(2L, valueRange.getValues().size());
        Assert.assertEquals("a1", valueRange.getValues().get(0).get(0));
        Assert.assertEquals("b1", valueRange.getValues().get(0).get(1));
        Assert.assertEquals("a2", valueRange.getValues().get(1).get(0));
        Assert.assertEquals("b2", valueRange.getValues().get(1).get(1));
    }
}