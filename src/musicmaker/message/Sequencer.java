package musicmaker.message;

public class Sequencer implements IPublisher, ISubscriber {
	private SubscriberList subscribers;
	private int nextNotify = 0;

	public Sequencer(int numSubscribers) {
		subscribers = new SubscriberList();
	}

	public int getStepNumber() {
		return nextNotify;
	}

	@Override
	public void notify(Message message) {
		if (nextNotify == subscribers.count())
			nextNotify = 0;
		SequencerMessage msg = new SequencerMessage(this, nextNotify);
		// System.out.println("Sequencer.notify " + nextNotify);
		subscribers.notify(nextNotify++, msg);
	}

	public void reset() {
		nextNotify = 0;
	}

	public void setStep(int stepNumber, ISubscriber subscriber) {
		subscribers.subscribe(subscriber);
	}

	@Override
	public void subscribe(ISubscriber subscriber) {
		subscribers.subscribe(subscriber);
	}

	@Override
	public void unsubscribe(ISubscriber subscriber) {
		subscribers.unsubscribe(subscriber);
	}

}
