package messageQueue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

// the following 2 dependencies are provided by activemq jar file which I didn't include since,
// the file size exceeds the limit in courseweb.
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Receiver implements MessageListener {
	private ConnectionFactory factory = null;
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageConsumer consumer = null;

	public Receiver() {

	}

	public void receiveMessage() {
		try {
			// Getting JMS connection from the server
	        // default broker URL is : tcp://localhost:61616
			factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
			connection = factory.createConnection();
			connection = factory.createConnection();
			
			connection.start();
			
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue("SAMPLEQUEUE");
	        
			//creating a message consumer to receive messages from a destination
			consumer = session.createConsumer(destination);
	        
			//calling the receiving method to receive a message and assign it to the variable called message
			Message message = consumer.receive();

	        //extracting the message content and cast to the appropriate message type.
			if (message instanceof TextMessage) {
				TextMessage text = (TextMessage) message;
				System.out.println("Message is : " + text.getText());
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	//getting a message from the JMS queue
	public static void main(String[] args) throws JMSException, InterruptedException {
		
	    //creating an object from receiver class and calling the receieveMessage method inside it
		Receiver receiver = new Receiver();

		// registering to be notified if a message is there.
		receiver.registerListener();
		
        // TODO: Comment this line => uncomment if sync messaging is needed.
		//receiver.receiveMessage();
		System.out.println("print");
		int i=0;
		for(;;) {
			Thread.sleep(1000);
			i++;
			System.out.print("\r" + i);

		}
	}
	
	private void registerListener() throws JMSException {
		
		factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
		connection = factory.createConnection();
		connection = factory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createQueue("SAMPLEQUEUE");
        //creating a message consumer to receive messages from a destination
		consumer = session.createConsumer(destination);
		
		consumer.setMessageListener(this); 
		
	}
	
	// following method will act as a callback method to make the
	// communication async.
	@Override
	public void onMessage(Message message) {
		// without casting, we can extract the text within the message.
		if (message instanceof TextMessage) {
			TextMessage txtMsg = (TextMessage)message;	
    		
			try {
				System.out.println(txtMsg.getText());
			} catch (JMSException jmse) {
		    		System.err.println("Unable to retrive message!");
		    		//e.printStackTrace();
			}
		}
		else {
			System.err.println("Received message is not a TextMessage.");
		}
	}
}