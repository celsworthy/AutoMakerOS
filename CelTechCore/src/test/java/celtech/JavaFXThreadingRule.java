package celtech;

import celtech.appManager.TestSystemNotificationManager;
import celtech.configuration.ApplicationConfiguration;
import celtech.postprocessor.TestGCodeOutputWriter;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.utils.tasks.TestTaskExecutor;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit {@link Rule} for running tests on the JavaFX thread and performing
 * JavaFX initialisation. To include in your test case, add the following code:
 *
 * <pre>
 * {@literal @}Rule
 * public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();
 * </pre>
 *
 * @author Andy Till
 *
 */
public class JavaFXThreadingRule implements TestRule
{

    /**
     * Flag for setting up the JavaFX, we only need to do this once for all
     * tests.
     */
    private static boolean jfxIsSetup;

    @Override
    public Statement apply(Statement statement, Description description)
    {

        return new OnJFXThreadStatement(statement);
    }

    private static class OnJFXThreadStatement extends Statement
    {

        private final TemporaryFolder temporaryUserStorageFolder = new TemporaryFolder();
        private String userStorageFolderPath;

        private final Statement statement;

        public OnJFXThreadStatement(Statement aStatement)
        {
            statement = aStatement;
        }

        private Throwable rethrownException = null;

        @Override
        public void evaluate() throws Throwable
        {

            if (!jfxIsSetup)
            {
                setupJavaFX();

                jfxIsSetup = true;
            }

            final CountDownLatch countDownLatch = new CountDownLatch(1);

            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        statement.evaluate();
                    } catch (Throwable e)
                    {
                        rethrownException = e;
                    }
                    countDownLatch.countDown();
                }
            });

            countDownLatch.await();

            // if an exception was thrown by the statement during evaluation,
            // then re-throw it to fail the test
            if (rethrownException != null)
            {
                throw rethrownException;
            }
        }

        protected void setupJavaFX() throws InterruptedException
        {
            Properties testProperties = new Properties();

            testProperties.setProperty(
                    "language", "UK");
            URL applicationInstallURL = JavaFXConfiguredTest.class.getResource("/InstallDir/AutoMaker/");
            userStorageFolderPath = temporaryUserStorageFolder.getRoot().getAbsolutePath()
                    + File.separator;

            BaseConfiguration.setInstallationProperties(
                    testProperties,
                    applicationInstallURL.getFile(),
                    userStorageFolderPath);

            File filamentDir = new File(userStorageFolderPath
                    + BaseConfiguration.filamentDirectoryPath
                    + File.separator);

            filamentDir.mkdirs();

            new File(userStorageFolderPath
                    + BaseConfiguration.printSpoolStorageDirectoryPath
                    + File.separator).mkdirs();

            new File(userStorageFolderPath
                    + ApplicationConfiguration.projectFileDirectoryPath
                    + File.separator).mkdirs();

            Lookup.setupDefaultValues();

            // force initialisation
            URL configURL = JavaFXConfiguredTest.class.getResource("/AutoMaker.configFile.xml");

            System.setProperty(
                    "libertySystems.configFile", configURL.getFile());
            String installDir = BaseConfiguration.getApplicationInstallDirectory(
                    Lookup.class);

            BaseLookup.setTaskExecutor(
                    new TestTaskExecutor());
            BaseLookup.setSystemNotificationHandler(
                    new TestSystemNotificationManager());

            BaseLookup.setPostProcessorOutputWriterFactory(TestGCodeOutputWriter::new);

            long timeMillis = System.currentTimeMillis();

            final CountDownLatch latch = new CountDownLatch(1);

            SwingUtilities.invokeLater(
                    new Runnable()
                    {

                        public void run()
                        {
                            // initializes JavaFX environment
                            new JFXPanel();

                            latch.countDown();
                        }
                    }
            );

            System.out.println(
                    "javafx initialising...");
            latch.await();

            System.out.println(
                    "javafx is initialised in " + (System.currentTimeMillis() - timeMillis) + "ms");
        }

    }
}
