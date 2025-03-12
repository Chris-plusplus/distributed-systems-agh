import socket;

serverIP = "127.0.0.1"
serverPort = 9008
msg = "żółta gęś"

print('PYTHON UDP CLIENT')
client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client.bind((serverIP, serverPort + 1))
msg_bytes = (300).to_bytes(4, byteorder='little')

client.sendto(msg_bytes, (serverIP, serverPort))

recv_bytes = client.recv(4)
print(f'{int.from_bytes(recv_bytes, byteorder='little')}')




