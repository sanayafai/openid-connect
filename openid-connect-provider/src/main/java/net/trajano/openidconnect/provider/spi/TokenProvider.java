package net.trajano.openidconnect.provider.spi;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Set;

import javax.json.JsonObject;
import javax.validation.constraints.NotNull;

import net.trajano.openidconnect.auth.AuthenticationRequest;
import net.trajano.openidconnect.core.Scope;
import net.trajano.openidconnect.token.IdToken;
import net.trajano.openidconnect.token.IdTokenResponse;

/**
 * This provides storage and retrieval for the token responses. Implementers
 * would generally be providing an expiring cache that provides multiple key
 * types pointing to the same token response instance.
 *
 * @author Archimedes
 */
public interface TokenProvider {

    /**
     * Gets the token data by access token. This may return null if there is no
     * data found for the access token.
     *
     * @param accessToken
     *            access token.
     * @return token response data
     */
    IdTokenResponse getByAccessToken(String accessToken);

    /**
     * Gets the token by authorization code.
     *
     * @param code
     * @param deleteAfterRetrieval
     *            if true the implementation is expected to remove the code
     *            mapping to the token after the call
     * @return
     */
    IdTokenResponse getByCode(String code,
            boolean deleteAfterRetrieval);

    /**
     * @param clientId
     * @param refreshToken
     * @param scopes
     *            this may be null in which case the original scope is used, but
     *            can be used to reduce the scope after refresh.
     * @param expiresIn
     *            time in seconds of how long the new token will expire this
     *            will also reissue the ID token. This may be null to use the
     *            provider's default
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    IdTokenResponse refreshToken(@NotNull String clientId,
            @NotNull String refreshToken,
            Set<Scope> scopes,
            Integer expiresIn) throws IOException,
                    GeneralSecurityException;

    /**
     * Builds an {@link IdToken} for the subject . Extra implementation specific
     * options to the storage process to change certain aspects of the token can
     * be provided as well.
     *
     * @param subject
     *            subject
     * @return authorization code
     */
    String createNewToken(String subject,
            URI issuerUri,
            AuthenticationRequest request) throws IOException,
                    GeneralSecurityException;

    IdTokenResponse getByConsent(Consent consent);

    JsonObject getClaimsByAccessToken(String accessToken);
}
