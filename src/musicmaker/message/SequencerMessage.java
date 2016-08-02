package musicmaker.message;

public class SequencerMessage extends Message {

	private int stepNumber;

	public SequencerMessage(IPublisher publisher, int stepNumber) {
		super(publisher);
		stepNumber = stepNumber;
	}

	public int getStepNumber() {
		return stepNumber;
	}

}
