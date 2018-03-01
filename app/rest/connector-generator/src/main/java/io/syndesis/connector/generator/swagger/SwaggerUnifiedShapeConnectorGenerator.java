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
package io.syndesis.connector.generator.swagger;

import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import io.syndesis.model.DataShape;
import io.syndesis.model.action.ConnectorDescriptor;

public final class SwaggerUnifiedShapeConnectorGenerator extends BaseSwaggerConnectorGenerator {

    private final DataShapeGenerator dataShapeGenerator;

    public SwaggerUnifiedShapeConnectorGenerator() {
        dataShapeGenerator = new UnifiedDataShapeGenerator();
    }

    @Override
    ConnectorDescriptor.Builder createDescriptor(final String specification, final Swagger swagger, final Operation operation) {
        final ConnectorDescriptor.Builder actionDescriptor = new ConnectorDescriptor.Builder();

        final DataShape inputDataShape = dataShapeGenerator.createShapeFromRequest(specification, swagger, operation);
        actionDescriptor.inputDataShape(inputDataShape);

        final DataShape outputDataShape = dataShapeGenerator.createShapeFromResponse(specification, swagger, operation);
        actionDescriptor.outputDataShape(outputDataShape);

        actionDescriptor.putConfiguredProperty("operationId", operation.getOperationId());

        return actionDescriptor;
    }

}