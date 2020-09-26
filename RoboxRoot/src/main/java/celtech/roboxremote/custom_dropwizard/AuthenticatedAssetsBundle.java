package celtech.roboxremote.custom_dropwizard;

import celtech.roboxremote.security.User;
import static com.google.common.base.Preconditions.checkArgument;
import io.dropwizard.Bundle;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ian
 */
public class AuthenticatedAssetsBundle implements Bundle
{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatedAssetsBundle.class);

    private static final String DEFAULT_ASSETS_NAME = "assets";
    private static final String DEFAULT_INDEX_FILE = "index.html";
    private static final String DEFAULT_PATH = "/assets";

    protected final String resourcePath;
    protected final String uriPath;
    protected final String indexFile;
    protected final String assetsName;

    protected final Authenticator<BasicCredentials, User> authenticator;

    /**
     * Creates a new AssetsBundle which serves up static assets from
     * {@code src/main/resources/assets/*} as {@code /assets/*}.
     *
     * @see AssetsBundle#AssetsBundle(String, String, String)
     */
    public AuthenticatedAssetsBundle()
    {
        this(DEFAULT_PATH, DEFAULT_PATH, DEFAULT_INDEX_FILE, DEFAULT_ASSETS_NAME, null);
    }

    /**
     * Creates a new AssetsBundle which will configure the application to serve
     * the static files located in {@code src/main/resources/${path}} as
     * {@code /${path}}. For example, given a {@code path} of
     * {@code "/assets"}, {@code src/main/resources/assets/example.js} would be
     * served up from {@code /assets/example.js}.
     *
     * @param path the classpath and URI root of the static asset files
     * @see AssetsBundle#AssetsBundle(String, String, String)
     */
    public AuthenticatedAssetsBundle(String path, Authenticator<BasicCredentials, User> authenticator)
    {
        this(path, path, DEFAULT_INDEX_FILE, DEFAULT_ASSETS_NAME, authenticator);
    }

    /**
     * Creates a new AssetsBundle which will configure the application to serve
     * the static files located in {@code src/main/resources/${resourcePath}} as
     * {@code /${uriPath}}. For example, given a {@code resourcePath} of
     * {@code "/assets"} and a uriPath of {@code "/js"},
     * {@code src/main/resources/assets/example.js} would be served up from
     * {@code /js/example.js}.
     *
     * @param resourcePath the resource path (in the classpath) of the static
     * asset files
     * @param uriPath the uri path for the static asset files
     * @see AssetsBundle#AssetsBundle(String, String, String)
     */
    public AuthenticatedAssetsBundle(String resourcePath, String uriPath, Authenticator<BasicCredentials, User> authenticator)
    {
        this(resourcePath, uriPath, DEFAULT_INDEX_FILE, DEFAULT_ASSETS_NAME, authenticator);
    }

    /**
     * Creates a new AssetsBundle which will configure the application to serve
     * the static files located in {@code src/main/resources/${resourcePath}} as
     * {@code /${uriPath}}. If no file name is in ${uriPath}, ${indexFile} is
     * appended before serving. For example, given a {@code resourcePath} of
     * {@code "/assets"} and a uriPath of {@code "/js"},
     * {@code src/main/resources/assets/example.js} would be served up from
     * {@code /js/example.js}.
     *
     * @param resourcePath the resource path (in the classpath) of the static
     * asset files
     * @param uriPath the uri path for the static asset files
     * @param indexFile the name of the index file to use
     */
    public AuthenticatedAssetsBundle(String resourcePath, String uriPath, String indexFile, Authenticator<BasicCredentials, User> authenticator)
    {
        this(resourcePath, uriPath, indexFile, DEFAULT_ASSETS_NAME, authenticator);
    }

    /**
     * Creates a new AssetsBundle which will configure the application to serve
     * the static files located in {@code src/main/resources/${resourcePath}} as
     * {@code /${uriPath}}. If no file name is in ${uriPath}, ${indexFile} is
     * appended before serving. For example, given a {@code resourcePath} of
     * {@code "/assets"} and a uriPath of {@code "/js"},
     * {@code src/main/resources/assets/example.js} would be served up from
     * {@code /js/example.js}.
     *
     * @param resourcePath the resource path (in the classpath) of the static
     * asset files
     * @param uriPath the uri path for the static asset files
     * @param indexFile the name of the index file to use
     * @param assetsName the name of servlet mapping used for this assets bundle
     * @param authenticator
     */
    public AuthenticatedAssetsBundle(String resourcePath, String uriPath, String indexFile, String assetsName, Authenticator<BasicCredentials, User> authenticator)
    {
        checkArgument(resourcePath.startsWith("/"), "%s is not an absolute path", resourcePath);
        checkArgument(!"/".equals(resourcePath), "%s is the classpath root", resourcePath);
        this.resourcePath = resourcePath.endsWith("/") ? resourcePath : (resourcePath + '/');
        this.uriPath = uriPath.endsWith("/") ? uriPath : (uriPath + '/');
        this.indexFile = indexFile;
        this.assetsName = assetsName;
        this.authenticator = authenticator;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        // nothing doing
    }

    @Override
    public void run(Environment environment)
    {
        LOGGER.info("Registering AssetBundle with name: {} for path {}", assetsName, uriPath + '*');
        environment.servlets().addServlet(assetsName, createServlet()).addMapping(uriPath + '*');
    }

    public String getResourcePath()
    {
        return resourcePath;
    }

    public String getUriPath()
    {
        return uriPath;
    }

    public String getIndexFile()
    {
        return indexFile;
    }

    protected AuthenticatedAssetServlet createServlet()
    {
        return new AuthenticatedAssetServlet(resourcePath, uriPath, indexFile, StandardCharsets.UTF_8, authenticator);
    }
}
