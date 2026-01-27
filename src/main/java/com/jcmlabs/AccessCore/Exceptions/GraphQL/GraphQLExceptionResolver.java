package com.jcmlabs.AccessCore.Exceptions.GraphQL;


import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;
import graphql.ErrorType;

import java.util.Map;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(
            @NonNull Throwable ex,
            @NonNull DataFetchingEnvironment env
    ) {

        if (ex instanceof ResourceNotFoundException rnfe) {
            return GraphqlErrorBuilder.newError(env)
                    .message(rnfe.getMessage())
                    .errorType(ErrorType.DataFetchingException)
                    .extensions(Map.of(
                            "code", GraphQLErrorCodes.NOT_FOUND,
                            "resource", rnfe.getResource(),
                            "field", rnfe.getField(),
                            "value", rnfe.getValue()
                    ))
                    .build();
        }

        return null;
    }
}




