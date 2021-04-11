/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.gcodeviewer.comms;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import static org.lwjgl.glfw.GLFW.glfwPostEmptyEvent;

/**
 *
 * @author Tony
 */
public class CommandQueue extends Thread {
    private final static Stenographer STENO = StenographerFactory.getStenographer(CommandQueue.class.getName());
    private final BlockingQueue<String> pendingCommands;

    private boolean running = false;
    
    public CommandQueue()
    {
        this.setDaemon(true);
        this.setName("CommandQueue");
        this.setPriority(Thread.MAX_PRIORITY);
        
        pendingCommands = new ArrayBlockingQueue<>(10);
    }

    public boolean commandAvailable()
    {
        return !pendingCommands.isEmpty();
    }

    public String getNextCommandFromQueue()
    {
        String command = "";
        try {
            command = pendingCommands.take();
        } catch(InterruptedException e) {
            //e.printStackTrace();
        }
        
        STENO.debug("next command = \"" + command + "\"");
        return command;
    }

    public void addCommandToQueue(String command)
    {
        STENO.debug("Add command = \"" + command + "\"");
        try {
            pendingCommands.put(command);
            glfwPostEmptyEvent(); // Wake up main thread.
        } catch(InterruptedException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        STENO.debug("Command Queue running ...");
        Scanner scanner = new Scanner(System.in);
        running = true;
        while (running) {
            String inputString = scanner.nextLine();
            if (inputString.equalsIgnoreCase("q")) {
                running = false;
            }
            addCommandToQueue(inputString);
        }
        STENO.debug("Command Queue finished");
    }
    
    public void stopRunning() {
        running = false;
        // Clearing the pendingCommands queue should release the run thread, if it is blocked,
        // which should then terminate
        pendingCommands.clear();
    }
}
