/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxremote.custom_dropwizard;

import celtech.roboxremote.security.User;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import java.nio.file.Path;

/**
 *
 * @author alynch
 */
public class ExternalAuthenticatedAssetsBundle extends AuthenticatedAssetsBundle {

    private final Path externalStaticDir;

    public ExternalAuthenticatedAssetsBundle(Path externalStaticDir,
            String resourcePath, String uriPath,
            Authenticator<BasicCredentials, User> authenticator) {
        super(resourcePath, uriPath, authenticator);
        this.externalStaticDir = externalStaticDir;
    }

    @Override
    protected ExternalAuthenticatedAssetServlet createServlet() {
        return new ExternalAuthenticatedAssetServlet(externalStaticDir,
                resourcePath, uriPath, indexFile,
                authenticator);
    }

}
