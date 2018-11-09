'''
Simple Client that send a rquest and print the reply from the Server
@author Lorenzo Pratesi Mariti, 5793319
'''
import socket

serverName = 'localhost'
serverPort = 12000

print("Creating socket...")
clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

print("Connetting to server...")
clientSocket.connect((serverName, serverPort))
print("REQUEST")

request = "GET /get_directory/root_file_html.html HTTP/1.0\r\nConnection: Keep-Alive\r\nUser-Agent: myBrowser\r\n\r\n"
print(request)
print("Sending...")
clientSocket.send(request.encode('ascii'))
print("Waiting reply...")
reply = clientSocket.recv(2048)
print("From Server: {}".format(reply.decode('ascii')))
clientSocket.close()
