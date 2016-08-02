package musicmaker.message;

public class Broadcaster implements IPublisher, ISubscriber {

	private ISubscriber[] subscribers;
	private int nextSubscriber = 0;

	public Broadcaster(int numSubscribers) {
		subscribers = new ISubscriber[numSubscribers];
	}

	@Override
	public void notify(Message message) {
		for (ISubscriber subscriber : subscribers)
			if (subscriber != null)
				subscriber.notify(message);
	}

	@Override
	public void subscribe(ISubscriber subscriber) {
		subscribers[nextSubscriber++] = subscriber;
	}

	@Override
	public void unsubscribe(ISubscriber subscriber) {
	}

}
