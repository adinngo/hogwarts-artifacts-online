package com.example.hogwarts_artifacts_online.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class UserRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final UriTemplate USER_URI_TEMPLATE = new UriTemplate("/users/{userId}");
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        //extract userId from the request
        Map<String, String> uriVariables = USER_URI_TEMPLATE.match(context.getRequest().getRequestURI());
        String userIdFromRequestUri = uriVariables.get("userId");

        //extract userId from jwt
        Authentication authentication = authenticationSupplier.get();
        String userIdFromJwt = ((Jwt) authentication.getPrincipal()).getClaim("userId").toString();

        //check if user has role user
        boolean hasUserRole = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().contains("user"));

        //check if user has role admin
        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().contains("admin"));

        //compare two userId
        boolean userIdsMatch = userIdFromRequestUri != null && userIdFromRequestUri.equals(userIdFromJwt);
        return new AuthorizationDecision(hasAdminRole || (hasUserRole && userIdsMatch));
    }
}
