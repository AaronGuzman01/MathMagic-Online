GitHub Repository: 
https://github.com/AaronGuzman01/MatheMagic-Online

INSTRUCTIONS:
To run this program successfully you have to first start the server by running the 
MatheMagicServer.java file. Once the server is running, which can be verified by
looking at the server's command console where a server started message should appear 
you then start the MatheMagicClient.java file and make ensure that the command console 
on the client side has established a connection with the server (A message will display 
in the console indicating a successful connection).


COMMANDS IMPLEMENTED:
This project implements the following server Protocols:

1. LOGOUT - This request will verify the clients' identity and indicate a successful or 
unsuccessful login attempt. A list of authorized users can be found in the login.txt file.
This command follows the following format: LOGIN username password.

2. SOLVE - This request will receive a geometry problem from the client and return a 
solution or error message to the client. These interactions will also be recorded for 
each user in a corresponding file. This command can take input for a circle or rectangle 
problem using a flag in the request.
This command follows the following formats (n indicates numerical value): 
SOLVE -c n
SOLVE -r n
SOLVE -r n n

3. LIST - This request will return a list of interactions (from the SOLVE request)
that the user has had with the server. This request can also be used with a special 
flag format to return all interactions from all users. This flag format, however, will
require the user to the root user, which this protocol will verify.
This command follows the following formats:
LIST
LIST -all

4. SHUTDOWN - This request will close the current connection with the client and will 
close the server down.
This command follows the following format: SHUTDOWN

5. LOGOUT - This request will logout the user and end the user's connection with the server.
This server also enables the reconnection of a new client, so after this command is executed
the server will wait for the next client to connect.
This command follows the following format: LOGOUT

PROBLEMS & BUGS:
The only problem I found in my implementation was that when you entered an invalid character 
after the command it would not work. For example if I entered SHUTDOWN 5 or LOGOUT  3, it 
would not execute the command. I accounted for whitespaces so whitespaces after a correctly
formatted command would not affect a command but an extra character would. This also happened
when I entered an extra number in the SOLVE command, so commands like SOLVE -c 4 4 & SOLVE -r 5 5 5
would not work. This really isn't a bug since the protocols account for the correct format,
but I do feel that this was a limitation in my implementation.


SAMPLE RUN:
First Client:

MathMagic connection established
Start entering a command:

LOGIN qiang qiang22
SUCCESS 

SOLVE -c 6
Circle’s circumference is 37.70 and area is 113.10

SOLVE -r 5
Rectangle’s perimeter is 20.00 and area is 25.00

SOLVE -r 2 6
Rectangle’s perimeter is 16.00 and area is 12.00

LIST
qiang:
	radius 6: Circle’s circumference is 37.70 and area is 113.10
	sides 5 5: Rectangle’s perimeter is 20.00 and area is 25.00
	sides 2 6: Rectangle’s perimeter is 16.00 and area is 12.0

LOGOUT
200 OK 


Second Client (New client after logout command):

MathMagic connection established
Start entering a command:

LOGIN root root22
SUCCESS 

SOLVE -c 56
Circle’s circumference is 351.86 and area is 9852.03

SOLVE -r 350 34
Rectangle’s perimeter is 768.00 and area is 11900.00

LIST -all
root:
	sides 5 5: Rectangle’s perimeter is 20.00 and area is 25.00
	radius 56: Circle’s circumference is 351.86 and area is 9852.03
	sides 350 34: Rectangle’s perimeter is 768.00 and area is 11900.0
john:
	radius 2: Circle’s circumference is 12.57 and area is 12.57
	radius 4: Circle’s circumference is 25.13 and area is 50.2
sally:
	No interactions yet
qiang:
	radius 6: Circle’s circumference is 37.70 and area is 113.10
	sides 5 5: Rectangle’s perimeter is 20.00 and area is 25.00
	sides 2 6: Rectangle’s perimeter is 16.00 and area is 12.0

SHUTDOWN
200 OK 



