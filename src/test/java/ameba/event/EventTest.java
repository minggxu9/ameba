package ameba.event;

import ameba.core.AddOn;
import ameba.core.Application;
import ameba.lib.Akka;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author icode
 */
public class EventTest {

    private static final Logger logger = LoggerFactory.getLogger(EventTest.class);

    @Test
    public void publish() {

        AddOn addOn = new Akka.AddOn();
        addOn.setup(new Application());

        EventBus eventBus = EventBus.createMix();

        for (int i = 0; i < 10; i++) {
            eventBus.subscribe(TestEvent.class, new AsyncListener<TestEvent>() {
                @Override
                public void onReceive(TestEvent event) {
                    logger.info("async receive message : {}", event.message);
                }
            });

            eventBus.subscribe(TestEvent1.class, new AsyncListener<TestEvent1>() {
                @Override
                public void onReceive(TestEvent1 event) {
                    logger.info("TestEvent1 async receive message : {}", event.message);
                }
            });
        }
        for (int i = 0; i < 5; i++) {
            final int finalI = i;
            eventBus.subscribe(TestEvent.class, new Listener<TestEvent>() {
                @Override
                public void onReceive(TestEvent event) {
                    logger.info("receive message {} : {}", finalI, event.message);
                }
            });

            eventBus.subscribe(TestEvent1.class, new Listener<TestEvent1>() {
                @Override
                public void onReceive(TestEvent1 event) {
                    logger.info("TestEvent1 receive message {} : {}", finalI, event.message);
                }
            });
        }

        logger.info("publish message ..");
        for (int i = 0; i < 10; i++) {
            eventBus.publish(new TestEvent("message: " + i));
            eventBus.publish(new TestEvent1("message: " + i));
        }

        try {
            synchronized (this) {
                wait(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("remove all event and publish message ..");
        eventBus.unsubscribe(TestEvent.class);
        eventBus.unsubscribe(TestEvent1.class);
        for (int i = 0; i < 10; i++) {
            eventBus.publish(new TestEvent("message: " + i));
            eventBus.publish(new TestEvent1("message: " + i));
        }
    }

    public static class TestEvent implements Event {
        public String message;

        public TestEvent(String message) {
            this.message = message;
        }
    }

    public static class TestEvent1 implements Event {
        public String message;

        public TestEvent1(String message) {
            this.message = message;
        }
    }
}
