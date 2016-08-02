package musicmaker.message;

import java.util.ArrayList;

public class SubscriberList implements IPublisher, ISubscriber {

	private ArrayList<ISubscriber> subscribers = new ArrayList<ISubscriber>();

	public SubscriberList() {
	}

	public int count() {
		return subscribers.size();
	}

	@Override
	public void notify(Message message) {
		for (ISubscriber subscriber : subscribers)
			if (subscriber != null)
				subscriber.notify(message);
	}

	public void notify(int subscriberNumber, Message message) {
		ISubscriber subscriber = subscribers.get(subscriberNumber);
		if (subscriber != null)
			subscriber.notify(message);
	}

	@Override
	public void subscribe(ISubscriber subscriber) {
		subscribers.add(subscriber);
	}

	@Override
	public void unsubscribe(ISubscriber subscriber) {
		subscribers.remove(subscriber);
	}

}
