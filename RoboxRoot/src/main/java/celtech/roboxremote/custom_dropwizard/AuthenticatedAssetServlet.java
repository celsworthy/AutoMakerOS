package celtech.roboxremote.custom_dropwizard;

import celtech.roboxremote.security.RootAPIUnauthorisedHandler;
import celtech.roboxremote.security.User;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.servlets.assets.ByteRange;
import io.dropwizard.servlets.assets.ResourceURL;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Ian
 */
public class AuthenticatedAssetServlet extends HttpServlet
{

    private Stenographer steno = StenographerFactory.getStenographer(AuthenticatedAssetServlet.class.getName());
    private static final long serialVersionUID = 6393345594784987908L;
    private static final CharMatcher SLASHES = CharMatcher.is('/');
    private final Authenticator<BasicCredentials, User> authenticator;

    private static class CachedAsset
    {

        private final byte[] resource;
        private final String eTag;
        private final long lastModifiedTime;

        private CachedAsset(byte[] resource, long lastModifiedTime)
        {
            this.resource = resource;
            this.eTag = '"' + Hashing.murmur3_128().hashBytes(resource).toString() + '"';
            this.lastModifiedTime = lastModifiedTime;
        }

        public byte[] getResource()
        {
            return resource;
        }

        public String getETag()
        {
            return eTag;
        }

        public long getLastModifiedTime()
        {
            return lastModifiedTime;
        }
    }

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.HTML_UTF_8;

    private final String resourcePath;
    private final String uriPath;
    private final String indexFile;
    private final Charset defaultCharset;

    /**
     * Creates a new {@code AssetServlet} that serves static assets loaded from
     * {@code resourceURL} (typically a file: or jar: URL). The assets are
     * served at URIs rooted at {@code uriPath}. For example, given a
     * {@code resourceURL} of {@code "file:/data/assets"} and a {@code uriPath}
     * of {@code "/js"}, an {@code AssetServlet} would serve the contents of {@code
     * /data/assets/example.js} in response to a request for
     * {@code /js/example.js}. If a directory is requested and {@code indexFile}
     * is defined, then {@code AssetServlet} will attempt to serve a file with
     * that name in that directory. If a directory is requested and {@code
     * indexFile} is null, it will serve a 404.
     *
     * @param resourcePath the base URL from which assets are loaded
     * @param uriPath the URI path fragment in which all requests are rooted
     * @param indexFile the filename to use when directories are requested, or
     * null to serve no indexes
     * @param defaultCharset the default character set
     * @param authenticator
     * @param authorizer
     */
    public AuthenticatedAssetServlet(String resourcePath,
            String uriPath,
            String indexFile,
            Charset defaultCharset,
            Authenticator<BasicCredentials, User> authenticator)
    {
        final String trimmedPath = SLASHES.trimFrom(resourcePath);
        this.resourcePath = trimmedPath.isEmpty() ? trimmedPath : trimmedPath + '/';
        final String trimmedUri = SLASHES.trimTrailingFrom(uriPath);
        this.uriPath = trimmedUri.isEmpty() ? "/" : trimmedUri;
        this.indexFile = indexFile;
        this.defaultCharset = defaultCharset;
        this.authenticator = authenticator;
    }

    public URL getResourceURL()
    {
        return Resources.getResource(resourcePath);
    }

    public String getUriPath()
    {
        return uriPath;
    }

    public String getIndexFile()
    {
        return indexFile;
    }

    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException
    {
        steno.info("Serving " + req.getScheme() + req.getPathInfo());
        if (req.getPathInfo().startsWith("/rootMenu.html"))
        {
            String referrer = req.getHeader("referer");
            String authHeader = req.getHeader("Authorization");

            boolean authenticatedOK = false;

            if (authHeader != null)
            {
                StringTokenizer st = new StringTokenizer(authHeader);
                if (st.hasMoreTokens())
                {
                    String basic = st.nextToken();

                    if (basic.equalsIgnoreCase("Basic"))
                    {
                        try
                        {
                            String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
                            int p = credentials.indexOf(":");
                            if (p != -1)
                            {
                                String login = credentials.substring(0, p).trim();
                                String password = credentials.substring(p + 1).trim();

                                BasicCredentials basicCredentials = new BasicCredentials(login, password);
                                try
                                {
                                    Optional<User> authenticationResponse = authenticator.authenticate(basicCredentials);

                                    if (authenticationResponse.isPresent())
                                    {
                                        authenticatedOK = true;
                                    }
                                } catch (AuthenticationException ex)
                                {
                                    steno.error("Authentication exception");
                                }
                            } else
                            {
                                steno.warning("Invalid authentication token");
                            }
                        } catch (UnsupportedEncodingException e)
                        {
                            steno.warning("Couldn't retrieve authentication");
                        }
                    }
                }
            }

            if (!authenticatedOK)
            {
                if (referrer == null)
                {
                    //Didn't get any auth data
                    resp.sendRedirect("/login.html");
                } else
                {
                    //Didn't get any auth data
                    resp.addHeader("WWW-Authenticate", RootAPIUnauthorisedHandler.getAuthenticationString());
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
                return;
            }
        }

        try
        {
            final StringBuilder builder = new StringBuilder(req.getServletPath());
            if (req.getPathInfo() != null)
            {
                builder.append(req.getPathInfo());
            }
            final CachedAsset cachedAsset = loadAsset(builder.toString());
            if (cachedAsset == null)
            {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (isCachedClientSide(req, cachedAsset))
            {
                resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            final String rangeHeader = req.getHeader(HttpHeaders.RANGE);

            final int resourceLength = cachedAsset.getResource().length;
            ImmutableList<ByteRange> ranges = ImmutableList.of();

            boolean usingRanges = false;
            // Support for HTTP Byte Ranges
            // http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
            if (rangeHeader != null)
            {

                final String ifRange = req.getHeader(HttpHeaders.IF_RANGE);

                if (ifRange == null || cachedAsset.getETag().equals(ifRange))
                {

                    try
                    {
                        ranges = parseRangeHeader(rangeHeader, resourceLength);
                    } catch (NumberFormatException e)
                    {
                        resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return;
                    }

                    if (ranges.isEmpty())
                    {
                        resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return;
                    }

                    resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    usingRanges = true;

                    resp.addHeader(HttpHeaders.CONTENT_RANGE, "bytes "
                            + Joiner.on(",").join(ranges) + "/" + resourceLength);
                }
            }

            resp.setDateHeader(HttpHeaders.LAST_MODIFIED, cachedAsset.getLastModifiedTime());
            resp.setHeader(HttpHeaders.ETAG, cachedAsset.getETag());

            final String mimeTypeOfExtension = req.getServletContext()
                    .getMimeType(req.getRequestURI());
            MediaType mediaType = DEFAULT_MEDIA_TYPE;

            if (mimeTypeOfExtension != null)
            {
                try
                {
                    mediaType = MediaType.parse(mimeTypeOfExtension);
                    if (defaultCharset != null && mediaType.is(MediaType.ANY_TEXT_TYPE))
                    {
                        mediaType = mediaType.withCharset(defaultCharset);
                    }
                } catch (IllegalArgumentException ignore)
                {
                    // ignore
                }
            }

            if (mediaType.is(MediaType.ANY_VIDEO_TYPE)
                    || mediaType.is(MediaType.ANY_AUDIO_TYPE) || usingRanges)
            {
                resp.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
            }

            resp.setContentType(mediaType.type() + '/' + mediaType.subtype());

            if (mediaType.charset().isPresent())
            {
                resp.setCharacterEncoding(mediaType.charset().get().toString());
            }

            try (ServletOutputStream output = resp.getOutputStream())
            {
                if (usingRanges)
                {
                    for (ByteRange range : ranges)
                    {
                        output.write(cachedAsset.getResource(), range.getStart(),
                                range.getEnd() - range.getStart() + 1);
                    }
                } else
                {
                    output.write(cachedAsset.getResource());
                }
            }
        } catch (RuntimeException | URISyntaxException ignored)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private CachedAsset loadAsset(String key) throws URISyntaxException, IOException
    {
        checkArgument(key.startsWith(uriPath));
        final String requestedResourcePath = SLASHES.trimFrom(key.substring(uriPath.length()));
        final String absoluteRequestedResourcePath = SLASHES.trimFrom(this.resourcePath + requestedResourcePath);

        URL requestedResourceURL = getResourceUrl(absoluteRequestedResourcePath);
        if (ResourceURL.isDirectory(requestedResourceURL))
        {
            if (indexFile != null)
            {
                requestedResourceURL = getResourceUrl(absoluteRequestedResourcePath + '/' + indexFile);
            } else
            {
                // directory requested but no index file defined
                return null;
            }
        }

        long lastModified = ResourceURL.getLastModified(requestedResourceURL);
        if (lastModified < 1)
        {
            // Something went wrong trying to get the last modified time: just use the current time
            lastModified = System.currentTimeMillis();
        }

        // zero out the millis since the date we get back from If-Modified-Since will not have them
        lastModified = (lastModified / 1000) * 1000;
        return new CachedAsset(readResource(requestedResourceURL), lastModified);
    }

    protected URL getResourceUrl(String absoluteRequestedResourcePath)
    {
        return Resources.getResource(absoluteRequestedResourcePath);
    }

    protected byte[] readResource(URL requestedResourceURL) throws IOException
    {
        return Resources.toByteArray(requestedResourceURL);
    }

    private boolean isCachedClientSide(HttpServletRequest req, CachedAsset cachedAsset)
    {
        return cachedAsset.getETag().equals(req.getHeader(HttpHeaders.IF_NONE_MATCH))
                || (req.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE) >= cachedAsset.getLastModifiedTime());
    }

    /**
     * Parses a given Range header for one or more byte ranges.
     *
     * @param rangeHeader Range header to parse
     * @param resourceLength Length of the resource in bytes
     * @return List of parsed ranges
     */
    private ImmutableList<ByteRange> parseRangeHeader(final String rangeHeader,
            final int resourceLength)
    {
        final ImmutableList.Builder<ByteRange> builder = ImmutableList.builder();
        if (rangeHeader.indexOf("=") != -1)
        {
            final String[] parts = rangeHeader.split("=");
            if (parts.length > 1)
            {
                final List<String> ranges = Splitter.on(",").trimResults().splitToList(parts[1]);
                for (final String range : ranges)
                {
                    builder.add(ByteRange.parse(range, resourceLength));
                }
            }
        }
        return builder.build();
    }
}
