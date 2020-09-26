package celtech.roboxbase.services.printing;

import celtech.roboxbase.configuration.BaseConfiguration;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import java.io.File;
import java.io.IOException;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Tony Aldhous
 */
public class SFTPUtils
{

    private static final String PRIVATE_KEY = "automaker-root.ssh";
    private static final int[] pp1 = {81, 86, 10, 93, 51, 78, 87, 120, 117};
    private static final int[] pp2 = {-14, 155, 66, 138, 31, 189, 11, 231, 3};
    private static final String USER = "pi";
    
    private final Stenographer steno = StenographerFactory.getStenographer(this.getClass().getName());
    private String hostAddress = null;
           
    public SFTPUtils(String hostAddress)
    {
        this.hostAddress = hostAddress;
    }
    
    private void createRemoteDirectory(ChannelSftp channelSftp, String remoteDirectory) throws SftpException
    {
        SftpATTRS attrs = null;
        try
        {
            attrs = channelSftp.stat(remoteDirectory);
        }
        catch (Exception e)
        {
            steno.debug("Remote directory \"" + remoteDirectory + "\" not found");
        }

        if (attrs == null)
        {
            steno.info("Creating remote directory \"" + remoteDirectory + "\"");
            channelSftp.mkdir(remoteDirectory);
        }
    }
    
    public boolean transferToRemotePrinter(File localFile, String remoteDirectory, String remoteFile, SftpProgressMonitor monitor)
    {
        // Use sftp to transfer file to remote printer.
        boolean transferredOK = false;
        String remotePath = remoteFile;
        try
        {
            JSch jsch = new JSch();
            steno.info("Connecting to SFTP service");
            String pp = "";
            for (int i = 0; i < 9; ++i)
                pp += Character.toString((char)((i % 2) == 0 ? pp1[i] + pp2[i] : pp2[i] - pp1[i]));
            jsch.addIdentity(BaseConfiguration.getApplicationKeyDirectory() + PRIVATE_KEY, pp);
            Session session = jsch.getSession(USER, hostAddress, 22);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            steno.debug("Connected to host \"" + hostAddress + "\"");

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
                        
            if (remoteDirectory != null && !remoteDirectory.isEmpty())
            {
                // Create directory, creating any missing parent directories.
                // This fixes a problem where the transfer fails because
                // the project directory has not been created.
                String[] pathComponents = remoteDirectory.split("/");
                remotePath = "";
                for(int i = 0; i < pathComponents.length; ++i)
                {
                    if (i == 0)
                        remotePath = pathComponents[0];
                    else
                        remotePath = remotePath + "/" + pathComponents[i];
                    if (!remotePath.isEmpty())
                        createRemoteDirectory(channelSftp, remotePath);
                }
                remotePath = remoteDirectory + '/' + remoteFile;
            }

            steno.info("Transferring file");
            channelSftp.put(localFile.getCanonicalPath(),
                            remotePath,
                            monitor);

            channelSftp.disconnect();
            session.disconnect();
            
            transferredOK = true;
        }
        catch (SftpException | JSchException | IOException ex)
        {
            steno.error("Failed to transfer \"" + localFile.getPath() + "\" to remote printer file \"" + remotePath + "\": " + ex.getMessage());
        }

        return transferredOK;
    }
}
