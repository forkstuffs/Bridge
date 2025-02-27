package net.pixteria.bridge.future;

import org.redisson.api.RedissonClient;

import java.time.Duration;

final class Example {

    public static void main(String[] args) {
        final RedissonClient redis = null;
        final var pipeline = new Pipeline(redis, "bridge-response-topic");
        pipeline.register("test-event", TestEvent.Request.class, event -> {
            assert event.test.equals("ping");
            event.reply(new TestEvent.Response("pong"));
        });
        pipeline.call("test-event", new TestEvent.Request("ping"), Duration.ofSeconds(1L))
            .thenAccept(event -> {
                assert event.test2.equals("pong");
            });
    }

    private static final class TestEvent {
        private static final class Request extends EventResponsible<Response> {
            private final String test;

            public Request(String test) {
                this.test = test;
            }
        }

        private static final class Response {
            private final String test2;

            public Response(String test2) {
                this.test2 = test2;
            }
        }
    }
}
