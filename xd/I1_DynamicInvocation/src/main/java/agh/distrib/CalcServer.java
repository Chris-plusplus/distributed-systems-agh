package agh.distrib;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.util.concurrent.Executors;

public class CalcServer {
    static Logger logger = LogManager.getLogger(CalcServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        Configurator.setRootLevel(Level.INFO);


        Server server = ServerBuilder
                .forPort(10000)
                .executor(Executors.newFixedThreadPool(10))
                .addService(ProtoReflectionService.newInstance())
                .addService(new CalculatorImpl())
                .build()
                .start();

        logger.info("Server started at port {}", server.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Shutting down gRPC server");
                server.shutdown();
            }
        });

        server.awaitTermination();
    }
}