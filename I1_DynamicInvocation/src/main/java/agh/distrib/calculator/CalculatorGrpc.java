package agh.distrib.calculator;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.71.0)",
    comments = "Source: calculator.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CalculatorGrpc {

  private CalculatorGrpc() {}

  public static final java.lang.String SERVICE_NAME = "calculator.Calculator";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<agh.distrib.calculator.SeqArgs,
      agh.distrib.calculator.CalcResult> getAddMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "add",
      requestType = agh.distrib.calculator.SeqArgs.class,
      responseType = agh.distrib.calculator.CalcResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<agh.distrib.calculator.SeqArgs,
      agh.distrib.calculator.CalcResult> getAddMethod() {
    io.grpc.MethodDescriptor<agh.distrib.calculator.SeqArgs, agh.distrib.calculator.CalcResult> getAddMethod;
    if ((getAddMethod = CalculatorGrpc.getAddMethod) == null) {
      synchronized (CalculatorGrpc.class) {
        if ((getAddMethod = CalculatorGrpc.getAddMethod) == null) {
          CalculatorGrpc.getAddMethod = getAddMethod =
              io.grpc.MethodDescriptor.<agh.distrib.calculator.SeqArgs, agh.distrib.calculator.CalcResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "add"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  agh.distrib.calculator.SeqArgs.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  agh.distrib.calculator.CalcResult.getDefaultInstance()))
              .setSchemaDescriptor(new CalculatorMethodDescriptorSupplier("add"))
              .build();
        }
      }
    }
    return getAddMethod;
  }

  private static volatile io.grpc.MethodDescriptor<agh.distrib.calculator.DefaultArgs,
      agh.distrib.calculator.CalcResult> getMulMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "mul",
      requestType = agh.distrib.calculator.DefaultArgs.class,
      responseType = agh.distrib.calculator.CalcResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<agh.distrib.calculator.DefaultArgs,
      agh.distrib.calculator.CalcResult> getMulMethod() {
    io.grpc.MethodDescriptor<agh.distrib.calculator.DefaultArgs, agh.distrib.calculator.CalcResult> getMulMethod;
    if ((getMulMethod = CalculatorGrpc.getMulMethod) == null) {
      synchronized (CalculatorGrpc.class) {
        if ((getMulMethod = CalculatorGrpc.getMulMethod) == null) {
          CalculatorGrpc.getMulMethod = getMulMethod =
              io.grpc.MethodDescriptor.<agh.distrib.calculator.DefaultArgs, agh.distrib.calculator.CalcResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "mul"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  agh.distrib.calculator.DefaultArgs.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  agh.distrib.calculator.CalcResult.getDefaultInstance()))
              .setSchemaDescriptor(new CalculatorMethodDescriptorSupplier("mul"))
              .build();
        }
      }
    }
    return getMulMethod;
  }

  private static volatile io.grpc.MethodDescriptor<agh.distrib.calculator.DefaultArgs,
      agh.distrib.calculator.CalcResult> getPowMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "pow",
      requestType = agh.distrib.calculator.DefaultArgs.class,
      responseType = agh.distrib.calculator.CalcResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<agh.distrib.calculator.DefaultArgs,
      agh.distrib.calculator.CalcResult> getPowMethod() {
    io.grpc.MethodDescriptor<agh.distrib.calculator.DefaultArgs, agh.distrib.calculator.CalcResult> getPowMethod;
    if ((getPowMethod = CalculatorGrpc.getPowMethod) == null) {
      synchronized (CalculatorGrpc.class) {
        if ((getPowMethod = CalculatorGrpc.getPowMethod) == null) {
          CalculatorGrpc.getPowMethod = getPowMethod =
              io.grpc.MethodDescriptor.<agh.distrib.calculator.DefaultArgs, agh.distrib.calculator.CalcResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "pow"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  agh.distrib.calculator.DefaultArgs.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  agh.distrib.calculator.CalcResult.getDefaultInstance()))
              .setSchemaDescriptor(new CalculatorMethodDescriptorSupplier("pow"))
              .build();
        }
      }
    }
    return getPowMethod;
  }

  private static volatile io.grpc.MethodDescriptor<agh.distrib.calculator.SingleInt,
      agh.distrib.calculator.SingleInt> getFibMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "fib",
      requestType = agh.distrib.calculator.SingleInt.class,
      responseType = agh.distrib.calculator.SingleInt.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<agh.distrib.calculator.SingleInt,
      agh.distrib.calculator.SingleInt> getFibMethod() {
    io.grpc.MethodDescriptor<agh.distrib.calculator.SingleInt, agh.distrib.calculator.SingleInt> getFibMethod;
    if ((getFibMethod = CalculatorGrpc.getFibMethod) == null) {
      synchronized (CalculatorGrpc.class) {
        if ((getFibMethod = CalculatorGrpc.getFibMethod) == null) {
          CalculatorGrpc.getFibMethod = getFibMethod =
              io.grpc.MethodDescriptor.<agh.distrib.calculator.SingleInt, agh.distrib.calculator.SingleInt>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "fib"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  agh.distrib.calculator.SingleInt.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  agh.distrib.calculator.SingleInt.getDefaultInstance()))
              .setSchemaDescriptor(new CalculatorMethodDescriptorSupplier("fib"))
              .build();
        }
      }
    }
    return getFibMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CalculatorStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CalculatorStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CalculatorStub>() {
        @java.lang.Override
        public CalculatorStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CalculatorStub(channel, callOptions);
        }
      };
    return CalculatorStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static CalculatorBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CalculatorBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CalculatorBlockingV2Stub>() {
        @java.lang.Override
        public CalculatorBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CalculatorBlockingV2Stub(channel, callOptions);
        }
      };
    return CalculatorBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CalculatorBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CalculatorBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CalculatorBlockingStub>() {
        @java.lang.Override
        public CalculatorBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CalculatorBlockingStub(channel, callOptions);
        }
      };
    return CalculatorBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CalculatorFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CalculatorFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CalculatorFutureStub>() {
        @java.lang.Override
        public CalculatorFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CalculatorFutureStub(channel, callOptions);
        }
      };
    return CalculatorFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void add(agh.distrib.calculator.SeqArgs request,
        io.grpc.stub.StreamObserver<agh.distrib.calculator.CalcResult> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddMethod(), responseObserver);
    }

    /**
     */
    default void mul(agh.distrib.calculator.DefaultArgs request,
        io.grpc.stub.StreamObserver<agh.distrib.calculator.CalcResult> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getMulMethod(), responseObserver);
    }

    /**
     */
    default void pow(agh.distrib.calculator.DefaultArgs request,
        io.grpc.stub.StreamObserver<agh.distrib.calculator.CalcResult> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPowMethod(), responseObserver);
    }

    /**
     */
    default void fib(agh.distrib.calculator.SingleInt request,
        io.grpc.stub.StreamObserver<agh.distrib.calculator.SingleInt> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFibMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service Calculator.
   */
  public static abstract class CalculatorImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return CalculatorGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service Calculator.
   */
  public static final class CalculatorStub
      extends io.grpc.stub.AbstractAsyncStub<CalculatorStub> {
    private CalculatorStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CalculatorStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CalculatorStub(channel, callOptions);
    }

    /**
     */
    public void add(agh.distrib.calculator.SeqArgs request,
        io.grpc.stub.StreamObserver<agh.distrib.calculator.CalcResult> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void mul(agh.distrib.calculator.DefaultArgs request,
        io.grpc.stub.StreamObserver<agh.distrib.calculator.CalcResult> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getMulMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void pow(agh.distrib.calculator.DefaultArgs request,
        io.grpc.stub.StreamObserver<agh.distrib.calculator.CalcResult> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPowMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fib(agh.distrib.calculator.SingleInt request,
        io.grpc.stub.StreamObserver<agh.distrib.calculator.SingleInt> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getFibMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service Calculator.
   */
  public static final class CalculatorBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<CalculatorBlockingV2Stub> {
    private CalculatorBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CalculatorBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CalculatorBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public agh.distrib.calculator.CalcResult add(agh.distrib.calculator.SeqArgs request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddMethod(), getCallOptions(), request);
    }

    /**
     */
    public agh.distrib.calculator.CalcResult mul(agh.distrib.calculator.DefaultArgs request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getMulMethod(), getCallOptions(), request);
    }

    /**
     */
    public agh.distrib.calculator.CalcResult pow(agh.distrib.calculator.DefaultArgs request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPowMethod(), getCallOptions(), request);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, agh.distrib.calculator.SingleInt>
        fib(agh.distrib.calculator.SingleInt request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getFibMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service Calculator.
   */
  public static final class CalculatorBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CalculatorBlockingStub> {
    private CalculatorBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CalculatorBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CalculatorBlockingStub(channel, callOptions);
    }

    /**
     */
    public agh.distrib.calculator.CalcResult add(agh.distrib.calculator.SeqArgs request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddMethod(), getCallOptions(), request);
    }

    /**
     */
    public agh.distrib.calculator.CalcResult mul(agh.distrib.calculator.DefaultArgs request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getMulMethod(), getCallOptions(), request);
    }

    /**
     */
    public agh.distrib.calculator.CalcResult pow(agh.distrib.calculator.DefaultArgs request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPowMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<agh.distrib.calculator.SingleInt> fib(
        agh.distrib.calculator.SingleInt request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getFibMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service Calculator.
   */
  public static final class CalculatorFutureStub
      extends io.grpc.stub.AbstractFutureStub<CalculatorFutureStub> {
    private CalculatorFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CalculatorFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CalculatorFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<agh.distrib.calculator.CalcResult> add(
        agh.distrib.calculator.SeqArgs request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<agh.distrib.calculator.CalcResult> mul(
        agh.distrib.calculator.DefaultArgs request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getMulMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<agh.distrib.calculator.CalcResult> pow(
        agh.distrib.calculator.DefaultArgs request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPowMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD = 0;
  private static final int METHODID_MUL = 1;
  private static final int METHODID_POW = 2;
  private static final int METHODID_FIB = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ADD:
          serviceImpl.add((agh.distrib.calculator.SeqArgs) request,
              (io.grpc.stub.StreamObserver<agh.distrib.calculator.CalcResult>) responseObserver);
          break;
        case METHODID_MUL:
          serviceImpl.mul((agh.distrib.calculator.DefaultArgs) request,
              (io.grpc.stub.StreamObserver<agh.distrib.calculator.CalcResult>) responseObserver);
          break;
        case METHODID_POW:
          serviceImpl.pow((agh.distrib.calculator.DefaultArgs) request,
              (io.grpc.stub.StreamObserver<agh.distrib.calculator.CalcResult>) responseObserver);
          break;
        case METHODID_FIB:
          serviceImpl.fib((agh.distrib.calculator.SingleInt) request,
              (io.grpc.stub.StreamObserver<agh.distrib.calculator.SingleInt>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getAddMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              agh.distrib.calculator.SeqArgs,
              agh.distrib.calculator.CalcResult>(
                service, METHODID_ADD)))
        .addMethod(
          getMulMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              agh.distrib.calculator.DefaultArgs,
              agh.distrib.calculator.CalcResult>(
                service, METHODID_MUL)))
        .addMethod(
          getPowMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              agh.distrib.calculator.DefaultArgs,
              agh.distrib.calculator.CalcResult>(
                service, METHODID_POW)))
        .addMethod(
          getFibMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              agh.distrib.calculator.SingleInt,
              agh.distrib.calculator.SingleInt>(
                service, METHODID_FIB)))
        .build();
  }

  private static abstract class CalculatorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CalculatorBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return agh.distrib.calculator.CalcProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Calculator");
    }
  }

  private static final class CalculatorFileDescriptorSupplier
      extends CalculatorBaseDescriptorSupplier {
    CalculatorFileDescriptorSupplier() {}
  }

  private static final class CalculatorMethodDescriptorSupplier
      extends CalculatorBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    CalculatorMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CalculatorGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CalculatorFileDescriptorSupplier())
              .addMethod(getAddMethod())
              .addMethod(getMulMethod())
              .addMethod(getPowMethod())
              .addMethod(getFibMethod())
              .build();
        }
      }
    }
    return result;
  }
}
