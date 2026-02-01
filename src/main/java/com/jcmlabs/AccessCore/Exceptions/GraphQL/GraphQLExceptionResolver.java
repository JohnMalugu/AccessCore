package com.jcmlabs.AccessCore.Exceptions.GraphQL;


import com.jcmlabs.AccessCore.Exceptions.Domain.ResourceNotFoundException;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
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

        return switch (ex){

            case ResourceNotFoundException rnfe ->
                    GraphqlErrorBuilder.newError(env)
                            .message(rnfe.getMessage())
                            .extensions(Map.of(
                                    "code", ResponseCode.NOT_FOUND,
                                    "error", GraphQLErrorCodes.NOT_FOUND,
                                    "details", Map.of(
                                            "resource", rnfe.getResource(),
                                            "field", rnfe.getField(),
                                            "value", rnfe.getValue()
                                    )
                            ))
                            .build();


            default -> throw new IllegalStateException("Unexpected value: " + ex);
        };
    }
}




