from google.protobuf.descriptor_pool import DescriptorPool
from google.protobuf.descriptor import *
import grpc
from grpc_reflection.v1alpha.proto_reflection_descriptor_database import (
    ProtoReflectionDescriptorDatabase,
)
from google.protobuf import message_factory

with grpc.insecure_channel('127.0.0.1:10000') as channel:
    reflDB = ProtoReflectionDescriptorDatabase(channel)

    serviceName = next(filter(lambda s: 'reflection' not in s, reflDB.get_services()))
    descPool = DescriptorPool(reflDB)
    serviceDesc = descPool.FindServiceByName(serviceName)
    assert isinstance(serviceDesc, ServiceDescriptor)

    addDesc = serviceDesc.FindMethodByName("add")
    mulDesc = serviceDesc.FindMethodByName("mul")
    powDesc = serviceDesc.FindMethodByName("pow")
    fibDesc = serviceDesc.FindMethodByName("fib")

    mulPowInputT = addDesc.input_type
    mulPowRequestDesc = descPool.FindMessageTypeByName(mulPowInputT.full_name)
    addInputT = powDesc.input_type
    addRequestDesc = descPool.FindMessageTypeByName(addInputT.full_name)
    fibInputT = fibDesc.input_type
    fibRequestDesc = descPool.FindMessageTypeByName(fibInputT.full_name)

    DefaultMessageT = message_factory.GetMessageClass(addRequestDesc)
    SeqMessageT = message_factory.GetMessageClass(mulPowRequestDesc)
    SingleIntT = message_factory.GetMessageClass(fibRequestDesc)
    
    outputT = powDesc.output_type
    responseDesc = descPool.FindMessageTypeByName(outputT.full_name)
    ResponseT = message_factory.GetMessageClass(responseDesc)

    callAdd = channel.unary_unary(f'/{serviceName}/{addDesc.name}')
    callMul = channel.unary_unary(f'/{serviceName}/{mulDesc.name}')
    callPow = channel.unary_unary(f'/{serviceName}/{powDesc.name}')
    callFib = channel.unary_stream(f'/{serviceName}/{fibDesc.name}')

    defaultCallables = {
        'mul': callMul,
        'pow': callPow
    }

    print("Available commands:")
    print("    quit")
    print("    add numberSeq - computes sum(numberSeq)")
    print("    mul num1 num2 - computes num1 * num2")
    print("    pow num1 num2 - computes num1 ** num2")
    print("    fib N - computes first N numbers from fibbonaci sequence")

    def unpack(response, ResponseType = ResponseT):
        deserialized = ResponseType()
        deserialized.ParseFromString(response)
        return deserialized.value

    while True:
        prompt = input("> ").split()
        if len(prompt) == 0:
            continue
        cmd = prompt[0]
        argCount = len(prompt) - 1
        if cmd == 'quit':
            break
        elif cmd == 'add':
            args = SeqMessageT()
            for arg in prompt[1:]:
                try:
                    args.args.append(float(arg))        
                except ValueError as e:
                    print(e)
                    continue
            print(f'result: {unpack(callAdd(args.SerializeToString()))}')
        elif cmd in defaultCallables:
            if argCount < 2:
                print(f'required 2 arguments, got {argCount}')
                continue
            elif argCount > 2:
                print(f'required 2 arguments, got {argCount}, ignoring tail')
            args = DefaultMessageT()
            try:
                args.arg1 = float(prompt[1])
                args.arg2 = float(prompt[2])
            except ValueError as e:
                print(e)
                continue
            print(f'result = {unpack(defaultCallables[cmd](args.SerializeToString()))}')
        elif cmd == 'fib':
            if argCount < 1:
                print(f'required 1 argument, got {argCount}')
                continue
            elif argCount > 1:
                print(f'required 1 argument, got {argCount}, ignoring tail')
            arg = SingleIntT()
            try:
                arg.value = int(prompt[1])
            except ValueError as e:
                print(e)
                continue
            resultStream = callFib(arg.SerializeToString())
            for i, result in enumerate(resultStream):
                print(f'fib({int(prompt[1])})[{i}] = {unpack(result, SingleIntT)}')
        else:
            print('unknown operation')