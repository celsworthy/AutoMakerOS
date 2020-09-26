/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxremote.custom_dropwizard;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxremote.security.User;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author alynch
 */
public class ExternalAuthenticatedAssetServlet extends AuthenticatedAssetServlet {

    private static final Stenographer steno = StenographerFactory.getStenographer(BaseConfiguration.class.getName());

    private final Path externalStaticDir;

    public ExternalAuthenticatedAssetServlet(Path externalStaticDir,
            String resourcePath,
            String uriPath,
            String indexFile,
            Authenticator<BasicCredentials, User> authenticator) {

        super(resourcePath, uriPath, indexFile, StandardCharsets.UTF_8, authenticator);
        this.externalStaticDir = externalStaticDir;
        steno.info("Using external static dir at " + externalStaticDir);
    }

    @Override
    protected byte[] readResource(URL requestedResourceURL) throws IOException {
        String absPath = requestedResourceURL.getPath();

        int assetsIx = absPath.lastIndexOf("assets");
        String relPath = absPath.substring(assetsIx + 7);
        Path fileLocation = externalStaticDir.resolve(relPath);
        if (Files.isReadable(fileLocation))
        {
            System.out.println("Get external resource: " + absPath);
            return Files.readAllBytes(fileLocation);
        } else
        {
            return super.readResource(requestedResourceURL);
        }
    }

}
