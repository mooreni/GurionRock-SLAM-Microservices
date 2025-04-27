

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.concurrent.ConcurrentLinkedQueue;
import bgu.spl.mics.Future;

import bgu.spl.mics.MicroService;



public class MessageBusImplTest {
    
    MessageBusImpl messageBusImpl;
    Event<Object> mockEvent;
    MicroService mockMicroService;
    Future<Object> mockFuture;

    @BeforeEach
    public void setUp() {
        messageBusImpl = MessageBusImpl.getInstance();
        mockEvent = mock(Event.class);
        mockMicroService = mock(MicroService.class);

        messageBusImpl.getEventsSubs().put((Class<? extends Event<Object>>) mockEvent.getClass(), new ConcurrentLinkedQueue<MicroService>());
    }

    @Test
    public void testSubscribeEvent() {
        // test if the event is added to the queue
        messageBusImpl.subscribeEvent((Class<? extends Event<Object>>) mockEvent.getClass(), mockMicroService);
        assertTrue(messageBusImpl.getEventsSubs().get(mockEvent.getClass()).contains(mockMicroService));
    }
   
    @Test
    public void testComplete() {
        mockFuture = mock(Future.class);
        messageBusImpl.getEventsFutures().put((Event<?>)mockEvent, (Future<?>)mockFuture);
        assertTrue(messageBusImpl.getEventsFutures().containsKey(mockEvent));
        // test if the event is removed from the map
        // test if the future is resolved
        messageBusImpl.complete(mockEvent, new Object());
        verify(mockFuture).resolve(any());
        assertFalse(messageBusImpl.getEventsFutures().containsKey(mockEvent));
    }

    @Test
    public void testRegister() {
        // test if the microService is added to the map
        messageBusImpl.register(mockMicroService);
        assertTrue(messageBusImpl.getRegisteredServices().containsKey(mockMicroService));
    }

}
