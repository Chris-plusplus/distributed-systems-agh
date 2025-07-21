package agh.distrib;

import agh.distrib.calculator.CalcResult;
import agh.distrib.calculator.CalculatorGrpc.*;
import agh.distrib.calculator.DefaultArgs;
import agh.distrib.calculator.SeqArgs;
import agh.distrib.calculator.SingleInt;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CalculatorImpl extends CalculatorImplBase {
    Logger log = LogManager.getLogger(CalculatorImpl.class);

    @Override
    public void add(SeqArgs request, StreamObserver<CalcResult> responseObserver) {
        StringBuilder sb = new StringBuilder();
        double result = 0.0;
        for(var x : request.getArgsList()){
            sb.append(x);
            sb.append(", ");

            result += x;
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);

        log.info("add({}) = {}", sb.toString(), result);

        responseObserver.onNext(CalcResult.newBuilder().setValue(result).build());
        responseObserver.onCompleted();
    }

    @Override
    public void mul(DefaultArgs request, StreamObserver<CalcResult> responseObserver) {
        double result = request.getArg1() * request.getArg2();

        log.info("mul({}, {}) = {}", request.getArg1(), request.getArg2(), result);

        responseObserver.onNext(CalcResult.newBuilder().setValue(result).build());
        responseObserver.onCompleted();
    }

    @Override
    public void pow(DefaultArgs request, StreamObserver<CalcResult> responseObserver) {
        double result = Math.pow(request.getArg1(), request.getArg2());

        log.info("pow({}, {}) = {}", request.getArg1(), request.getArg2(), result);

        responseObserver.onNext(CalcResult.newBuilder().setValue(result).build());
        responseObserver.onCompleted();
    }

    @Override
    public void fib(SingleInt request, StreamObserver<SingleInt> responseObserver) {
        long n = request.getValue();
        long a = 0, b = 1;

        for(long i = 0; i != n; ++i){
            responseObserver.onNext(SingleInt
                    .newBuilder()
                    .setValue(a)
                    .build());
            log.info("fib({})[{}] = {}", n, i, a);

            long next = a + b;
            a = b;
            b = next;
        }
        responseObserver.onCompleted();
    }
}
