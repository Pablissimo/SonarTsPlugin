package com.pablissimo.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;

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
    public void executesCommandWithCorrectArguments() {
        final ArrayList<Command> capturedCommands = new ArrayList<Command>();
        
        Answer<Integer> captureCommand = new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                capturedCommands.add((Command) invocation.getArguments()[0]);
                return 0;
            }
        };
        
        when(this.commandExecutor.execute(any(Command.class), any(StreamConsumer.class), any(StreamConsumer.class), any(long.class))).then(captureCommand);
        this.executorImpl.execute("path/to/tslint", "path/to/config", "path/to/file");
        
        assertEquals(1, capturedCommands.size());
        
        Command theCommand = capturedCommands.get(0);
        
        assertEquals("node path/to/tslint --format json --config path/to/config path/to/file", theCommand.toCommandLine());
    }
}
