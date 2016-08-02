package musicmaker.theory;

import java.util.Timer;
import java.util.TimerTask;

import musicmaker.message.IPublisher;
import musicmaker.message.ISubscriber;
import musicmaker.message.Message;
import musicmaker.message.SubscriberList;

public class Metronome extends TimerTask implements IPublisher {

   private SubscriberList subscribers = new SubscriberList();
   private Message msg = new Message(this); // create one message to keep re-using
   private Timer timer;
   private long delay = 250;
   private int numTicksLeft = 0;

   public Metronome() {
   }

   @Override
   public void run() {
      tick();
   }

   public void setDelay(long delay) {
      this.delay = delay;
   }

   // Starts for an unbounded number of ticks
   public void start() {
      this.start(-1);
   }

   public void start(int numTicks) {
      if (timer == null) {
         numTicksLeft = numTicks;
         timer = new Timer();
         MetronomeTask metronomeTask = new MetronomeTask();
         timer.schedule(metronomeTask, 0, delay);
      }
   }

   public void stop() {
      if (timer != null) {
         timer.cancel();
         timer = null;
      }
   }

   public void subscribe(ISubscriber subscriber) {
      subscribers.subscribe(subscriber);
   }

   public void tick() {
      if (numTicksLeft == 0)
         this.stop();
      else {
         // System.out.println("Metronome.tick(" + numTicksLeft + ")");
         subscribers.notify(msg);
         if (numTicksLeft > 0)
            numTicksLeft--;
      }
   }

   public void unsubscribe(ISubscriber subscriber) {
      subscribers.unsubscribe(subscriber);
   }

   private class MetronomeTask extends TimerTask {

      @Override
      public void run() {
         Metronome.this.tick();
      }

   }

}
