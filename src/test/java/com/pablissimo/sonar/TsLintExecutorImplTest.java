package com.pablissimo.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;

import edu.emory.mathcs.backport.java.util.Arrays;

public class TsLintExecutorImplTest {
    TsLintExecutorImpl executorImpl;
    CommandExecutor commandExecutor;
    
    @Before
    public void setUp() throws Exception {
        this.commandExecutor = mock(CommandExecutor.class);
        this.executorImpl = spy(new TsLintExecutorImpl());
        when(this.executorImpl.createExecutor()).thenReturn(this.commandExecutor);
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
        this.executorImpl.execute("path/to/tslint", "path/to/config", "path/to/rules", Arrays.asList(new String[] { "path/to/file", "path/to/another" }), 40000);
        
        assertEquals(1, capturedCommands.size());
        
        Command theCommand = capturedCommands.get(0);
        long theTimeout = capturedTimeouts.get(0);
        
        assertEquals("node path/to/tslint --format json --rules-dir path/to/rules --config path/to/config path/to/file path/to/another", theCommand.toCommandLine());
        // Expect one timeout period per file processed
        assertEquals(2 * 40000, theTimeout);        
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
        this.executorImpl.execute("path/to/tslint", "path/to/config", null, Arrays.asList(new String[] { "path/to/file" }), 40000);
        
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
        this.executorImpl.execute("path/to/tslint", "path/to/config", "", Arrays.asList(new String[] { "path/to/file" }), 40000);
        
        Command theCommand = capturedCommands.get(0);
        assertFalse(theCommand.toCommandLine().contains("--rules-dir"));
    }
    
    @Test
    public void BatchesExecutions_IfTooManyFilesForCommandLine() {
        List<String> filenames = new ArrayList<String>();
        int currentLength = 0;
        int standardCmdLength = "node path/to/tslint --format json --rules-dir path/to/rules --config path/to/config".length();
        
        String firstBatch = "first batch";
        while (currentLength + 12 < TsLintExecutorImpl.MAX_COMMAND_LENGTH - standardCmdLength) {
            filenames.add(firstBatch);
            currentLength += firstBatch.length() + 1;
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
        this.executorImpl.execute("path/to/tslint", "path/to/config", "path/to/rules", filenames, 40000);
        
        assertEquals(2, capturedCommands.size());
        
        Command theSecondCommand = capturedCommands.get(1);
        
        assertFalse(theSecondCommand.toCommandLine().contains("first batch"));
        assertTrue(theSecondCommand.toCommandLine().contains("second batch"));
    }
}
