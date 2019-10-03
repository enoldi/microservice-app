package com.prim4s.gatewayapi.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

public class AuthorizationHeaderFilter extends ZuulFilter {

    private final OAuth2AuthorizedClientService clientService;

    public AuthorizationHeaderFilter(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }


    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext requestContext = RequestContext.getCurrentContext();
        Optional<String> authorizationHeader = getAuthorizationHeader();
        authorizationHeader.ifPresent(s -> requestContext.addZuulRequestHeader("Authorization", s));
        return null;
    }

    private Optional<String> getAuthorizationHeader() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient auth2AuthorizedClient = clientService.loadAuthorizedClient(
                auth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                auth2AuthenticationToken.getName()
        );

        OAuth2AccessToken auth2AccessToken = auth2AuthorizedClient.getAccessToken();

        if ( auth2AccessToken == null ){
            return Optional.empty();
        } else {
            String tokenType = auth2AccessToken.getTokenType().getValue();
            String authorizationHeaderValue = String.format("%s %s", tokenType, auth2AccessToken.getTokenValue());
            return Optional.of(authorizationHeaderValue);
        }
    }
}
