package net.trajano.openidconnect.jaspic.internal;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.security.auth.message.AuthStatus;

import net.trajano.openidconnect.jaspic.OpenIdConnectAuthModule;

public class UserInfoRequestProcessor implements ValidateRequestProcessor {

    @Override
    public boolean canValidateRequest(final ValidateContext context) {

        if (!context.hasTokenCookie()) {
            return false;
        }

        return context.isSecure() && context.isGetRequest() && context.isRequestUri(OpenIdConnectAuthModule.USERINFO_URI_KEY);
    }

    @Override
    public AuthStatus validateRequest(final ValidateContext context) throws IOException,
            GeneralSecurityException {

        context.getResp()
                .getWriter()
                .print(context.getTokenCookie()
                        .getUserInfo());
        return AuthStatus.SEND_SUCCESS;
    }

}
