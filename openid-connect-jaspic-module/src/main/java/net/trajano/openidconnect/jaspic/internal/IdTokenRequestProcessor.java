package net.trajano.openidconnect.jaspic.internal;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.json.Json;
import javax.security.auth.message.AuthStatus;
import javax.ws.rs.core.MediaType;

import net.trajano.openidconnect.jaspic.OpenIdConnectAuthModule;

public class IdTokenRequestProcessor implements ValidateRequestProcessor {

    @Override
    public boolean canValidateRequest(final ValidateContext context) {

        if (!context.hasTokenCookie()) {
            return false;
        }

        return context.isSecure() && context.isGetRequest() && context.isRequestUri(OpenIdConnectAuthModule.TOKEN_URI_KEY);
    }

    @Override
    public AuthStatus validateRequest(final ValidateContext context) throws IOException,
    GeneralSecurityException {

        context.setContentType(MediaType.APPLICATION_JSON);
        Json.createWriter(context.getResp()
                .getOutputStream())
                .writeObject(context.getIdToken());
        return AuthStatus.SEND_SUCCESS;
    }
}
