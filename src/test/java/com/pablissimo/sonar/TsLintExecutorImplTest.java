package com.pablissimo.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.TempFolder;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;

import edu.emory.mathcs.backport.java.util.Arrays;

public class TsLintExecutorImplTest {
    TsLintExecutorImpl executorImpl;
    CommandExecutor commandExecutor;
    TempFolder tempFolder;
    File tempOutputFile;

    System2 system;

    TsLintExecutorConfig config;

    @Before
    public void setUp() throws Exception {
        this.system = mock(System2.class);
        this.tempFolder = mock(TempFolder.class);

        this.tempOutputFile = mock(File.class);
        when(this.tempOutputFile.getAbsolutePath()).thenReturn("path/to/temp");
        when(this.tempFolder.newFile()).thenReturn(this.tempOutputFile);

        this.commandExecutor = mock(CommandExecutor.class);

        this.executorImpl = spy(new TsLintExecutorImpl(this.system, this.tempFolder));
        when(this.executorImpl.createExecutor()).thenReturn(this.commandExecutor);
        doReturn(mock(BufferedReader.class)).when(this.executorImpl).getBufferedReaderForFile(any(File.class));

        // Setup a default config, which each method will mutate as required
        this.config = new TsLintExecutorConfig();
        this.config.setPathToNode("node");
        this.config.setPathToTsLint("path/to/tslint");
        this.config.setConfigFile("path/to/config");
        this.config.setRulesDir("path/to/rules");
        this.config.setTimeoutMs(40000);
    }

    @Test
    public void executesCommandWithCorrectArgumentsAndTimeouts() {
        final ArrayList<Command> capturedCommands = new ArrayList<Command>();
        final ArrayList<Long> capturedTimeouts = new ArrayList<Long>();

        Answer<Integer> captureCommand = new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                capturedCommands.add((Command) invocation.getArguments()[0]);
                capturedTimeouts.add((long) invocation.getArguments()[3]);
                return 0;
            }
        };

        when(this.commandExecutor.execute(any(Command.class), any(StreamConsumer.class), any(StreamConsumer.class), any(long.class))).then(captureCommand);
        this.executorImpl.execute(this.config, Arrays.asList(new String[] { "path/to/file", "path/to/another" }));

        assertEquals(1, capturedCommands.size());

        Command theCommand = capturedCommands.get(0);
        long theTimeout = capturedTimeouts.get(0);

        assertEquals("node path/to/tslint --format json --rules-dir path/to/rules --out path/to/temp --config path/to/config path/to/file path/to/another", theCommand.toCommandLine());
        // Expect one timeout period per file processed
        assertEquals(2 * 40000, theTimeout);
    }

    @Test
    public void doesNotSendFileListToTsLint_ifConfigSaysToUseProjectFile() {
        final ArrayList<Command> capturedCommands = new ArrayList<Command>();
        final ArrayList<Long> capturedTimeouts = new ArrayList<Long>();

        Answer<Integer> captureCommand = new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                capturedCommands.add((Command) invocation.getArguments()[0]);
                capturedTimeouts.add((long) invocation.getArguments()[3]);
                return 0;
            }
        };

        this.config.setPathToTsConfig("path/to/tsconfig.json");

        when(this.commandExecutor.execute(any(Command.class), any(StreamConsumer.class), any(StreamConsumer.class), any(long.class))).then(captureCommand);
        this.executorImpl.execute(this.config, Arrays.asList(new String[] { "path/to/file", "path/to/another" }));

        assertEquals(1, capturedCommands.size());

        Command theCommand = capturedCommands.get(0);
        long theTimeout = capturedTimeouts.get(0);

        assertEquals("node path/to/tslint --format json --rules-dir path/to/rules --out path/to/temp --config path/to/config --project path/to/tsconfig.json", theCommand.toCommandLine());
        // Timeout should be just what we specified since we're not batching

        assertEquals(40000, theTimeout);
    }

    @Test
    public void usesTypeCheckParameter_ifConfigSaysToUseTypeCheck() {
        final ArrayList<Command> capturedCommands = new ArrayList<Command>();

        Answer<Integer> captureCommand = new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                capturedCommands.add((Command) invocation.getArguments()[0]);
                return 0;
            }
        };

        this.config.setPathToTsConfig("path/to/tsconfig.json");
        this.config.setShouldPerformTypeCheck(true);

        when(this.commandExecutor.execute(any(Command.class), any(StreamConsumer.class), any(StreamConsumer.class), any(long.class))).then(captureCommand);
        this.executorImpl.execute(this.config, Arrays.asList(new String[] { "path/to/file", "path/to/another" }));

        assertEquals(1, capturedCommands.size());

        Command theCommand = capturedCommands.get(0);

        assertEquals("node path/to/tslint --format json --rules-dir path/to/rules --out path/to/temp --config path/to/config --project path/to/tsconfig.json --type-check", theCommand.toCommandLine());
    }

    @Test
    public void DoesNotAddRulesDirParameter_IfNull() {
        final ArrayList<Command> capturedCommands = new ArrayList<Command>();
        final ArrayList<Long> capturedTimeouts = new ArrayList<Long>();

        Answer<Integer> captureCommand = new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                capturedCommands.add((Command) invocation.getArguments()[0]);
                capturedTimeouts.add((long) invocation.getArguments()[3]);
                return 0;
            }
        };

        when(this.commandExecutor.execute(any(Command.class), any(StreamConsumer.class), any(StreamConsumer.class), any(long.class))).then(captureCommand);

        this.config.setRulesDir(null);
        this.executorImpl.execute(this.config, Arrays.asList(new String[] { "path/to/file" }));

        Command theCommand = capturedCommands.get(0);
        assertFalse(theCommand.toCommandLine().contains("--rules-dir"));
    }

    @Test
    public void DoesNotAddRulesDirParameter_IfEmptyString() {
        final ArrayList<Command> capturedCommands = new ArrayList<Command>();
        final ArrayList<Long> capturedTimeouts = new ArrayList<Long>();

        Answer<Integer> captureCommand = new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                capturedCommands.add((Command) invocation.getArguments()[0]);
                capturedTimeouts.add((long) invocation.getArguments()[3]);
                return 0;
            }
        };

        when(this.commandExecutor.execute(any(Command.class), any(StreamConsumer.class), any(StreamConsumer.class), any(long.class))).then(captureCommand);

        this.config.setRulesDir("");
        this.executorImpl.execute(this.config, Arrays.asList(new String[] { "path/to/file" }));

        Command theCommand = capturedCommands.get(0);
        assertFalse(theCommand.toCommandLine().contains("--rules-dir"));
    }

    @Test
    public void BatchesExecutions_IfTooManyFilesForCommandLine() {
        List<String> filenames = new ArrayList<String>();
        int currentLength = 0;
        int standardCmdLength = "node path/to/tslint --format json --rules-dir path/to/rules --out path/to/temp --config path/to/config".length();

        String firstBatch = "first batch";
        while (currentLength + 12 < TsLintExecutorImpl.MAX_COMMAND_LENGTH - standardCmdLength) {
            filenames.add(firstBatch);
            currentLength += firstBatch.length() + 1; // 1 for the space
        }
        filenames.add("second batch");

        final ArrayList<Command> capturedCommands = new ArrayList<Command>();
        final ArrayList<Long> capturedTimeouts = new ArrayList<Long>();

        Answer<Integer> captureCommand = new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                capturedCommands.add((Command) invocation.getArguments()[0]);
                capturedTimeouts.add((long) invocation.getArguments()[3]);
                return 0;
            }
        };

        when(this.commandExecutor.execute(any(Command.class), any(StreamConsumer.class), any(StreamConsumer.class), any(long.class))).then(captureCommand);
        this.executorImpl.execute(this.config, filenames);

        assertEquals(2, capturedCommands.size());

        Command theSecondCommand = capturedCommands.get(1);

        assertFalse(theSecondCommand.toCommandLine().contains("first batch"));
        assertTrue(theSecondCommand.toCommandLine().contains("second batch"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void execute_throws_ifNullConfigSupplied() {
        this.executorImpl.execute(null, new ArrayList<String>());
    }

    @Test(expected=IllegalArgumentException.class)
    public void execute_throws_ifNullFileListSupplied() {
        this.executorImpl.execute(this.config, null);
    }

    @Test
    public void useAlreadyExistsTsLintOutput_ifConfigSaysToUseTsLintOutput() throws IOException {
               this.config.setPathToTsLintOutput("path/to/output.json");


        doReturn(mock(BufferedReader.class)).when(this.executorImpl).getBufferedReaderForFile(any(File.class));

        List<String> result = this.executorImpl.execute(this.config, Arrays.asList(new String[]{}));

        assertEquals(1, result.size());
    }
}
