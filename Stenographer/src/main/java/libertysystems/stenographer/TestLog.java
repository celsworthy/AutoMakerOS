/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libertysystems.stenographer;

/**
 *
 * @author Ian Hudson Liberty Systems Limited
 */
public class TestLog
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Stenographer steno = StenographerFactory.getStenographer(TestLog.class.getName());
        steno.info("hello");
//        steno.info("Home:" + System.getProperty("user.home"));
//        String a = System.getProperty("user.home") + "\\/fred/f";
//        steno.info(a);
//        steno.info(a.replaceAll("\\\\", "/"));

        steno.debug("This is debug");
        steno.passthrough("This is passthrough");
System.err.println("Error through system");
        throw new NumberFormatException("This is a fake");
    }
}
