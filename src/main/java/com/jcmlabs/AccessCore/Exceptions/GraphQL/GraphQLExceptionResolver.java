package com.jcmlabs.AccessCore.Exceptions.GraphQL;


import com.jcmlabs.AccessCore.Exceptions.BusinessException;
import com.jcmlabs.AccessCore.Exceptions.Domain.ResourceNotFoundException;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.security.authentication.BadCredentialsException;
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

            case BusinessException be ->
                    GraphqlErrorBuilder.newError(env)
                            .message(be.getMessage())
                            .extensions(Map.of(
                                    "code", be.getCode(),
                                    "error", GraphQLErrorCodes.BAD_REQUEST
                            ))
                            .build();


            case DataIntegrityViolationException ignored ->
                    GraphqlErrorBuilder.newError(env)
                            .message("Duplicate or invalid data detected")
                            .extensions(Map.of(
                                    "code", ResponseCode.DUPLICATE,
                                    "error", GraphQLErrorCodes.CONFLICT
                            ))
                            .build();

            case BadCredentialsException se ->
                    GraphqlErrorBuilder.newError(env)
                            .message(se.getMessage())
                            .extensions(Map.of(
                                    "code", ResponseCode.UNAUTHORIZED,
                                    "error", GraphQLErrorCodes.UNAUTHORIZED
                            ))
                            .build();

            default -> throw new IllegalStateException("Unexpected value: " + ex);
        };
    }
}




