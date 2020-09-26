/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxremote;

import celtech.roboxbase.configuration.BaseConfiguration;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;

public class RootUUID {

    private static final String ROOT_UUID_PROPERTIES_FILE = "rootuuid.properties";
    private static final String ROBOX_DOMAIN = "celuk.robox";
    private static String rootUUID = null;
    
    public static String get()
    {
        if (rootUUID == null)
        {
            rootUUID = loadRootUUIDFromProperties();
            if (rootUUID == null)
            {
                UUID id = null;
                try {
                    String seed = getMACAddress();
                    if (seed != null)
                    {
                        seed = seed.trim().toUpperCase();
                        if (!seed.isBlank())
                            id = generateType5UUID(seed, ROBOX_DOMAIN);
                    }
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                if (id == null)
                    id = UUID.randomUUID();
                rootUUID = id.toString();
                storeRootUUIDAsProperties(rootUUID);
            }
        }
        
        return rootUUID;
    }

    /**
     * Type 5 UUID Generation
     * 
     * @param namespace
     * @param name
     * @return 
     * @throws UnsupportedEncodingException 
     */
    public static UUID generateType5UUID(String namespace, String name) throws UnsupportedEncodingException {
        String source = namespace + name;
        byte[] bytes = source.getBytes("UTF-8");
        UUID uuid = type5UUIDFromBytes(bytes);
        return uuid;
    }

    public static UUID type5UUIDFromBytes(byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("SHA-1 not supported", nsae);
        }
        byte[] bytes = Arrays.copyOfRange(md.digest(name), 0, 16);
        bytes[6] &= 0x0f; /* clear version        */
        bytes[6] |= 0x50; /* set to version 5     */
        bytes[8] &= 0x3f; /* clear variant        */
        bytes[8] |= 0x80; /* set to IETF variant  */
        return constructType5UUID(bytes);
    }

    private static UUID constructType5UUID(byte[] data) {
        long msb = 0;
        long lsb = 0;
        assert data.length == 16 : "data must be 16 bytes in length";

        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (data[i] & 0xff);

        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);
        return new UUID(msb, lsb);
    }
    
    private static String getMACAddress()
    {
        String macAddress = "";
        try
        {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements())
            {
                NetworkInterface ni = (NetworkInterface) networkInterfaces
                        .nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while (nias.hasMoreElements())
                {
                    InetAddress ia = (InetAddress) nias.nextElement();
                    if (!ia.isLinkLocalAddress()
                            && !ia.isLoopbackAddress()
                            && ia instanceof Inet4Address)
                    {
                        byte[] mac = ni.getHardwareAddress();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X", mac[i]));		
                        }
                        macAddress = sb.toString();
                        break;
                    }
                }
            }
        }
        catch (SocketException e)
        {
        }
        
        return macAddress;
    }

    private static String loadRootUUIDFromProperties()
    {
        String id = null;
        InputStream input = null;

        try
        {
            input = new FileInputStream(BaseConfiguration.getUserStorageDirectory() + ROOT_UUID_PROPERTIES_FILE);

            // load a properties file
            Properties rootUUIDProperties = new Properties();
            rootUUIDProperties.load(input);
            id = rootUUIDProperties.getProperty("root_uuid");
        } catch (IOException ex)
        {
        } finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                } catch (IOException ex)
                {
                }
            }
        }
        return id;
    }

    private static void storeRootUUIDAsProperties(String id)
    {
        OutputStream output = null;

        try
        {
            output = new FileOutputStream(BaseConfiguration.getUserStorageDirectory() + ROOT_UUID_PROPERTIES_FILE);

            // load a properties file
            Properties rootUUIDProperties = new Properties();
            rootUUIDProperties.setProperty("root_uuid", id);
            rootUUIDProperties.store(output, "");
        } catch (IOException ex)
        {
        } finally
        {
            if (output != null)
            {
                try
                {
                    output.close();
                } catch (IOException ex)
                {
                }
            }
        }
    }
}