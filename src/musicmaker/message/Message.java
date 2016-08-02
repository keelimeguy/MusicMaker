package musicmaker.message;

public class Message {
	private IPublisher publisher;

	public Message(IPublisher publisher) {
		publisher = publisher;
	}

	public IPublisher getPublisher() {
		return publisher;
	}
}
